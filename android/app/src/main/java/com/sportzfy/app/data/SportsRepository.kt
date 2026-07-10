package com.sportzfy.app.data

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Executors
import java.util.concurrent.Future

object SportsRepository {

    private val executor = Executors.newFixedThreadPool(8)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val LIVE_STATUSES = setOf(
        "STATUS_IN_PROGRESS", "STATUS_HALFTIME", "STATUS_END_PERIOD",
        "STATUS_FIRST_HALF", "STATUS_SECOND_HALF", "STATUS_OVERTIME"
    )

    /**
     * Fetch FIFA World Cup 2026 schedule — FAST parallel strategy:
     * 1. First try a single ESPN range request (today → +13 days) — ONE network call
     * 2. If the range returns <2 events, fall back to 7 parallel daily calls
     */
    fun fetchWorldCup(callback: (List<SportEvent>?) -> Unit) {
        executor.execute {
            val events = fetchRange() ?: fetchParallel()
            events?.let {
                val sorted = it.sortedWith(compareBy({ statusOrder(it.status) }, { it.date }))
                mainHandler.post { callback(sorted) }
            } ?: mainHandler.post { callback(null) }
        }
    }

    // ── Strategy 1: single range request ────────────────────────────
    private fun fetchRange(): List<SportEvent>? {
        return try {
            val today = calStr(0)
            val end   = calStr(13)
            val url = URL(
                "https://site.api.espn.com/apis/site/v2/sports/soccer/fifa.world/scoreboard" +
                "?dates=$today-$end&limit=50"
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000
            conn.readTimeout    = 6000
            conn.setRequestProperty("User-Agent", "Sportzfy/9.0")

            if (conn.responseCode != 200) return null
            val root = JSONObject(conn.inputStream.bufferedReader().readText())
            val arr  = root.optJSONArray("events") ?: return null
            if (arr.length() == 0) return null          // empty → try parallel

            val list = mutableListOf<SportEvent>()
            val seen = mutableSetOf<String>()
            for (i in 0 until arr.length()) {
                val ev = arr.getJSONObject(i)
                val id = ev.optString("id"); if (id.isEmpty() || seen.contains(id)) continue
                seen.add(id); mapEvent(ev)?.let { list.add(it) }
            }
            conn.disconnect()
            if (list.isEmpty()) null else list
        } catch (_: Exception) { null }
    }

    // ── Strategy 2: 7 parallel daily requests ───────────────────────
    private fun fetchParallel(): List<SportEvent>? {
        return try {
            val futures: List<Future<List<SportEvent>>> = (0..6).map { i ->
                executor.submit<List<SportEvent>> {
                    try {
                        val date = calStr(i)
                        val url  = URL(
                            "https://site.api.espn.com/apis/site/v2/sports/soccer/fifa.world/scoreboard?dates=$date"
                        )
                        val conn = url.openConnection() as HttpURLConnection
                        conn.connectTimeout = 6000
                        conn.readTimeout    = 6000
                        conn.setRequestProperty("User-Agent", "Sportzfy/9.0")

                        val list = mutableListOf<SportEvent>()
                        if (conn.responseCode == 200) {
                            val arr = JSONObject(conn.inputStream.bufferedReader().readText())
                                .optJSONArray("events")
                            arr?.let {
                                for (j in 0 until it.length()) mapEvent(it.getJSONObject(j))?.let { e -> list.add(e) }
                            }
                        }
                        conn.disconnect()
                        list
                    } catch (_: Exception) { emptyList() }
                }
            }

            val events = mutableListOf<SportEvent>()
            val seen   = mutableSetOf<String>()
            futures.forEach { f ->
                f.get().forEach { ev -> if (seen.add(ev.id)) events.add(ev) }
            }
            if (events.isEmpty()) null else events
        } catch (_: Exception) { null }
    }

    private fun calStr(daysAhead: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAhead)
        return String.format("%04d%02d%02d",
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun statusOrder(s: String) = when (s) {
        "live" -> 0; "upcoming" -> 1; else -> 2
    }

    private fun mapEvent(ev: JSONObject): SportEvent? {
        return try {
            val comp        = ev.optJSONArray("competitions")?.optJSONObject(0) ?: return null
            val competitors = comp.optJSONArray("competitors") ?: return null

            var home: JSONObject? = null
            var away: JSONObject? = null
            for (i in 0 until competitors.length()) {
                val c = competitors.getJSONObject(i)
                when (c.optString("homeAway")) { "home" -> home = c; "away" -> away = c }
            }
            if (home == null) home = competitors.optJSONObject(0)
            if (away == null) away = competitors.optJSONObject(1)

            val statusType = ev.optJSONObject("status")?.optJSONObject("type")
            val statusName = statusType?.optString("name") ?: ""
            val isLive = LIVE_STATUSES.contains(statusName)
            val isDone = statusName.contains("FINAL") || statusName == "STATUS_FULL_TIME"
            val clock  = ev.optJSONObject("status")?.optString("displayClock")
                ?.takeIf { it.isNotEmpty() && it != "0:00" }

            val homeTeam  = home?.optJSONObject("team")
            val awayTeam  = away?.optJSONObject("team")
            val homeScore = home?.optString("score") ?: ""
            val awayScore = away?.optString("score") ?: ""
            val date      = ev.optString("date")

            SportEvent(
                id       = ev.optString("id"),
                sport    = "football",
                league   = "Football · FIFA বিশ্বকাপ ২০২৬",
                homeName = homeTeam?.optString("shortDisplayName")?.takeIf { it.isNotEmpty() }
                    ?: homeTeam?.optString("displayName") ?: "Home",
                awayName = awayTeam?.optString("shortDisplayName")?.takeIf { it.isNotEmpty() }
                    ?: awayTeam?.optString("displayName") ?: "Away",
                homeLogo  = homeTeam?.optString("logo") ?: "",
                awayLogo  = awayTeam?.optString("logo") ?: "",
                homeScore = homeScore,
                awayScore = awayScore,
                status    = when { isLive -> "live"; isDone -> "finished"; else -> "upcoming" },
                clock     = clock,
                displayTime = formatBST(date),
                date      = date,
                hot       = isLive && homeScore.isNotEmpty() && awayScore.isNotEmpty() && homeScore != awayScore,
                venue     = comp.optJSONObject("venue")?.optString("fullName")?.takeIf { it.isNotEmpty() }
            )
        } catch (_: Exception) { null }
    }

    private fun formatBST(isoDate: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = sdf.parse(isoDate) ?: return "TBD"
            val bst = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"))
            bst.time = parsed
            val h = bst.get(Calendar.HOUR_OF_DAY)
            val m = bst.get(Calendar.MINUTE)
            val session = when {
                h < 6  -> "রাত"; h < 12 -> "সকাল"; h < 16 -> "দুপুর"
                h < 19 -> "বিকাল"; h < 21 -> "সন্ধ্যা"; else -> "রাত"
            }
            "$session %02d:%02d".format(h, m)
        } catch (_: Exception) { "TBD" }
    }
}

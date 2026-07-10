package com.sportzfy.app

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {

    // Raw GitHub API — no rate-limit token needed for public releases
    private const val API =
        "https://api.github.com/repos/freefirejafor-wq/sportzfy/releases/latest"

    // Fallback: raw tag endpoint (bypasses releases/latest rate limit)
    private const val TAGS_API =
        "https://api.github.com/repos/freefirejafor-wq/sportzfy/tags?per_page=1"

    fun check(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val release = fetchRelease() ?: return@launch

                val tag     = release.optString("tag_name", "").removePrefix("v")
                val latest  = tag.toIntOrNull() ?: return@launch
                val current = BuildConfig.VERSION_CODE

                if (latest <= current) return@launch   // আপডেট নেই

                val assets      = release.optJSONArray("assets") ?: return@launch
                if (assets.length() == 0) return@launch
                val apkUrl      = assets.getJSONObject(0).getString("browser_download_url")
                val releaseName = release.optString("name", "Sportzfy v$tag")
                val body        = release.optString("body", "").take(200)

                withContext(Dispatchers.Main) {
                    showUpdateDialog(context, releaseName, body, apkUrl)
                }
            } catch (_: Exception) { /* silent */ }
        }
    }

    // ── Fetch with fallback ──────────────────────────────────────────
    private fun fetchRelease(): JSONObject? {
        // Try primary API
        fetchJson(API)?.let { return it }

        // Rate-limited? Try tag list to at least get the latest tag number
        // then build the release URL from it
        return try {
            val tagsJson = fetchRaw(TAGS_API) ?: return null
            val arr  = org.json.JSONArray(tagsJson)
            if (arr.length() == 0) return null
            val tag  = arr.getJSONObject(0).optString("name", "").removePrefix("v")
            if (tag.isEmpty()) return null
            fetchJson("https://api.github.com/repos/freefirejafor-wq/sportzfy/releases/tags/v$tag")
        } catch (_: Exception) { null }
    }

    private fun fetchJson(url: String): JSONObject? {
        return try {
            val raw = fetchRaw(url) ?: return null
            val obj = JSONObject(raw)
            // Detect GitHub rate-limit or error responses
            if (obj.has("message") && !obj.has("tag_name")) null else obj
        } catch (_: Exception) { null }
    }

    private fun fetchRaw(url: String): String? {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.setRequestProperty("Accept",     "application/vnd.github+json")
            conn.setRequestProperty("User-Agent", "Sportzfy/${BuildConfig.VERSION_NAME}")
            conn.connectTimeout = 10_000
            conn.readTimeout    = 10_000
            if (conn.responseCode != 200) { conn.disconnect(); return null }
            val body = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            body
        } catch (_: Exception) { null }
    }

    // ── Update dialog ────────────────────────────────────────────────
    private fun showUpdateDialog(
        context: Context, name: String, body: String, apkUrl: String
    ) {
        val msg = buildString {
            append("$name পাওয়া গেছে!\n\n")
            if (body.isNotBlank()) append(body.trim()).append("\n\n")
            append("এখনই আপডেট করবেন?")
        }
        AlertDialog.Builder(context, R.style.DarkDialogTheme)
            .setTitle("🆕 নতুন আপডেট!")
            .setMessage(msg)
            .setPositiveButton("✅ ডাউনলোড করুন") { _, _ -> download(context, apkUrl) }
            .setNegativeButton("পরে") { d, _ -> d.dismiss() }
            .setCancelable(false)
            .show()
    }

    // ── Download with progress toast ─────────────────────────────────
    private fun download(context: Context, url: String) {
        Toast.makeText(context, "⬇️ ডাউনলোড শুরু হয়েছে... একটু অপেক্ষা করুন", Toast.LENGTH_LONG).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "sportzfy-update.apk"
                )
                if (file.exists()) file.delete()

                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connectTimeout = 15_000
                conn.readTimeout    = 60_000
                conn.connect()

                val total = conn.contentLength.toLong()
                var downloaded = 0L
                FileOutputStream(file).use { out ->
                    conn.inputStream.use { input ->
                        val buf = ByteArray(8 * 1024)
                        var n: Int
                        while (input.read(buf).also { n = it } != -1) {
                            out.write(buf, 0, n)
                            downloaded += n
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ ডাউনলোড সম্পন্ন! Install করুন।", Toast.LENGTH_SHORT).show()
                    val uri = FileProvider.getUriForFile(
                        context, "${context.packageName}.provider", file
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    })
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ ডাউনলোড ব্যর্থ। পুনরায় চেষ্টা করুন।", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

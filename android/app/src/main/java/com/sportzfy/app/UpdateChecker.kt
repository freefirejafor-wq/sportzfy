package com.sportzfy.app

import android.content.Context
import android.content.Intent
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

    private const val API = "https://api.github.com/repos/freefirejafor-wq/sportzfy/releases/latest"

    fun check(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL(API).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                if (conn.responseCode != 200) return@launch

                val release = JSONObject(conn.inputStream.bufferedReader().readText())
                val tag = release.getString("tag_name").removePrefix("v")
                val latest = tag.toIntOrNull() ?: return@launch
                val current = BuildConfig.VERSION_CODE
                if (latest <= current) return@launch

                val assets = release.getJSONArray("assets")
                if (assets.length() == 0) return@launch
                val apkUrl = assets.getJSONObject(0).getString("browser_download_url")
                val releaseName = release.optString("name", "Sportzfy v$tag")

                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(context, R.style.DarkDialogTheme)
                        .setTitle("🆕 নতুন আপডেট!")
                        .setMessage("$releaseName পাওয়া গেছে।\n\nএখনই আপডেট করবেন?")
                        .setPositiveButton("✅ আপডেট") { _, _ -> download(context, apkUrl) }
                        .setNegativeButton("পরে") { d, _ -> d.dismiss() }
                        .setCancelable(false)
                        .show()
                }
            } catch (_: Exception) { /* silent — background check */ }
        }
    }

    private fun download(context: Context, url: String) {
        Toast.makeText(context, "⬇️ ডাউনলোড হচ্ছে...", Toast.LENGTH_LONG).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "sportzfy-update.apk"
                )
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connect()
                FileOutputStream(file).use { out -> conn.inputStream.use { it.copyTo(out) } }

                withContext(Dispatchers.Main) {
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
                    Toast.makeText(context, "❌ ডাউনলোড ব্যর্থ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

package com.sportzfy.app

import android.os.Bundle
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url") ?: "https://sportzfy.app"
        val title = intent.getStringExtra("title") ?: "Sportzfy"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString = "Sportzfy/8.0 Android"
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progressBar.visibility = ProgressBar.GONE
                }
            }
            loadUrl(url)
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressed(); return true }
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }
}

package com.sportzfy.app

import android.os.Bundle
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    companion object {
        // Chrome user-agent — YouTube accepts this in WebView
        private const val CHROME_UA =
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url   = intent.getStringExtra("url")   ?: "https://sportzfy.app"
        val title = intent.getStringExtra("title") ?: "Sportzfy"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.webView.apply {
            settings.apply {
                javaScriptEnabled                          = true
                domStorageEnabled                          = true
                mediaPlaybackRequiresUserGesture           = false   // auto-play video
                allowContentAccess                         = true
                allowFileAccess                            = true
                setSupportMultipleWindows(false)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString  = CHROME_UA                          // ← key fix
                loadWithOverviewMode = true
                useWideViewPort      = true
                builtInZoomControls  = false
                cacheMode            = WebSettings.LOAD_DEFAULT
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progressBar.visibility = ProgressBar.GONE
                }
                // Allow YouTube redirects within the WebView
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val host = request.url.host ?: return false
                    return !host.contains("youtube.com") && !host.contains("youtu.be") &&
                           !host.contains("googlevideo.com") && !host.contains("ggpht.com")
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) binding.progressBar.visibility = ProgressBar.GONE
                    else binding.progressBar.visibility = ProgressBar.VISIBLE
                }
            }

            loadUrl(url)
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressed(); return true }
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }

    override fun onPause()  { super.onPause();  binding.webView.onPause() }
    override fun onResume() { super.onResume(); binding.webView.onResume() }
    override fun onDestroy() {
        binding.webView.stopLoading()
        binding.webView.destroy()
        super.onDestroy()
    }
}

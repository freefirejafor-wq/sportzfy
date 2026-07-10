package com.sportzfy.app

import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    companion object {
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
                javaScriptEnabled                = true
                domStorageEnabled                = true
                mediaPlaybackRequiresUserGesture = false   // auto-play video
                allowContentAccess               = true
                allowFileAccess                  = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString  = CHROME_UA
                loadWithOverviewMode             = true
                useWideViewPort                  = true
                builtInZoomControls              = false
                cacheMode                        = WebSettings.LOAD_DEFAULT
                databaseEnabled                  = true
                // Needed for YouTube embed cross-origin iframes
                @Suppress("DEPRECATION")
                allowUniversalAccessFromFileURLs = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progressBar.visibility = ProgressBar.GONE
                }
                override fun shouldOverrideUrlLoading(
                    view: WebView, request: WebResourceRequest
                ): Boolean {
                    val host = request.url.host ?: return false
                    // Allow all YouTube / Google / Invidious / Piped domains inside WebView
                    val allowed = listOf(
                        "youtube.com", "youtu.be", "googlevideo.com", "ggpht.com",
                        "ytimg.com", "youtube-nocookie.com", "googleusercontent.com",
                        "googleapis.com", "google.com"
                    )
                    return allowed.none { host.contains(it) }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    binding.progressBar.visibility =
                        if (newProgress == 100) ProgressBar.GONE else ProgressBar.VISIBLE
                }

                // ── Full-screen video support ────────────────────────
                override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                    customView?.let { callback.onCustomViewHidden(); return }
                    customView = view
                    customViewCallback = callback
                    (window.decorView as FrameLayout).addView(
                        view, FrameLayout.LayoutParams(-1, -1)
                    )
                    binding.webView.visibility = View.GONE
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
                }

                override fun onHideCustomView() {
                    (window.decorView as FrameLayout).removeView(customView)
                    customView = null
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                    binding.webView.visibility = View.VISIBLE
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }

            loadUrl(url)
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressed(); return true }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        // Exit fullscreen first
        customView?.let {
            (webChromeClientRef as? WebChromeClient)?.onHideCustomView()
            return
        }
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }

    private val webChromeClientRef get() = binding.webView.webChromeClient

    override fun onPause()   { super.onPause();   binding.webView.onPause() }
    override fun onResume()  { super.onResume();  binding.webView.onResume() }
    override fun onDestroy() {
        binding.webView.stopLoading()
        binding.webView.destroy()
        super.onDestroy()
    }
}

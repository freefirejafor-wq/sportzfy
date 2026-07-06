package com.sportzfy.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val webUrl by lazy { getString(R.string.web_url) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isOnline()) {
            setupWebView()
        } else {
            showNoInternet()
        }

        binding.retryButton.setOnClickListener {
            if (isOnline()) {
                binding.noInternetLayout.visibility = View.GONE
                setupWebView()
            }
        }
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
                allowFileAccess = true
                allowContentAccess = true
                setSupportMultipleWindows(true)
            }

            // JavaScript Interface — web থেকে native player খুলবে
            addJavascriptInterface(StreamBridge(), "AndroidBridge")

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                    // Web page-এ JavaScript inject করি — video URL ধরার জন্য
                    injectVideoInterceptor()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return when {
                        // M3U8/HLS stream → native player-এ খুলব
                        url.contains(".m3u8") || url.contains(".mpd") -> {
                            openNativePlayer(url, "Live Stream")
                            true
                        }
                        // External links → browser-এ খুলব
                        !url.contains(Uri.parse(webUrl).host ?: "") -> {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        }
                        else -> false
                    }
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        showNoInternet()
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    // Fullscreen video handling
                }
            }

            loadUrl(webUrl)
        }
    }

    // Web page-এ script inject করি — video element ধরতে
    private fun injectVideoInterceptor() {
        val script = """
            (function() {
                document.querySelectorAll('video').forEach(function(v) {
                    v.addEventListener('play', function() {
                        var src = v.src || v.querySelector('source')?.src;
                        if (src && (src.includes('.m3u8') || src.includes('.mpd'))) {
                            if (window.AndroidBridge) {
                                window.AndroidBridge.onStreamDetected(src, document.title || 'Live Stream');
                            }
                        }
                    });
                });
            })();
        """.trimIndent()
        binding.webView.evaluateJavascript(script, null)
    }

    // JavaScript → Kotlin bridge
    inner class StreamBridge {
        @JavascriptInterface
        fun onStreamDetected(url: String, title: String) {
            runOnUiThread {
                openNativePlayer(url, title)
            }
        }

        @JavascriptInterface
        fun openPlayer(url: String, title: String) {
            runOnUiThread {
                openNativePlayer(url, title)
            }
        }
    }

    private fun openNativePlayer(url: String, title: String) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_URL, url)
            putExtra(PlayerActivity.EXTRA_TITLE, title)
        }
        startActivity(intent)
    }

    private fun showNoInternet() {
        binding.progressBar.visibility = View.GONE
        binding.noInternetLayout.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

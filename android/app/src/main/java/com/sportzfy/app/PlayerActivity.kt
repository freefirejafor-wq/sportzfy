package com.sportzfy.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.sportzfy.app.databinding.ActivityPlayerBinding

@UnstableApi
class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STREAM_URL  = "stream_url"
        const val EXTRA_STREAM_NAME = "stream_name"
        const val EXTRA_MATCH_TITLE = "match_title"
        const val EXTRA_DRM_KID     = "drm_kid"
        const val EXTRA_DRM_KEY     = "drm_key"
        const val EXTRA_FORMAT      = "format"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private var isLocked   = false
    private var isFullscreen = true
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideControls() }

    // Current stream info (for re-launch / quality switch)
    private var currentUrl    = ""
    private var currentFormat = "hls"
    private var currentDrmKid: String? = null
    private var currentDrmKey: String? = null

    // ─────────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        val streamUrl  = intent.getStringExtra(EXTRA_STREAM_URL)  ?: return
        val streamName = intent.getStringExtra(EXTRA_STREAM_NAME) ?: ""
        val matchTitle = intent.getStringExtra(EXTRA_MATCH_TITLE) ?: ""
        currentDrmKid  = intent.getStringExtra(EXTRA_DRM_KID)
        currentDrmKey  = intent.getStringExtra(EXTRA_DRM_KEY)
        currentFormat  = intent.getStringExtra(EXTRA_FORMAT) ?: "hls"
        currentUrl     = streamUrl

        binding.tvMatchTitle.text  = matchTitle
        binding.tvStreamName.text  = "📡  $streamName"

        initPlayer(streamUrl, currentFormat, currentDrmKid, currentDrmKey)
        setupControls(streamName, matchTitle)
        scheduleHide()
    }

    // ── hex → base64url ───────────────────────────────────────────────
    private fun hexToBase64Url(hex: String): String {
        val bytes = ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun buildClearKeyUri(kid: String, key: String): String {
        val k64 = hexToBase64Url(kid)
        val v64 = hexToBase64Url(key)
        return """data:application/json,{"keys":[{"kty":"oct","k":"$v64","kid":"$k64"}],"type":"temporary"}"""
    }

    // ── init ExoPlayer with FFmpeg renderer ───────────────────────────
    private fun initPlayer(url: String, format: String, drmKid: String?, drmKey: String?) {
        // Prefer FFmpeg extension decoders for extra codec support
        val renderersFactory = DefaultRenderersFactory(this).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        }

        trackSelector = DefaultTrackSelector(this)
        player = ExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .build()
            .also { exo ->
                binding.playerView.player = exo
                binding.playerView.useController = false

                val mediaItem = when {
                    format == "dash" && drmKid != null && drmKey != null -> {
                        // DASH + ClearKey DRM
                        MediaItem.Builder()
                            .setUri(url)
                            .setMimeType(MimeTypes.APPLICATION_MPD)
                            .setDrmConfiguration(
                                MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                                    .setLicenseUri(buildClearKeyUri(drmKid, drmKey))
                                    .build()
                            )
                            .build()
                    }
                    format == "dash" -> {
                        MediaItem.Builder()
                            .setUri(url)
                            .setMimeType(MimeTypes.APPLICATION_MPD)
                            .build()
                    }
                    url.contains(".m3u8", ignoreCase = true) ||
                    url.contains("m3u", ignoreCase = true) ||
                    format == "hls" -> {
                        MediaItem.Builder()
                            .setUri(url)
                            .setMimeType(MimeTypes.APPLICATION_M3U8)
                            .build()
                    }
                    else -> MediaItem.fromUri(url)
                }

                exo.setMediaItem(mediaItem)
                exo.prepare()
                exo.playWhenReady = true

                exo.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> {
                                binding.progressBuffering.visibility = View.VISIBLE
                                binding.tvBufferingMsg.visibility    = View.VISIBLE
                            }
                            Player.STATE_READY -> {
                                binding.progressBuffering.visibility = View.GONE
                                binding.tvBufferingMsg.visibility    = View.GONE
                                binding.btnPlayPause.text = "⏸"
                            }
                            Player.STATE_ENDED -> binding.btnPlayPause.text = "▶"
                            else -> {}
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        binding.btnPlayPause.text = if (isPlaying) "⏸" else "▶"
                    }
                })
            }
    }

    // ── setup all button controls ─────────────────────────────────────
    private fun setupControls(streamName: String, matchTitle: String) {

        // Tap anywhere → show/hide controls
        binding.playerView.setOnClickListener {
            if (isLocked) return@setOnClickListener
            toggleControls()
        }

        // Play / Pause
        binding.btnPlayPause.setOnClickListener {
            player?.let { if (it.isPlaying) it.pause() else it.play() }
            scheduleHide()
        }

        // Rewind 10s
        binding.btnRewind.setOnClickListener {
            player?.let { it.seekTo(maxOf(0, it.currentPosition - 10_000)) }
            scheduleHide()
        }

        // Forward 10s
        binding.btnForward.setOnClickListener {
            player?.let { it.seekTo(it.currentPosition + 10_000) }
            scheduleHide()
        }

        // Lock / Unlock controls
        binding.btnLock.setOnClickListener {
            isLocked = !isLocked
            binding.btnLock.text = if (isLocked) "🔓" else "🔒"
            Toast.makeText(this,
                if (isLocked) "কন্ট্রোল লক হয়েছে — ট্যাপ করুন আনলক করতে"
                else "কন্ট্রোল আনলক হয়েছে",
                Toast.LENGTH_SHORT).show()
            if (isLocked) {
                binding.topBar.visibility    = View.GONE
                binding.bottomBar.visibility = View.GONE
                binding.centerControls.visibility = View.GONE
                binding.btnLock.visibility   = View.VISIBLE
            } else {
                showControls()
            }
            scheduleHide()
        }

        // Back / Minimize → floating player
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, FloatingPlayerService::class.java).apply {
                putExtra("stream_url",  currentUrl)
                putExtra("stream_name", streamName)
                putExtra("match_title", matchTitle)
                putExtra("format",      currentFormat)
            }
            startForegroundService(intent)
            finish()
        }

        // Close
        binding.btnClose.setOnClickListener { finish() }

        // Quality selector — HD / SD / Auto
        binding.btnQuality.setOnClickListener {
            showQualityDialog()
            scheduleHide()
        }

        // Add custom URL / M3U8
        binding.btnAddUrl.setOnClickListener {
            showAddUrlDialog(matchTitle)
            scheduleHide()
        }

        // PiP (floating)
        binding.btnFloat.setOnClickListener {
            val intent = Intent(this, FloatingPlayerService::class.java).apply {
                putExtra("stream_url",  currentUrl)
                putExtra("stream_name", streamName)
                putExtra("match_title", matchTitle)
                putExtra("format",      currentFormat)
            }
            startForegroundService(intent)
            finish()
        }
    }

    // ── Quality Dialog ────────────────────────────────────────────────
    private fun showQualityDialog() {
        val tracks = player?.currentTracks ?: return
        val videoGroup = tracks.groups.firstOrNull { g ->
            g.type == C.TRACK_TYPE_VIDEO && g.length > 1
        }

        val items = mutableListOf("🔄 Auto (সেরা উপলব্ধ)")
        val heights = mutableListOf(-1) // -1 = auto

        videoGroup?.let { g ->
            for (i in 0 until g.length) {
                val fmt = g.getTrackFormat(i)
                val label = when {
                    fmt.height >= 1080 -> "🔵 FHD ${fmt.height}p"
                    fmt.height >= 720  -> "🟢 HD  ${fmt.height}p"
                    fmt.height >= 480  -> "🟡 SD  ${fmt.height}p"
                    else               -> "🔴 Low ${fmt.height}p"
                }
                items.add(label)
                heights.add(fmt.height)
            }
        }

        if (videoGroup == null) {
            // Fallback quality names for adaptive streams
            items.addAll(listOf("🔵 HD (720p+)", "🟡 SD (480p-)", "🔴 Low Quality"))
            heights.addAll(listOf(720, 480, 360))
        }

        AlertDialog.Builder(this, R.style.DarkDialogTheme)
            .setTitle("কোয়ালিটি বেছে নিন")
            .setItems(items.toTypedArray()) { _, idx ->
                val maxHeight = heights[idx]
                if (maxHeight == -1) {
                    // Auto: clear overrides
                    trackSelector.buildUponParameters()
                        .clearVideoSizeConstraints()
                        .also { trackSelector.setParameters(it) }
                    Toast.makeText(this, "Auto quality চালু হয়েছে", Toast.LENGTH_SHORT).show()
                } else if (videoGroup != null && idx > 0) {
                    // Override to specific track
                    val override = TrackSelectionOverride(videoGroup.mediaTrackGroup, idx - 1)
                    trackSelector.buildUponParameters()
                        .setOverrideForType(override)
                        .also { trackSelector.setParameters(it) }
                    Toast.makeText(this, "${items[idx]} সিলেক্ট হয়েছে", Toast.LENGTH_SHORT).show()
                } else {
                    // Fallback: constrain by max height
                    trackSelector.buildUponParameters()
                        .setMaxVideoSize(Int.MAX_VALUE, maxHeight)
                        .also { trackSelector.setParameters(it) }
                    Toast.makeText(this, "${items[idx]} সিলেক্ট হয়েছে", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    // ── Add Custom URL / M3U8 Dialog ──────────────────────────────────
    private fun showAddUrlDialog(matchTitle: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
        }
        val urlInput = EditText(this).apply {
            hint = "M3U8 / Stream URL এখানে দিন"
            inputType = InputType.TYPE_TEXT_VARIATION_URI or InputType.TYPE_CLASS_TEXT
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF888888.toInt())
            setText(currentUrl)
        }
        val nameInput = EditText(this).apply {
            hint = "Stream এর নাম (ঐচ্ছিক)"
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF888888.toInt())
            setPadding(0, 20, 0, 0)
        }
        layout.addView(urlInput)
        layout.addView(nameInput)

        AlertDialog.Builder(this, R.style.DarkDialogTheme)
            .setTitle("Custom URL / M3U8 চালান")
            .setView(layout)
            .setPositiveButton("▶ চালান") { _, _ ->
                val url  = urlInput.text.toString().trim()
                val name = nameInput.text.toString().trim().ifEmpty { "Custom Stream" }
                if (url.isNotEmpty()) {
                    player?.release()
                    currentUrl = url
                    currentFormat = when {
                        url.contains(".mpd", ignoreCase = true) -> "dash"
                        else -> "hls"
                    }
                    binding.tvStreamName.text = "📡  $name"
                    initPlayer(url, currentFormat, null, null)
                } else {
                    Toast.makeText(this, "URL খালি রাখা যাবে না", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    // ── UI helpers ────────────────────────────────────────────────────
    private fun showControls() {
        binding.topBar.visibility         = View.VISIBLE
        binding.bottomBar.visibility      = View.VISIBLE
        binding.centerControls.visibility = View.VISIBLE
        binding.btnLock.visibility        = View.VISIBLE
    }

    private fun hideControls() {
        binding.topBar.visibility         = View.GONE
        binding.bottomBar.visibility      = View.GONE
        binding.centerControls.visibility = View.GONE
        if (!isLocked) binding.btnLock.visibility = View.GONE
    }

    private fun toggleControls() {
        if (binding.topBar.visibility == View.VISIBLE) {
            hideControls()
        } else {
            showControls()
            scheduleHide()
        }
    }

    private fun scheduleHide() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 4000)
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN          or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION      or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    // ── lifecycle ─────────────────────────────────────────────────────
    override fun onPause()   { super.onPause();   player?.pause() }
    override fun onResume()  { super.onResume();  hideSystemUI() }
    override fun onDestroy() { super.onDestroy(); player?.release(); player = null }
}

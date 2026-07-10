package com.sportzfy.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
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
    private var controlsVisible = true
    private val hideRunnable = Runnable { hideControls() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val streamUrl  = intent.getStringExtra(EXTRA_STREAM_URL)  ?: return
        val streamName = intent.getStringExtra(EXTRA_STREAM_NAME) ?: ""
        val matchTitle = intent.getStringExtra(EXTRA_MATCH_TITLE) ?: ""
        val drmKid     = intent.getStringExtra(EXTRA_DRM_KID)
        val drmKey     = intent.getStringExtra(EXTRA_DRM_KEY)
        val format     = intent.getStringExtra(EXTRA_FORMAT) ?: "hls"

        binding.tvMatchTitle.text = matchTitle
        binding.tvStreamName.text = "📡 $streamName"

        initPlayer(streamUrl, format, drmKid, drmKey)
        setupControls(streamName, matchTitle)
    }

    private fun hexToBase64Url(hex: String): String {
        val bytes = ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun buildClearKeyUri(kid: String, key: String): String {
        val k  = hexToBase64Url(key)
        val ki = hexToBase64Url(kid)
        return """data:application/json,{"keys":[{"kty":"oct","k":"$k","kid":"$ki"}],"type":"temporary"}"""
    }

    private fun initPlayer(url: String, format: String, drmKid: String?, drmKey: String?) {
        trackSelector = DefaultTrackSelector(this)
        player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build().also { exo ->
            binding.playerView.player = exo
            binding.playerView.useController = false

            val mediaItem = when {
                format == "dash" && drmKid != null && drmKey != null -> {
                    val drmConfig = MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                        .setLicenseUri(buildClearKeyUri(drmKid, drmKey))
                        .build()
                    MediaItem.Builder()
                        .setUri(url)
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .setDrmConfiguration(drmConfig)
                        .build()
                }
                format == "dash" -> MediaItem.Builder()
                    .setUri(url).setMimeType(MimeTypes.APPLICATION_MPD).build()
                else -> MediaItem.fromUri(url)
            }

            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = true

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    val loading = state == Player.STATE_BUFFERING
                    binding.progressBuffering.visibility = if (loading) View.VISIBLE else View.GONE
                    binding.tvBufferingMsg.visibility    = if (loading) View.VISIBLE else View.GONE
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    binding.btnPlayPause.text = if (isPlaying) "⏸" else "▶"
                }
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    binding.progressBuffering.visibility = View.GONE
                    binding.tvBufferingMsg.visibility    = View.GONE
                    AlertDialog.Builder(this@PlayerActivity)
                        .setTitle("⚠️ Playback Error")
                        .setMessage("Stream could not be loaded.\n\n${error.message}")
                        .setPositiveButton("OK") { d, _ -> d.dismiss() }
                        .show()
                }
            })
        }
    }

    private fun setupControls(streamName: String, matchTitle: String) {
        binding.root.setOnClickListener     { toggleControls() }
        binding.playerView.setOnClickListener { toggleControls() }

        binding.btnPlayPause.setOnClickListener {
            player?.let { if (it.isPlaying) it.pause() else it.play() }
        }

        binding.btnBack.setOnClickListener  { finish() }
        binding.btnClose.setOnClickListener { finish() }

        binding.btnQuality.setOnClickListener { showQualityDialog() }

        binding.btnStreams.setOnClickListener {
            StreamPickerBottomSheet.newInstance(matchTitle)
                .show(supportFragmentManager, "streams")
        }

        binding.btnFloat.setOnClickListener { minimizeToFloating(streamName, matchTitle) }

        binding.btnLock.setOnClickListener {
            hideControls()
            binding.lockOverlay.visibility = View.VISIBLE
        }
        binding.btnUnlock.setOnClickListener {
            binding.lockOverlay.visibility = View.GONE
            showControls()
        }

        scheduleHide()
    }

    private fun toggleControls() {
        if (binding.lockOverlay.visibility == View.VISIBLE) return
        if (controlsVisible) hideControls() else showControls()
    }

    private fun showControls() {
        controlsVisible = true
        binding.topBar.visibility       = View.VISIBLE
        binding.bottomBar.visibility    = View.VISIBLE
        binding.btnPlayPause.visibility = View.VISIBLE
        scheduleHide()
    }

    private fun hideControls() {
        controlsVisible = false
        binding.topBar.visibility       = View.GONE
        binding.bottomBar.visibility    = View.GONE
        binding.btnPlayPause.visibility = View.GONE
    }

    private fun scheduleHide() {
        binding.root.removeCallbacks(hideRunnable)
        binding.root.postDelayed(hideRunnable, 4000)
    }

    private fun minimizeToFloating(streamName: String, matchTitle: String) {
        player?.let { exo ->
            val url = exo.currentMediaItem?.localConfiguration?.uri?.toString() ?: return
            startService(Intent(this, FloatingPlayerService::class.java).apply {
                putExtra(FloatingPlayerService.EXTRA_STREAM_URL,  url)
                putExtra(FloatingPlayerService.EXTRA_STREAM_NAME, streamName)
                putExtra(FloatingPlayerService.EXTRA_MATCH_TITLE, matchTitle)
            })
        }
        finish()
    }

    private fun showQualityDialog() {
        val qualities = arrayOf("Auto (Recommended)", "1080p FHD", "720p HD", "480p SD", "360p SD")
        AlertDialog.Builder(this, R.style.QualityDialogTheme)
            .setTitle("🎯 Quality")
            .setItems(qualities) { _, which ->
                when (which) {
                    0 -> trackSelector.setParameters(trackSelector.buildUponParameters().clearVideoSizeConstraints())
                    1 -> trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(1920, 1080))
                    2 -> trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(1280, 720))
                    3 -> trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(854, 480))
                    4 -> trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(640, 360))
                }
            }.show()
    }

    override fun onResume()  { super.onResume();  player?.play() }
    override fun onPause()   { super.onPause();   player?.pause() }
    override fun onDestroy() { super.onDestroy(); player?.release(); player = null }
}

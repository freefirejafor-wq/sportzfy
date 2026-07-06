package com.sportzfy.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.sportzfy.app.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URL = "stream_url"
        const val EXTRA_TITLE = "stream_title"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector

    private var streamUrl = ""
    private var streamTitle = ""
    private var isLocked = false
    private var isFullscreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        streamUrl = intent.getStringExtra(EXTRA_URL) ?: ""
        streamTitle = intent.getStringExtra(EXTRA_TITLE) ?: "Live Stream"

        if (streamUrl.isEmpty()) {
            Toast.makeText(this, "Stream URL পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTitle.text = streamTitle
        enableFullscreen()
        setupPlayer()
        setupControls()
    }

    private fun setupPlayer() {
        // FFmpeg + ExoPlayer রেন্ডারার — এক্সট্রা কোডেক সাপোর্ট
        val renderersFactory = DefaultRenderersFactory(this).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        }

        trackSelector = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredVideoMimeType(MimeTypes.VIDEO_H264)
                    .setMaxVideoSizeSd() // Default: SD
            )
        }

        player = ExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer

                val mediaItem = when {
                    streamUrl.contains(".m3u8") -> MediaItem.Builder()
                        .setUri(streamUrl)
                        .setMimeType(MimeTypes.APPLICATION_M3U8) // HLS
                        .build()
                    streamUrl.contains(".mpd") -> MediaItem.Builder()
                        .setUri(streamUrl)
                        .setMimeType(MimeTypes.APPLICATION_MPD) // DASH
                        .build()
                    else -> MediaItem.fromUri(streamUrl)
                }

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()

                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Toast.makeText(
                            this@PlayerActivity,
                            "স্ট্রিম লোড হয়নি: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                        // Quality tracks পাওয়া গেলে Quality button সক্রিয় করি
                        val hasMultipleQualities = tracks.groups.any { group ->
                            group.type == C.TRACK_TYPE_VIDEO && group.length > 1
                        }
                        binding.btnQuality.isEnabled = hasMultipleQualities
                    }
                })
            }
    }

    private fun setupControls() {
        // Back Button
        binding.btnBack.setOnClickListener {
            if (!isLocked) finish()
        }

        // Lock Button — সব control বন্ধ করে
        binding.btnLock.setOnClickListener {
            isLocked = true
            binding.topControlsBar.visibility = View.GONE
            binding.bottomControlsBar.visibility = View.GONE
            binding.lockOverlay.visibility = View.VISIBLE
            Toast.makeText(this, "🔒 কন্ট্রোল লক হয়েছে", Toast.LENGTH_SHORT).show()
        }

        // Unlock Button
        binding.btnUnlock.setOnClickListener {
            isLocked = false
            binding.lockOverlay.visibility = View.GONE
            binding.topControlsBar.visibility = View.VISIBLE
            binding.bottomControlsBar.visibility = View.VISIBLE
            Toast.makeText(this, "🔓 কন্ট্রোল আনলক হয়েছে", Toast.LENGTH_SHORT).show()
        }

        // Quality Select — HD/SD বেছে নেওয়া
        binding.btnQuality.setOnClickListener {
            showQualityDialog()
        }

        // Floating Player — মিনি পপআপ প্লেয়ার
        binding.btnFloating.setOnClickListener {
            startFloatingPlayer()
        }

        // Fullscreen toggle
        binding.btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }
    }

    // Quality Dialog — HD / SD / Auto
    private fun showQualityDialog() {
        val tracks = player?.currentTracks ?: return

        val videoGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
        if (videoGroups.isEmpty()) {
            Toast.makeText(this, "কোয়ালিটি তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }

        val group = videoGroups.first()
        val qualities = mutableListOf("Auto")
        val heightList = mutableListOf(-1) // Auto = -1

        for (i in 0 until group.length) {
            if (group.isTrackSupported(i)) {
                val format = group.getTrackFormat(i)
                val label = when {
                    format.height >= 1080 -> "1080p (Full HD)"
                    format.height >= 720  -> "720p (HD)"
                    format.height >= 480  -> "480p (SD)"
                    format.height >= 360  -> "360p"
                    format.height > 0     -> "${format.height}p"
                    else                  -> "Track ${i + 1}"
                }
                qualities.add(label)
                heightList.add(format.height)
            }
        }

        AlertDialog.Builder(this)
            .setTitle("কোয়ালিটি বেছে নিন")
            .setItems(qualities.toTypedArray()) { _, which ->
                val selectedHeight = heightList[which]
                if (selectedHeight == -1) {
                    // Auto mode
                    trackSelector.setParameters(
                        trackSelector.buildUponParameters().clearVideoSizeConstraints()
                    )
                    binding.btnQuality.text = "Auto"
                } else {
                    // নির্দিষ্ট quality
                    val label = qualities[which].substringBefore(" ")
                    binding.btnQuality.text = label
                    trackSelector.setParameters(
                        trackSelector.buildUponParameters()
                            .setMaxVideoSize(Int.MAX_VALUE, selectedHeight)
                            .setMinVideoSize(0, selectedHeight)
                    )
                }
            }
            .show()
    }

    // Floating Player চালু করো
    private fun startFloatingPlayer() {
        val intent = Intent(this, FloatingPlayerService::class.java).apply {
            putExtra(FloatingPlayerService.EXTRA_URL, streamUrl)
            putExtra(FloatingPlayerService.EXTRA_TITLE, streamTitle)
        }
        startForegroundService(intent)
        // Player Activity বন্ধ করি — ব্যাকগ্রাউন্ডে যাব
        player?.stop()
        finish()
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isFullscreen = false
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            enableFullscreen()
            isFullscreen = true
        }
    }

    private fun enableFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
        enableFullscreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}

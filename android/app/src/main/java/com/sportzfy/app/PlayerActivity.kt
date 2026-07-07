package com.sportzfy.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.sportzfy.app.databinding.ActivityPlayerBinding

@UnstableApi
class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STREAM_URL   = "stream_url"
        const val EXTRA_STREAM_NAME  = "stream_name"
        const val EXTRA_MATCH_TITLE  = "match_title"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private var isLocked = false
    private val hideControlsRunnable = Runnable { hideControls() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Full landscape + keep screen on
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val streamUrl   = intent.getStringExtra(EXTRA_STREAM_URL) ?: return
        val streamName  = intent.getStringExtra(EXTRA_STREAM_NAME) ?: ""
        val matchTitle  = intent.getStringExtra(EXTRA_MATCH_TITLE) ?: ""

        binding.tvMatchTitle.text  = matchTitle
        binding.tvStreamName.text  = "📡 $streamName"

        initPlayer(streamUrl)
        setupControls(streamName, matchTitle)
    }

    private fun initPlayer(url: String) {
        trackSelector = DefaultTrackSelector(this)
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exo ->
                binding.playerView.player = exo
                binding.playerView.useController = false
                exo.setMediaItem(MediaItem.fromUri(url))
                exo.prepare()
                exo.playWhenReady = true

                exo.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> {
                                binding.progressBuffering.visibility = View.VISIBLE
                                binding.tvBufferingMsg.visibility = View.VISIBLE
                            }
                            Player.STATE_READY -> {
                                binding.progressBuffering.visibility = View.GONE
                                binding.tvBufferingMsg.visibility = View.GONE
                            }
                            Player.STATE_ENDED, Player.STATE_IDLE -> {
                                binding.progressBuffering.visibility = View.GONE
                                binding.tvBufferingMsg.visibility = View.GONE
                            }
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        binding.btnPlayPause.text = if (playing) "⏸" else "▶"
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        binding.progressBuffering.visibility = View.GONE
                        binding.tvBufferingMsg.visibility = View.VISIBLE
                        binding.tvBufferingMsg.text = "⚠ Stream error — try another stream"
                    }
                })
            }
    }

    private fun setupControls(streamName: String, matchTitle: String) {
        // Tap to toggle controls
        binding.playerView.setOnClickListener {
            if (isLocked) {
                showLockOnly()
                return@setOnClickListener
            }
            if (binding.topBar.visibility == View.VISIBLE) hideControls()
            else showControls()
        }

        // Back (minimize to floating)
        binding.btnBack.setOnClickListener { minimizeToFloating(streamName, matchTitle) }

        // Close
        binding.btnClose.setOnClickListener { finish() }

        // Play/Pause
        binding.btnPlayPause.setOnClickListener {
            player?.let { if (it.isPlaying) it.pause() else it.play() }
            resetControlsTimer()
        }

        // Lock
        binding.btnLock.setOnClickListener {
            isLocked = true
            binding.topBar.visibility = View.GONE
            binding.bottomBar.visibility = View.GONE
            binding.lockOverlay.visibility = View.VISIBLE
        }

        // Unlock
        binding.btnUnlock.setOnClickListener {
            isLocked = false
            binding.lockOverlay.visibility = View.GONE
            showControls()
        }

        // Quality select
        binding.btnQuality.setOnClickListener {
            showQualityDialog()
            resetControlsTimer()
        }

        // Streams
        binding.btnStreams.setOnClickListener {
            val sheet = com.sportzfy.app.ui.StreamPickerBottomSheet.newInstance(matchTitle)
            sheet.show(supportFragmentManager, "streams")
            resetControlsTimer()
        }

        // Floating mini player
        binding.btnFloat.setOnClickListener { minimizeToFloating(streamName, matchTitle) }

        showControls()
    }

    private fun showControls() {
        if (isLocked) return
        binding.topBar.visibility = View.VISIBLE
        binding.bottomBar.visibility = View.VISIBLE
        resetControlsTimer()
    }

    private fun hideControls() {
        binding.topBar.visibility = View.GONE
        binding.bottomBar.visibility = View.GONE
    }

    private fun showLockOnly() {
        // Just pulse the overlay briefly
        binding.lockOverlay.alpha = 1f
        binding.lockOverlay.animate().alpha(0f).setStartDelay(1500).setDuration(400).start()
    }

    private fun resetControlsTimer() {
        binding.root.removeCallbacks(hideControlsRunnable)
        binding.root.postDelayed(hideControlsRunnable, 4000)
    }

    private fun minimizeToFloating(streamName: String, matchTitle: String) {
        player?.let {
            val intent = Intent(this, FloatingPlayerService::class.java).apply {
                putExtra(FloatingPlayerService.EXTRA_URL,   it.currentMediaItem?.localConfiguration?.uri?.toString() ?: "")
                putExtra(FloatingPlayerService.EXTRA_NAME,  streamName)
                putExtra(FloatingPlayerService.EXTRA_TITLE, matchTitle)
                putExtra(FloatingPlayerService.EXTRA_POS,   it.currentPosition)
            }
            startService(intent)
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
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}

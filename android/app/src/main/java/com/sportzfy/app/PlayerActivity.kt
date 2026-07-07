package com.sportzfy.app

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.datasource.DefaultHttpDataSource
import com.sportzfy.app.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URL   = "stream_url"
        const val EXTRA_TITLE = "stream_title"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private var isLocked = false
    private var controlsVisible = true
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideControls() }
    private var streamUrl = ""
    private var streamTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Fullscreen landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        streamUrl   = intent.getStringExtra(EXTRA_URL)   ?: ""
        streamTitle = intent.getStringExtra(EXTRA_TITLE) ?: "Live Stream"

        binding.textTitle.text = streamTitle

        setupPlayer()
        setupControls()
        scheduleHideControls()
    }

    private fun setupPlayer() {
        trackSelector = DefaultTrackSelector(this)

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        binding.playerView.player = player

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Sportzfy/6.0 Android")
            .setAllowCrossProtocolRedirects(true)

        val mediaSource = when {
            streamUrl.contains(".mpd", ignoreCase = true) ->
                DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamUrl))
            else ->
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamUrl))
        }

        player?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> binding.progressBuffering.visibility = View.VISIBLE
                        Player.STATE_READY     -> binding.progressBuffering.visibility = View.GONE
                        Player.STATE_ENDED     -> finish()
                        else                   -> {}
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    binding.progressBuffering.visibility = View.GONE
                    showError(error.message ?: "Stream error")
                }
            })
        }
    }

    private fun setupControls() {
        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Play/Pause
        binding.btnPlayPause.setOnClickListener {
            player?.let {
                if (it.isPlaying) it.pause() else it.play()
                updatePlayPauseIcon()
            }
            scheduleHideControls()
        }

        // Lock button
        binding.btnLock.setOnClickListener {
            isLocked = true
            binding.topControls.visibility    = View.GONE
            binding.bottomControls.visibility = View.GONE
            binding.lockOverlay.visibility    = View.VISIBLE
        }

        // Unlock button
        binding.btnUnlock.setOnClickListener {
            isLocked = false
            binding.lockOverlay.visibility = View.GONE
            showControls()
        }

        // Quality
        binding.btnQuality.setOnClickListener {
            showQualityDialog()
            scheduleHideControls()
        }

        // Floating player
        binding.btnFloat.setOnClickListener {
            val intent = Intent(this, FloatingPlayerService::class.java).apply {
                putExtra(FloatingPlayerService.EXTRA_URL,   streamUrl)
                putExtra(FloatingPlayerService.EXTRA_TITLE, streamTitle)
            }
            startForegroundService(intent)
            finish()
        }

        // Tap to toggle controls
        binding.playerView.setOnClickListener {
            if (!isLocked) {
                if (controlsVisible) hideControls() else showControls()
            }
        }
    }

    private fun showQualityDialog() {
        val formats = trackSelector.currentMappedTrackInfo ?: return
        val items = arrayOf("🔄 Auto", "🔵 HD 1080p", "🟢 HD 720p", "🟡 SD 480p", "🟡 SD 360p")
        AlertDialog.Builder(this, R.style.QualityDialogTheme)
            .setTitle("Select Quality")
            .setItems(items) { _, which ->
                val params = trackSelector.buildUponParameters()
                when (which) {
                    0 -> params.setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                    1 -> params.setMaxVideoSize(1920, 1080)
                    2 -> params.setMaxVideoSize(1280, 720)
                    3 -> params.setMaxVideoSize(854, 480)
                    4 -> params.setMaxVideoSize(640, 360)
                }
                trackSelector.setParameters(params)
            }
            .show()
    }

    private fun showError(msg: String) {
        AlertDialog.Builder(this)
            .setTitle("Stream Error")
            .setMessage("Could not load stream.\n$msg")
            .setPositiveButton("Retry") { _, _ -> setupPlayer() }
            .setNegativeButton("Back")  { _, _ -> finish() }
            .show()
    }

    private fun showControls() {
        controlsVisible = true
        binding.topControls.visibility    = View.VISIBLE
        binding.bottomControls.visibility = View.VISIBLE
        scheduleHideControls()
    }

    private fun hideControls() {
        controlsVisible = false
        binding.topControls.visibility    = View.GONE
        binding.bottomControls.visibility = View.GONE
    }

    private fun scheduleHideControls() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 3500)
    }

    private fun updatePlayPauseIcon() {
        binding.btnPlayPause.text = if (player?.isPlaying == true) "⏸" else "▶"
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
        hideHandler.removeCallbacks(hideRunnable)
        player?.release()
        player = null
    }
}

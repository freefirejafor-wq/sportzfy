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
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.sportzfy.app.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URL   = "stream_url"
        const val EXTRA_TITLE = "stream_title"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector

    private var streamUrl   = ""
    private var streamTitle = ""
    private var isLocked    = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        streamUrl   = intent.getStringExtra(EXTRA_URL)   ?: ""
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

    // ─── Player ───────────────────────────────────────────────────────────────

    private fun setupPlayer() {
        trackSelector = DefaultTrackSelector(this)

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exo ->
                binding.playerView.player = exo

                val mediaItem = when {
                    streamUrl.contains(".m3u8") ->
                        MediaItem.Builder().setUri(streamUrl)
                            .setMimeType(MimeTypes.APPLICATION_M3U8).build()
                    streamUrl.contains(".mpd") ->
                        MediaItem.Builder().setUri(streamUrl)
                            .setMimeType(MimeTypes.APPLICATION_MPD).build()
                    else -> MediaItem.fromUri(streamUrl)
                }

                exo.setMediaItem(mediaItem)
                exo.prepare()
                exo.play()

                exo.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Toast.makeText(
                            this@PlayerActivity,
                            "স্ট্রিম লোড হয়নি: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        val hasVariants = tracks.groups.any { g ->
                            g.type == C.TRACK_TYPE_VIDEO && g.length > 1
                        }
                        binding.btnQuality.isEnabled = hasVariants
                    }
                })
            }
    }

    // ─── Controls ─────────────────────────────────────────────────────────────

    private fun setupControls() {
        // ← Back
        binding.btnBack.setOnClickListener {
            if (!isLocked) finish()
        }

        // 🔒 Lock
        binding.btnLock.setOnClickListener {
            isLocked = true
            binding.topControlsBar.visibility  = View.GONE
            binding.bottomControlsBar.visibility = View.GONE
            binding.lockOverlay.visibility      = View.VISIBLE
            Toast.makeText(this, "🔒 কন্ট্রোল লক হয়েছে", Toast.LENGTH_SHORT).show()
        }

        // 🔓 Unlock
        binding.btnUnlock.setOnClickListener {
            isLocked = false
            binding.lockOverlay.visibility      = View.GONE
            binding.topControlsBar.visibility  = View.VISIBLE
            binding.bottomControlsBar.visibility = View.VISIBLE
            Toast.makeText(this, "🔓 কন্ট্রোল আনলক হয়েছে", Toast.LENGTH_SHORT).show()
        }

        // 🎚️ Quality
        binding.btnQuality.setOnClickListener { showQualityDialog() }

        // 🪟 Floating
        binding.btnFloating.setOnClickListener { startFloatingPlayer() }

        // ⛶ Fullscreen toggle
        binding.btnFullscreen.setOnClickListener { toggleOrientation() }
    }

    // ─── Quality Dialog ────────────────────────────────────────────────────────

    private fun showQualityDialog() {
        val tracks = player?.currentTracks ?: return
        val videoGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }

        if (videoGroups.isEmpty()) {
            Toast.makeText(this, "কোয়ালিটি তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }

        val group   = videoGroups.first()
        val labels  = mutableListOf("Auto")
        val heights = mutableListOf(-1)

        for (i in 0 until group.length) {
            if (!group.isTrackSupported(i)) continue
            val fmt = group.getTrackFormat(i)
            val label = when {
                fmt.height >= 1080 -> "1080p Full HD"
                fmt.height >= 720  -> "720p HD"
                fmt.height >= 480  -> "480p SD"
                fmt.height >= 360  -> "360p"
                fmt.height > 0     -> "${fmt.height}p"
                else               -> "Track ${i + 1}"
            }
            labels.add(label)
            heights.add(fmt.height)
        }

        AlertDialog.Builder(this)
            .setTitle("কোয়ালিটি বেছে নিন")
            .setItems(labels.toTypedArray()) { _, which ->
                val h = heights[which]
                val params = trackSelector.buildUponParameters()
                if (h == -1) {
                    params.clearVideoSizeConstraints()
                    binding.btnQuality.text = "Auto"
                } else {
                    params.setMaxVideoSize(Int.MAX_VALUE, h).setMinVideoSize(0, h)
                    binding.btnQuality.text = labels[which].substringBefore(" ")
                }
                trackSelector.setParameters(params)
            }
            .show()
    }

    // ─── Floating Player ───────────────────────────────────────────────────────

    private fun startFloatingPlayer() {
        val intent = Intent(this, FloatingPlayerService::class.java).apply {
            putExtra(FloatingPlayerService.EXTRA_URL,   streamUrl)
            putExtra(FloatingPlayerService.EXTRA_TITLE, streamTitle)
        }
        startForegroundService(intent)
        player?.stop()
        finish()
    }

    // ─── Fullscreen ────────────────────────────────────────────────────────────

    private fun toggleOrientation() {
        requestedOrientation =
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun enableFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onPause()   { super.onPause();   player?.pause() }
    override fun onResume()  { super.onResume();  player?.play(); enableFullscreen() }
    override fun onDestroy() { super.onDestroy(); player?.release(); player = null }
}

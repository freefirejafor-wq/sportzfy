package com.sportzfy.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.sportzfy.app.databinding.FloatingPlayerBinding

class FloatingPlayerService : Service() {

    companion object {
        const val EXTRA_URL   = "stream_url"
        const val EXTRA_TITLE = "stream_title"
        const val CHANNEL_ID  = "sportzfy_floating"
        const val NOTIF_ID    = 1001
    }

    private var floatingView: android.view.View? = null
    private var windowManager: WindowManager? = null
    private var player: ExoPlayer? = null
    private lateinit var binding: FloatingPlayerBinding

    private var streamUrl   = ""
    private var streamTitle = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        streamUrl   = intent?.getStringExtra(EXTRA_URL)   ?: ""
        streamTitle = intent?.getStringExtra(EXTRA_TITLE) ?: "Live"
        startForeground(NOTIF_ID, buildNotification())
        showFloatingPlayer()
        return START_NOT_STICKY
    }

    private fun showFloatingPlayer() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        binding       = FloatingPlayerBinding.inflate(LayoutInflater.from(this))
        floatingView  = binding.root

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50; y = 200
        }

        windowManager?.addView(floatingView, params)
        setupDrag(params)
        setupPlayer()

        // Drag করে সরানো যাবে, controls লুকানো থাকবে
        binding.btnClose.setOnClickListener   { stopSelf() }
        binding.btnExpand.setOnClickListener  {
            startActivity(Intent(this, PlayerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(PlayerActivity.EXTRA_URL,   streamUrl)
                putExtra(PlayerActivity.EXTRA_TITLE, streamTitle)
            })
            stopSelf()
        }
    }

    private fun setupDrag(params: WindowManager.LayoutParams) {
        var ix = 0; var iy = 0; var tx = 0f; var ty = 0f
        floatingView?.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { ix = params.x; iy = params.y; tx = e.rawX; ty = e.rawY; true }
                MotionEvent.ACTION_MOVE -> {
                    params.x = ix + (e.rawX - tx).toInt()
                    params.y = iy + (e.rawY - ty).toInt()
                    windowManager?.updateViewLayout(floatingView, params); true
                }
                else -> false
            }
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.floatingPlayerView.player = exo
            val item = when {
                streamUrl.contains(".m3u8") ->
                    MediaItem.Builder().setUri(streamUrl).setMimeType(MimeTypes.APPLICATION_M3U8).build()
                streamUrl.contains(".mpd") ->
                    MediaItem.Builder().setUri(streamUrl).setMimeType(MimeTypes.APPLICATION_MPD).build()
                else -> MediaItem.fromUri(streamUrl)
            }
            exo.setMediaItem(item); exo.prepare(); exo.play()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "Sportzfy Player", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NotificationManager::class.java)).createNotificationChannel(ch)
        }
    }

    private fun buildNotification(): Notification {
        val pi = PendingIntent.getService(
            this, 0, Intent(this, FloatingPlayerService::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Floating Player")
            .setContentText(streamTitle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "বন্ধ করুন", pi)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release(); player = null
        floatingView?.let { windowManager?.removeView(it) }; floatingView = null
    }
}

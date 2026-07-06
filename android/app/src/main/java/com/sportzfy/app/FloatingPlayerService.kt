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
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.sportzfy.app.databinding.FloatingPlayerBinding

class FloatingPlayerService : Service() {

    companion object {
        const val EXTRA_URL = "stream_url"
        const val EXTRA_TITLE = "stream_title"
        const val CHANNEL_ID = "sportzfy_floating"
        const val NOTIF_ID = 1001
    }

    private var floatingView: View? = null
    private var windowManager: WindowManager? = null
    private var player: ExoPlayer? = null
    private lateinit var floatingBinding: FloatingPlayerBinding

    private var streamUrl = ""
    private var streamTitle = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        streamUrl = intent?.getStringExtra(EXTRA_URL) ?: ""
        streamTitle = intent?.getStringExtra(EXTRA_TITLE) ?: "Live Stream"

        startForeground(NOTIF_ID, buildNotification())
        showFloatingPlayer()

        return START_NOT_STICKY
    }

    private fun showFloatingPlayer() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = LayoutInflater.from(this)
        floatingBinding = FloatingPlayerBinding.inflate(inflater)
        floatingView = floatingBinding.root

        // Window params — screen-এর উপরে ভাসবে
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 200
        }

        windowManager?.addView(floatingView, params)

        // Drag করে সরানো যাবে
        setupDrag(params)

        // ExoPlayer শুরু করি
        setupPlayer()

        // Close button
        floatingBinding.btnClose.setOnClickListener {
            stopSelf()
        }

        // Expand — PlayerActivity-তে ফিরে যাও
        floatingBinding.btnExpand.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(PlayerActivity.EXTRA_URL, streamUrl)
                putExtra(PlayerActivity.EXTRA_TITLE, streamTitle)
            }
            startActivity(intent)
            stopSelf()
        }
    }

    private fun setupDrag(params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var touchX = 0f
        var touchY = 0f

        floatingView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - touchX).toInt()
                    params.y = initialY + (event.rawY - touchY).toInt()
                    windowManager?.updateViewLayout(floatingView, params)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            floatingBinding.floatingPlayerView.player = exoPlayer

            val mediaItem = when {
                streamUrl.contains(".m3u8") -> MediaItem.Builder()
                    .setUri(streamUrl)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()
                streamUrl.contains(".mpd") -> MediaItem.Builder()
                    .setUri(streamUrl)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()
                else -> MediaItem.fromUri(streamUrl)
            }

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, FloatingPlayerService::class.java)
        val stopPending = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.floating_player))
            .setContentText(streamTitle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "বন্ধ করুন", stopPending)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        floatingView?.let { windowManager?.removeView(it) }
        floatingView = null
    }
}

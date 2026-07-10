package com.sportzfy.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@UnstableApi
class FloatingPlayerService : Service() {

    companion object {
        const val EXTRA_STREAM_URL  = "stream_url"
        const val EXTRA_STREAM_NAME = "stream_name"
        const val EXTRA_MATCH_TITLE = "match_title"
        private const val CHANNEL_ID = "sportzfy_float"
        private const val NOTIF_ID   = 1001
    }

    private var player: ExoPlayer? = null
    private var floatingView: View? = null
    private var windowManager: WindowManager? = null
    private var initX = 0; private var initY = 0
    private var initTouchX = 0f; private var initTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() { super.onCreate(); createNotificationChannel() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url   = intent?.getStringExtra(EXTRA_STREAM_URL)  ?: return START_NOT_STICKY
        val name  = intent.getStringExtra(EXTRA_STREAM_NAME)  ?: ""
        val title = intent.getStringExtra(EXTRA_MATCH_TITLE)  ?: ""
        startForeground(NOTIF_ID, buildNotification(name, title))
        setupFloatingWindow(url, name, title)
        return START_NOT_STICKY
    }

    private fun setupFloatingWindow(url: String, name: String, title: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            380, 230,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 16; y = 120 }

        val view = LayoutInflater.from(this).inflate(R.layout.floating_player, null)
        floatingView = view

        // IDs from floating_player.xml:
        // floatPlayerView, tvFloatTitle, tvFloatName, btnFloatExpand, btnFloatClose, btnFloatPlayPause

        player = ExoPlayer.Builder(this).build().also { exo ->
            view.findViewById<PlayerView>(R.id.floatPlayerView).player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            exo.playWhenReady = true
        }

        view.findViewById<TextView>(R.id.tvFloatTitle).text = title
        view.findViewById<TextView>(R.id.tvFloatName).text  = name

        val btnPP = view.findViewById<ImageButton>(R.id.btnFloatPlayPause)
        btnPP.setOnClickListener {
            player?.let { p ->
                if (p.isPlaying) { p.pause(); btnPP.setImageResource(android.R.drawable.ic_media_play) }
                else             { p.play();  btnPP.setImageResource(android.R.drawable.ic_media_pause) }
            }
        }

        view.findViewById<ImageButton>(R.id.btnFloatExpand).setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(PlayerActivity.EXTRA_STREAM_URL,  url)
                putExtra(PlayerActivity.EXTRA_STREAM_NAME, name)
                putExtra(PlayerActivity.EXTRA_MATCH_TITLE, title)
            })
            stopSelf()
        }

        view.findViewById<ImageButton>(R.id.btnFloatClose).setOnClickListener { stopSelf() }

        view.setOnTouchListener { _, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initX = params.x; initY = params.y
                    initTouchX = ev.rawX; initTouchY = ev.rawY; true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = (initX + (ev.rawX - initTouchX)).toInt()
                    params.y = (initY + (ev.rawY - initTouchY)).toInt()
                    windowManager?.updateViewLayout(view, params); true
                }
                else -> false
            }
        }
        windowManager?.addView(view, params)
    }

    private fun buildNotification(name: String, title: String): Notification {
        val stopPending = PendingIntent.getService(
            this, 0, Intent(this, FloatingPlayerService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sportzfy — $name")
            .setContentText(if (title.isNotEmpty()) title else "Floating Player")
            .setSmallIcon(R.drawable.ic_nav_live)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(0, "Stop", stopPending)
            .build()
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(CHANNEL_ID, "Floating Player", NotificationManager.IMPORTANCE_LOW)
            .also { it.description = "Mini floating video player" }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
    }

    override fun onDestroy() {
        player?.release(); player = null
        floatingView?.let { windowManager?.removeView(it) }
        super.onDestroy()
    }
}

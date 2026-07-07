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
import android.view.WindowManager
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@UnstableApi
class FloatingPlayerService : Service() {

    companion object {
        const val EXTRA_URL   = "url"
        const val EXTRA_NAME  = "name"
        const val EXTRA_TITLE = "title"
        const val EXTRA_POS   = "position"
        private const val CHANNEL_ID = "sportzfy_floating"
        private const val NOTIF_ID   = 1001
    }

    private var windowManager: WindowManager? = null
    private var floatingView: android.view.View? = null
    private var player: ExoPlayer? = null
    private lateinit var params: WindowManager.LayoutParams

    private var lastX = 0; private var lastY = 0
    private var initX = 0; private var initY = 0
    private var touchX = 0f; private var touchY = 0f
    private var moved = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url   = intent?.getStringExtra(EXTRA_URL)   ?: return START_NOT_STICKY
        val name  = intent.getStringExtra(EXTRA_NAME)   ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE)  ?: ""
        val pos   = intent.getLongExtra(EXTRA_POS, 0L)

        startForeground(NOTIF_ID, buildNotification(name, title))
        showFloatingWindow(url, name, pos)
        return START_STICKY
    }

    private fun showFloatingWindow(url: String, name: String, position: Long) {
        // Remove existing
        floatingView?.let { windowManager?.removeView(it); floatingView = null }
        player?.release(); player = null

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager = wm

        // Inflate floating layout
        val view = LayoutInflater.from(this).inflate(R.layout.floating_player, null)
        floatingView = view

        // ExoPlayer in floating view
        val playerView = view.findViewById<PlayerView>(R.id.floatingPlayerView)
        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView.player = exo
            playerView.useController = false
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            if (position > 0) exo.seekTo(position)
            exo.playWhenReady = true
        }

        val dpW = (resources.displayMetrics.density * 320).toInt()
        val dpH = (resources.displayMetrics.density * 180).toInt()

        params = WindowManager.LayoutParams(
            dpW, dpH,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = resources.displayMetrics.widthPixels - dpW - 20
            y = resources.displayMetrics.heightPixels - dpH - 200
        }

        // Drag handling
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = params.x; lastY = params.y
                    initX = params.x; initY = params.y
                    touchX = event.rawX; touchY = event.rawY
                    moved = false; true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) moved = true
                    params.x = lastX + dx; params.y = lastY + dy
                    wm.updateViewLayout(view, params); true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) { v.performClick() }
                    true
                }
                else -> false
            }
        }

        // Expand button
        view.findViewById<ImageButton>(R.id.btnExpand).setOnClickListener {
            // Not moved — expand to full player
            val intent = Intent(this, PlayerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PlayerActivity.EXTRA_STREAM_URL, url)
                putExtra(PlayerActivity.EXTRA_STREAM_NAME, name)
                putExtra(PlayerActivity.EXTRA_MATCH_TITLE, "")
            }
            startActivity(intent)
            stopSelf()
        }

        // Close button
        view.findViewById<ImageButton>(R.id.btnFloatClose).setOnClickListener {
            stopSelf()
        }

        wm.addView(view, params)
    }

    private fun buildNotification(name: String, title: String): Notification {
        val stopIntent = Intent(this, FloatingPlayerService::class.java)
        val stopPending = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sportzfy — $name")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_nav_live)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(0, "Stop", stopPending)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Floating Player",
            NotificationManager.IMPORTANCE_LOW
        ).also { it.description = "Mini floating video player" }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    override fun onDestroy() {
        player?.release(); player = null
        floatingView?.let { windowManager?.removeView(it) }
        super.onDestroy()
    }
}

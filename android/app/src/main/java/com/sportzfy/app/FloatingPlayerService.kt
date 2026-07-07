package com.sportzfy.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.PlayerView
import android.widget.ImageButton

class FloatingPlayerService : Service() {

    companion object {
        const val EXTRA_URL   = "float_url"
        const val EXTRA_TITLE = "float_title"
        private const val CHANNEL_ID = "sportzfy_float"
    }

    private var windowManager: WindowManager? = null
    private var floatView: android.view.View? = null
    private var player: ExoPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url   = intent?.getStringExtra(EXTRA_URL)   ?: return START_NOT_STICKY
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Live Stream"

        setupFloatingWindow(url)
        return START_STICKY
    }

    private fun setupFloatingWindow(url: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatView = LayoutInflater.from(this).inflate(R.layout.floating_player, null)

        val params = WindowManager.LayoutParams(
            320.dp, 180.dp,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100; y = 100
        }

        // Drag to move
        var lastX = 0; var lastY = 0; var startX = 0; var startY = 0
        floatView?.setOnTouchListener { v, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = ev.rawX.toInt(); lastY = ev.rawY.toInt()
                    startX = params.x; startY = params.y
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = startX + (ev.rawX.toInt() - lastX)
                    params.y = startY + (ev.rawY.toInt() - lastY)
                    windowManager?.updateViewLayout(floatView, params)
                }
            }
            false
        }

        // Expand button
        floatView?.findViewById<ImageButton>(R.id.btnExpand)?.setOnClickListener {
            val i = Intent(this, PlayerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(PlayerActivity.EXTRA_URL, url)
            }
            startActivity(i)
            stopSelf()
        }

        // Close button
        floatView?.findViewById<ImageButton>(R.id.btnFloatClose)?.setOnClickListener {
            stopSelf()
        }

        // ExoPlayer
        player = ExoPlayer.Builder(this).build()
        floatView?.findViewById<PlayerView>(R.id.floatPlayerView)?.player = player

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Sportzfy/6.0 Android")

        val mediaSource = when {
            url.contains(".mpd", ignoreCase = true) ->
                DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url))
            else ->
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url))
        }

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true

        windowManager?.addView(floatView, params)
    }

    private val Int.dp get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        floatView?.let { windowManager?.removeView(it) }
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.floating_player))
            .setContentText("Sportzfy is playing")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
}

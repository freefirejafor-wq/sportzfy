package com.sportzfy.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fade in animation
        val fade = AlphaAnimation(0f, 1f).apply { duration = 600; fillAfter = true }
        binding.root.startAnimation(fade)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1800)
    }
}

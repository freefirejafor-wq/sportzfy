package com.sportzfy.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.AnimationSet
import androidx.appcompat.app.AppCompatActivity
import com.sportzfy.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo
        val scaleAnim = ScaleAnimation(
            0.5f, 1f, 0.5f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 600; fillAfter = true }

        val fadeAnim = AlphaAnimation(0f, 1f).apply {
            duration = 600; fillAfter = true
        }

        val set = AnimationSet(true).apply {
            addAnimation(scaleAnim)
            addAnimation(fadeAnim)
        }

        binding.logoContainer.startAnimation(set)

        // Fade in version text after 400ms
        Handler(Looper.getMainLooper()).postDelayed({
            val versionFade = AlphaAnimation(0f, 1f).apply { duration = 400; fillAfter = true }
            binding.versionText.startAnimation(versionFade)
        }, 400)

        // Go to MainActivity after 1.8s
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1800)
    }
}

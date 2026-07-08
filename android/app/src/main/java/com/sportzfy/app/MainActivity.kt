package com.sportzfy.app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.sportzfy.app.databinding.ActivityMainBinding
import com.sportzfy.app.ui.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
            R.string.app_name, R.string.app_name)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            loadFragment(LiveEventsFragment())
            binding.bottomNav.selectedItemId = R.id.nav_live
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_live       -> { loadFragment(LiveEventsFragment()); true }
                R.id.nav_sports     -> { loadFragment(SportsFragment()); true }
                R.id.nav_categories -> { loadFragment(CategoriesFragment()); true }
                R.id.nav_favorites  -> { loadFragment(FavoritesFragment()); true }
                R.id.nav_highlights -> { loadFragment(HighlightsFragment()); true }
                else -> false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.drawer_cricket_score   -> openWeb("https://www.cricbuzz.com", "Cricket Score")
            R.id.drawer_football_score  -> openWeb("https://www.livescore.com", "Football Score")
            R.id.drawer_network_stream  -> { loadFragment(NetworkStreamFragment()); binding.bottomNav.menu.findItem(R.id.nav_live)?.isChecked = false }
            R.id.drawer_playlists       -> { loadFragment(PlaylistFragment()); binding.bottomNav.menu.findItem(R.id.nav_live)?.isChecked = false }
            R.id.drawer_floating_player -> Toast.makeText(this, "Start playing a stream first", Toast.LENGTH_SHORT).show()
            R.id.drawer_force_low       -> {
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val isLow = prefs.getBoolean("force_low_quality", false)
                prefs.edit().putBoolean("force_low_quality", !isLow).apply()
                val msg = if (!isLow) "✅ Force Low Quality ON" else "❌ Force Low Quality OFF"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            R.id.drawer_settings        -> { loadFragment(SettingsFragment()); binding.bottomNav.menu.findItem(R.id.nav_live)?.isChecked = false }
            R.id.drawer_share           -> shareApp()
            R.id.drawer_telegram        -> openWeb("https://t.me/sportzfyapp", "Telegram")
            R.id.drawer_website         -> openWeb("https://sportzfy.app", "Website")
            R.id.drawer_whatsapp        -> openWeb("https://wa.me/message/sportzfy", "WhatsApp")
            R.id.drawer_exit            -> finishAffinity()
        }
        return true
    }

    private fun openWeb(url: String, title: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Watch live sports free! Download Sportzfy: https://sportzfy.app")
        }
        startActivity(Intent.createChooser(intent, "Share Sportzfy"))
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }
}

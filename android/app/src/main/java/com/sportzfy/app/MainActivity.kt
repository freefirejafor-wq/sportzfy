package com.sportzfy.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sportzfy.app.databinding.ActivityMainBinding
import com.sportzfy.app.ui.CategoriesFragment
import com.sportzfy.app.ui.LiveEventsFragment
import com.sportzfy.app.ui.SportsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default tab
        if (savedInstanceState == null) {
            loadFragment(LiveEventsFragment())
            binding.bottomNav.selectedItemId = R.id.nav_live
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_sports    -> { loadFragment(SportsFragment());     true }
                R.id.nav_live      -> { loadFragment(LiveEventsFragment()); true }
                R.id.nav_categories-> { loadFragment(CategoriesFragment()); true }
                else               -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

package com.sportzfy.app.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sportzfy.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Load saved settings
        binding.switchForceLow.isChecked = prefs.getBoolean("force_low_quality", false)
        binding.switchAutoplay.isChecked = prefs.getBoolean("autoplay", true)
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications", true)
        binding.switchPip.isChecked = prefs.getBoolean("pip_mode", true)

        binding.switchForceLow.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("force_low_quality", checked).apply()
            Toast.makeText(requireContext(), if (checked) "Low quality mode ON" else "Low quality mode OFF", Toast.LENGTH_SHORT).show()
        }
        binding.switchAutoplay.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("autoplay", checked).apply()
        }
        binding.switchNotifications.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("notifications", checked).apply()
        }
        binding.switchPip.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("pip_mode", checked).apply()
        }

        binding.tvAppVersion.text = "Sportzfy v8.0"
        binding.tvBuildInfo.text = "Build 800 · Kotlin · ExoPlayer"
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.databinding.FragmentNetworkStreamBinding

class NetworkStreamFragment : Fragment() {
    private var _binding: FragmentNetworkStreamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNetworkStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlayStream.setOnClickListener {
            val url = binding.etStreamUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("rtmp://")) {
                Toast.makeText(requireContext(), "Invalid URL format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("stream_url", url)
                putExtra("stream_name", binding.etStreamName.text.toString().trim().ifEmpty { "Network Stream" })
                putExtra("stream_title", "Custom Stream")
            }
            startActivity(intent)
        }

        // Recent stream history
        binding.btnClearHistory.setOnClickListener {
            requireContext().getSharedPreferences("ns_history", android.content.Context.MODE_PRIVATE)
                .edit().clear().apply()
            binding.etStreamUrl.setText("")
            Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

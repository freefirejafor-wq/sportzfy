package com.sportzfy.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sportzfy.app.data.SPORT_CHANNELS
import com.sportzfy.app.databinding.FragmentSportsBinding

class SportsFragment : Fragment() {

    private var _binding: FragmentSportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ChannelAdapter { channel ->
            StreamPickerBottomSheet.newInstance(channel.name)
                .show(parentFragmentManager, "StreamPicker")
        }

        binding.recyclerChannels.apply {
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = adapter
        }

        adapter.submitList(SPORT_CHANNELS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

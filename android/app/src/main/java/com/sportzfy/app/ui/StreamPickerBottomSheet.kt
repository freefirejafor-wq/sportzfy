package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.R
import com.sportzfy.app.data.ALL_STREAMS
import com.sportzfy.app.data.Stream
import com.sportzfy.app.databinding.BottomSheetStreamPickerBinding
import com.sportzfy.app.databinding.ItemStreamBinding

class StreamPickerBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetStreamPickerBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TITLE = "title"
        fun newInstance(title: String) = StreamPickerBottomSheet().apply {
            arguments = Bundle().apply { putString(ARG_TITLE, title) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetStreamPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textMatchTitle.text = arguments?.getString(ARG_TITLE) ?: "Select Stream"

        val adapter = StreamAdapter { stream ->
            dismiss()
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.EXTRA_URL,   stream.url)
                putExtra(PlayerActivity.EXTRA_TITLE, stream.name)
            }
            startActivity(intent)
        }

        binding.recyclerStreams.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter  = adapter
        }

        adapter.submitList(ALL_STREAMS)

        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Stream list adapter ──────────────────────────────────────────────────────

class StreamAdapter(
    private val onClick: (Stream) -> Unit
) : ListAdapter<Stream, StreamAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemStreamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStreamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val stream = getItem(position)
        with(holder.binding) {
            textStreamName.text = stream.name
            textQuality.text    = stream.quality

            val (bgColor, textColor) = when (stream.quality) {
                "FHD" -> Pair(0xFF003344.toInt(), 0xFF00D4E8.toInt())
                "HD"  -> Pair(0xFF003300.toInt(), 0xFF44DD44.toInt())
                else  -> Pair(0xFF332200.toInt(), 0xFFFFAA00.toInt())
            }
            textQuality.setBackgroundColor(bgColor)
            textQuality.setTextColor(textColor)

            root.setOnClickListener { onClick(stream) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Stream>() {
            override fun areItemsTheSame(a: Stream, b: Stream) = a.url == b.url
            override fun areContentsTheSame(a: Stream, b: Stream) = a == b
        }
    }
}

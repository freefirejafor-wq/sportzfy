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
import com.google.android.material.chip.Chip
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.R
import com.sportzfy.app.data.ALL_STREAMS
import com.sportzfy.app.data.Quality
import com.sportzfy.app.data.STREAM_CATEGORIES
import com.sportzfy.app.data.Stream
import com.sportzfy.app.data.getStreamsByCategory
import com.sportzfy.app.databinding.BottomSheetStreamPickerBinding

class StreamPickerBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetStreamPickerBinding? = null
    private val binding get() = _binding!!
    private lateinit var streamAdapter: StreamAdapter
    private var matchTitle: String = ""

    companion object {
        private const val ARG_TITLE = "match_title"
        fun newInstance(matchTitle: String) = StreamPickerBottomSheet().apply {
            arguments = Bundle().also { it.putString(ARG_TITLE, matchTitle) }
        }
    }

    override fun getTheme() = R.style.BottomSheetTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetStreamPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        matchTitle = arguments?.getString(ARG_TITLE) ?: "Watch Now"
        binding.tvMatchTitle.text = matchTitle

        streamAdapter = StreamAdapter { stream ->
            dismiss()
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.EXTRA_STREAM_URL,  stream.url)
                putExtra(PlayerActivity.EXTRA_STREAM_NAME, stream.name)
                putExtra(PlayerActivity.EXTRA_MATCH_TITLE, matchTitle)
                putExtra(PlayerActivity.EXTRA_FORMAT,      stream.format)
                // Pass DRM info only if present (NongorPlay DASH streams)
                stream.drmKid?.let { putExtra(PlayerActivity.EXTRA_DRM_KID, it) }
                stream.drmKey?.let { putExtra(PlayerActivity.EXTRA_DRM_KEY, it) }
            }
            requireContext().startActivity(intent)
        }

        binding.recyclerStreams.layoutManager = LinearLayoutManager(context)
        binding.recyclerStreams.adapter = streamAdapter
        streamAdapter.submitList(ALL_STREAMS)

        // Category chips
        STREAM_CATEGORIES.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                setChipBackgroundColorResource(R.color.chip_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.accent)
            }
            chip.setOnClickListener { streamAdapter.submitList(getStreamsByCategory(cat)) }
            binding.chipGroupCategories.addView(chip)
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Stream list adapter ───────────────────────────────────────────
    class StreamAdapter(
        private val onClick: (Stream) -> Unit
    ) : ListAdapter<Stream, StreamAdapter.VH>(DIFF) {

        companion object {
            val DIFF = object : DiffUtil.ItemCallback<Stream>() {
                override fun areItemsTheSame(a: Stream, b: Stream) = a.id == b.id
                override fun areContentsTheSame(a: Stream, b: Stream) = a == b
            }
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView    = view.findViewById(R.id.tvStreamName)
            val tvQuality: TextView = view.findViewById(R.id.tvQuality)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stream, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val stream = getItem(position)
            holder.tvName.text = stream.name
            // Show format badge for DASH streams
            holder.tvQuality.text = if (stream.format == "dash") "${stream.quality.name} · DASH" else stream.quality.name
            holder.tvQuality.setTextColor(
                holder.itemView.context.getColor(
                    when (stream.quality) {
                        Quality.FHD -> R.color.quality_fhd
                        Quality.HD  -> R.color.quality_hd
                        Quality.SD  -> R.color.quality_sd
                    }
                )
            )
            holder.itemView.setOnClickListener { onClick(stream) }
        }
    }
}

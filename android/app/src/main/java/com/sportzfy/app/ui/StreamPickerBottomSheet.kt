package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import android.widget.TextView

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetStreamPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        matchTitle = arguments?.getString(ARG_TITLE) ?: "Watch Now"
        binding.tvMatchTitle.text = matchTitle

        streamAdapter = StreamAdapter { stream -> launchPlayer(stream) }
        binding.recyclerStreams.layoutManager = LinearLayoutManager(context)
        binding.recyclerStreams.adapter = streamAdapter
        streamAdapter.submitList(ALL_STREAMS)

        // Category filter chips
        STREAM_CATEGORIES.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                setChipBackgroundColorResource(R.color.chip_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.accent)
            }
            chip.setOnClickListener {
                streamAdapter.submitList(getStreamsByCategory(cat))
            }
            binding.chipGroupCategories.addView(chip)
        }

        // Custom URL / M3U8 button
        binding.btnAddCustomUrl.setOnClickListener { showCustomUrlDialog() }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun launchPlayer(stream: Stream) {
        dismiss()
        requireContext().startActivity(
            Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.EXTRA_STREAM_URL,  stream.url)
                putExtra(PlayerActivity.EXTRA_STREAM_NAME, stream.name)
                putExtra(PlayerActivity.EXTRA_MATCH_TITLE, matchTitle)
                putExtra(PlayerActivity.EXTRA_FORMAT,      stream.format)
                stream.drmKid?.let { putExtra(PlayerActivity.EXTRA_DRM_KID, it) }
                stream.drmKey?.let { putExtra(PlayerActivity.EXTRA_DRM_KEY, it) }
            }
        )
    }

    // ── Custom URL input dialog ───────────────────────────────────────
    private fun showCustomUrlDialog() {
        val ctx = requireContext()
        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 10)
        }
        val urlInput = EditText(ctx).apply {
            hint = "M3U8 / Stream URL দিন"
            inputType = InputType.TYPE_TEXT_VARIATION_URI or InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
        }
        val nameInput = EditText(ctx).apply {
            hint = "নাম (ঐচ্ছিক)"
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(0, 16, 0, 0)
        }
        layout.addView(urlInput)
        layout.addView(nameInput)

        AlertDialog.Builder(ctx, R.style.DarkDialogTheme)
            .setTitle("🔗 Custom URL / M3U8 যোগ করুন")
            .setView(layout)
            .setPositiveButton("▶ চালান") { _, _ ->
                val url  = urlInput.text.toString().trim()
                val name = nameInput.text.toString().trim().ifEmpty { "Custom Stream" }
                if (url.isNotEmpty()) {
                    dismiss()
                    val fmt = if (url.contains(".mpd", ignoreCase = true)) "dash" else "hls"
                    ctx.startActivity(
                        Intent(ctx, PlayerActivity::class.java).apply {
                            putExtra(PlayerActivity.EXTRA_STREAM_URL,  url)
                            putExtra(PlayerActivity.EXTRA_STREAM_NAME, name)
                            putExtra(PlayerActivity.EXTRA_MATCH_TITLE, matchTitle)
                            putExtra(PlayerActivity.EXTRA_FORMAT,      fmt)
                        }
                    )
                } else {
                    Toast.makeText(ctx, "URL দিতে হবে", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Stream list adapter ──────────────────────────────────────────
    class StreamAdapter(
        private val onClick: (Stream) -> Unit
    ) : ListAdapter<Stream, StreamAdapter.VH>(DIFF) {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName:    TextView = view.findViewById(R.id.tvStreamName)
            val tvQuality: TextView = view.findViewById(R.id.tvQuality)

            fun bind(stream: Stream) {
                tvName.text = stream.name
                tvQuality.text = stream.quality.name
                tvQuality.setTextColor(when (stream.quality) {
                    Quality.FHD -> 0xFF00D4E8.toInt()
                    Quality.HD  -> 0xFF4ADE80.toInt()
                    Quality.SD  -> 0xFFFBBF24.toInt()
                })
                itemView.setOnClickListener { onClick(stream) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stream, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

        companion object {
            val DIFF = object : DiffUtil.ItemCallback<Stream>() {
                override fun areItemsTheSame(a: Stream, b: Stream) = a.id == b.id
                override fun areContentsTheSame(a: Stream, b: Stream) = a == b
            }
        }
    }
}

package com.sportzfy.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportzfy.app.databinding.FragmentHighlightsBinding

data class Highlight(
    val title: String,
    val sport: String,
    val thumbnail: String,
    val youtubeUrl: String,
    val duration: String
)

class HighlightsFragment : Fragment() {
    private var _binding: FragmentHighlightsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHighlightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val highlights = listOf(
            Highlight("Bangladesh vs India - Full Highlights", "Cricket", "", "https://www.youtube.com/results?search_query=cricket+highlights", "8:42"),
            Highlight("Champions League Final Highlights", "Football", "", "https://www.youtube.com/results?search_query=champions+league+highlights", "12:15"),
            Highlight("Messi Hat-trick Highlights", "Football", "", "https://www.youtube.com/results?search_query=messi+highlights", "6:30"),
            Highlight("Test Match Day 1 Highlights", "Cricket", "", "https://www.youtube.com/results?search_query=test+cricket+highlights", "18:20"),
            Highlight("NBA Finals Game 7", "Basketball", "", "https://www.youtube.com/results?search_query=nba+finals+highlights", "10:05"),
            Highlight("F1 Grand Prix Race Highlights", "Formula 1", "", "https://www.youtube.com/results?search_query=f1+race+highlights", "7:18"),
            Highlight("ICC World Cup Best Moments", "Cricket", "", "https://www.youtube.com/results?search_query=icc+world+cup+highlights", "15:40"),
            Highlight("Premier League Weekend Goals", "Football", "", "https://www.youtube.com/results?search_query=premier+league+goals", "9:55"),
        )
        val adapter = HighlightAdapter(highlights) { hl ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(hl.youtubeUrl)))
        }
        binding.recyclerHighlights.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHighlights.adapter = adapter
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

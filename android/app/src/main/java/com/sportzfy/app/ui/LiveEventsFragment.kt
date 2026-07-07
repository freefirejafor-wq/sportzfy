package com.sportzfy.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportzfy.app.data.ALL_MATCHES
import com.sportzfy.app.data.Match
import com.sportzfy.app.databinding.FragmentLiveEventsBinding

class LiveEventsFragment : Fragment() {

    private var _binding: FragmentLiveEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MatchAdapter
    private var currentFilter = "live"
    private var searchQuery = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MatchAdapter { match ->
            StreamPickerBottomSheet.newInstance(match.league + " — " + match.homeTeam + " vs " + match.awayTeam)
                .show(parentFragmentManager, "StreamPicker")
        }

        binding.recyclerMatches.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@LiveEventsFragment.adapter
        }

        // Filter tabs
        binding.chipLive.setOnClickListener     { currentFilter = "live";     updateList() }
        binding.chipUpcoming.setOnClickListener { currentFilter = "upcoming"; updateList() }
        binding.chipFinished.setOnClickListener { currentFilter = "finished"; updateList() }

        // Search
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                updateList()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        updateList()
    }

    private fun updateList() {
        // Update chip appearance
        val activeColor   = requireContext().getColor(com.sportzfy.app.R.color.accent)
        val inactiveColor = requireContext().getColor(com.sportzfy.app.R.color.chip_inactive)
        binding.chipLive.setTextColor(    if (currentFilter == "live")     activeColor else inactiveColor)
        binding.chipUpcoming.setTextColor(if (currentFilter == "upcoming") activeColor else inactiveColor)
        binding.chipFinished.setTextColor(if (currentFilter == "finished") activeColor else inactiveColor)

        val filtered = ALL_MATCHES.filter { match ->
            match.status == currentFilter &&
            (searchQuery.isEmpty() ||
             match.homeTeam.contains(searchQuery, true) ||
             match.awayTeam.contains(searchQuery, true) ||
             match.league.contains(searchQuery, true))
        }
        adapter.submitList(filtered)

        binding.emptyView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

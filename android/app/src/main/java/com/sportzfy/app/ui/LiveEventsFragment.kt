package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.data.ALL_STREAMS
import com.sportzfy.app.data.SportEvent
import com.sportzfy.app.data.SportsRepository
import com.sportzfy.app.databinding.FragmentLiveEventsBinding

class LiveEventsFragment : Fragment() {

    private var _binding: FragmentLiveEventsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MatchAdapter
    private val refreshHandler = Handler(Looper.getMainLooper())
    private val REFRESH_INTERVAL = 30_000L

    private var allEvents: List<SportEvent> = emptyList()
    private var currentFilter = "all"   // all | live | upcoming | finished
    private var searchQuery = ""

    private val refreshRunnable = Runnable {
        loadData()
        scheduleRefresh()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFilters()
        setupSearch()
        setupSwipeRefresh()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = MatchAdapter { event ->
            // Open stream picker bottom sheet
            val sheet = StreamPickerBottomSheet.newInstance(
                matchTitle = "${event.homeName} vs ${event.awayName}"
            )
            sheet.show(childFragmentManager, "streams")
        }
        binding.recyclerMatches.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LiveEventsFragment.adapter
            setHasFixedSize(false)
        }
    }

    private fun setupFilters() {
        fun selectChip(filter: String) {
            currentFilter = filter
            binding.chipAll.isSelected      = filter == "all"
            binding.chipLive.isSelected     = filter == "live"
            binding.chipUpcoming.isSelected = filter == "upcoming"
            binding.chipFinished.isSelected = filter == "finished"
            applyFilter()
        }
        binding.chipAll.setOnClickListener      { selectChip("all") }
        binding.chipLive.setOnClickListener     { selectChip("live") }
        binding.chipUpcoming.setOnClickListener { selectChip("upcoming") }
        binding.chipFinished.setOnClickListener { selectChip("finished") }
        selectChip("all")
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            searchQuery = text?.toString()?.trim() ?: ""
            applyFilter()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            requireContext().getColor(com.sportzfy.app.R.color.accent)
        )
        binding.swipeRefresh.setOnRefreshListener { loadData() }
    }

    private fun loadData() {
        binding.swipeRefresh.isRefreshing = true
        binding.tvError.visibility = View.GONE

        SportsRepository.fetchWorldCup { events ->
            binding.swipeRefresh.isRefreshing = false
            if (events != null) {
                allEvents = events
                applyFilter()
                binding.tvLastUpdated.text = "Updated just now"
                binding.tvError.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "⚠ Connection error — retrying in 30s"
            }
        }
    }

    private fun applyFilter() {
        var filtered = allEvents

        // Status filter
        if (currentFilter != "all") {
            filtered = filtered.filter { it.status == currentFilter }
        }

        // Search filter
        if (searchQuery.isNotEmpty()) {
            val q = searchQuery.lowercase()
            filtered = filtered.filter {
                it.homeName.lowercase().contains(q) ||
                it.awayName.lowercase().contains(q) ||
                it.league.lowercase().contains(q)
            }
        }

        adapter.submitList(filtered)

        if (filtered.isEmpty() && allEvents.isNotEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.recyclerMatches.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.recyclerMatches.visibility = View.VISIBLE
        }
    }

    private fun scheduleRefresh() {
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    override fun onResume() {
        super.onResume()
        scheduleRefresh()
    }

    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

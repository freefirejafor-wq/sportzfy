package com.sportzfy.app.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sportzfy.app.data.Match
import com.sportzfy.app.databinding.ItemMatchBinding

class MatchAdapter(
    private val onClick: (Match) -> Unit
) : ListAdapter<Match, MatchAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val match = getItem(position)
        with(holder.binding) {
            // League
            textLeagueIcon.text = match.leagueIcon
            textLeague.text = match.league

            // Teams
            textHomeFlag.text = match.homeFlag
            textHomeName.text = match.homeTeam
            textAwayFlag.text = match.awayFlag
            textAwayName.text = match.awayTeam

            // Status badge
            when (match.status) {
                "live" -> {
                    badgeContainer.setBackgroundResource(com.sportzfy.app.R.drawable.bg_badge_live)
                    textBadge.text = "🔴 LIVE"
                    textBadge.setTextColor(Color.parseColor("#FF4444"))
                    textTime.text = match.time
                }
                "upcoming" -> {
                    badgeContainer.setBackgroundResource(com.sportzfy.app.R.drawable.bg_badge_upcoming)
                    textBadge.text = "⏰ " + match.time
                    textBadge.setTextColor(Color.parseColor("#00D4E8"))
                    textTime.text = ""
                }
                "finished" -> {
                    badgeContainer.setBackgroundResource(com.sportzfy.app.R.drawable.bg_badge_finished)
                    textBadge.text = "FT"
                    textBadge.setTextColor(Color.parseColor("#888888"))
                    textTime.text = ""
                }
            }

            // HOT badge
            hotBadge.visibility = if (match.hot) android.view.View.VISIBLE else android.view.View.GONE

            // Click — only for live/finished (upcoming has no streams yet)
            root.setOnClickListener {
                if (match.status != "upcoming") onClick(match)
            }
            root.alpha = if (match.status == "upcoming") 0.7f else 1f
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Match>() {
            override fun areItemsTheSame(a: Match, b: Match) = a.id == b.id
            override fun areContentsTheSame(a: Match, b: Match) = a == b
        }
    }
}

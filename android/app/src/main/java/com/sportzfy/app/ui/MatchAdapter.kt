package com.sportzfy.app.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sportzfy.app.R
import com.sportzfy.app.data.SportEvent
import com.sportzfy.app.databinding.ItemMatchBinding

class MatchAdapter(
    private val onWatch: (SportEvent) -> Unit
) : ListAdapter<SportEvent, MatchAdapter.VH>(DIFF) {

    inner class VH(val b: ItemMatchBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ev = getItem(position)
        val b = holder.b
        val ctx = b.root.context

        // ── Sport / League header ─────────────────────────────────
        b.tvSport.text = ev.sport.uppercase()
        b.tvLeague.text = ev.league

        // ── Status badge ─────────────────────────────────────────
        when (ev.status) {
            "live" -> {
                b.tvStatus.text = if (ev.clock != null) ev.clock else "LIVE"
                b.tvStatus.setTextColor(ctx.getColor(R.color.live_red))
                b.statusBadge.setBackgroundResource(R.drawable.bg_badge_live)
                b.liveDot.visibility = View.VISIBLE
                pulseDot(b.liveDot)
            }
            "upcoming" -> {
                b.tvStatus.text = "UPCOMING"
                b.tvStatus.setTextColor(ctx.getColor(R.color.upcoming_orange))
                b.statusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming)
                b.liveDot.visibility = View.GONE
            }
            else -> {
                b.tvStatus.text = "FT"
                b.tvStatus.setTextColor(ctx.getColor(R.color.finished_blue))
                b.statusBadge.setBackgroundResource(R.drawable.bg_badge_finished)
                b.liveDot.visibility = View.GONE
            }
        }

        // ── HOT badge ────────────────────────────────────────────
        b.hotBadge.visibility = if (ev.hot) View.VISIBLE else View.GONE

        // ── Team logos (Glide) ────────────────────────────────────
        loadLogo(b.imgHome, ev.homeLogo, ev.homeName)
        loadLogo(b.imgAway, ev.awayLogo, ev.awayName)

        // ── Team names ───────────────────────────────────────────
        b.tvHomeName.text = ev.homeName
        b.tvAwayName.text = ev.awayName

        // ── Score or VS ──────────────────────────────────────────
        if (ev.status == "live" || ev.status == "finished") {
            b.tvScore.visibility = View.VISIBLE
            b.tvVs.visibility = View.GONE
            b.tvTime.visibility = View.GONE
            b.tvScore.text = "${ev.homeScore}  –  ${ev.awayScore}"
        } else {
            b.tvScore.visibility = View.GONE
            b.tvVs.visibility = View.VISIBLE
            b.tvTime.visibility = View.VISIBLE
            b.tvTime.text = ev.displayTime
        }

        // ── Venue ────────────────────────────────────────────────
        if (!ev.venue.isNullOrEmpty()) {
            b.tvVenue.visibility = View.VISIBLE
            b.tvVenue.text = "📍 ${ev.venue}"
        } else {
            b.tvVenue.visibility = View.GONE
        }

        // ── Watch button ─────────────────────────────────────────
        if (ev.status == "finished") {
            b.btnWatch.visibility = View.GONE
        } else {
            b.btnWatch.visibility = View.VISIBLE
            if (ev.status == "live") {
                b.btnWatch.setBackgroundResource(R.drawable.bg_btn_watch_live)
                b.btnWatch.text = "▶  Watch Live"
            } else {
                b.btnWatch.setBackgroundResource(R.drawable.bg_btn_watch)
                b.btnWatch.text = "📺  Set Reminder"
            }
            b.btnWatch.setOnClickListener { onWatch(ev) }
        }
    }

    private fun loadLogo(imageView: android.widget.ImageView, url: String, name: String) {
        if (url.isNotEmpty()) {
            Glide.with(imageView)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .circleCrop()
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_channel_placeholder)
        }
    }

    private fun pulseDot(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.4f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.4f, 1f)
        val anim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        anim.duration = 1000
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.interpolator = OvershootInterpolator()
        anim.start()
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SportEvent>() {
            override fun areItemsTheSame(a: SportEvent, b: SportEvent) = a.id == b.id
            override fun areContentsTheSame(a: SportEvent, b: SportEvent) = a == b
        }
    }
}

package com.sportzfy.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sportzfy.app.R
import com.sportzfy.app.databinding.ItemChannelBinding

class ChannelAdapter(
    private val onClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.VH>(DIFF) {

    inner class VH(val b: ItemChannelBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = holder.b
        val channel = getItem(position)

        ch.tvChannelName.text = channel.name

        // 🔴 LIVE badge — show for NongorPlay DASH streams
        if (ch.root.findViewById<View?>(R.id.tvLiveBadge) != null) {
            ch.root.findViewById<android.widget.TextView>(R.id.tvLiveBadge)?.visibility =
                if (channel.streamUrl != null) View.VISIBLE else View.GONE
        }

        // Logo via Glide
        if (channel.logoUrl.isNotEmpty()) {
            Glide.with(ch.imgChannel)
                .load(channel.logoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .centerInside()
                .into(ch.imgChannel)
        } else {
            ch.imgChannel.setImageResource(R.drawable.ic_channel_placeholder)
        }

        ch.root.setOnClickListener { onClick(channel) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(a: Channel, b: Channel) = a.id == b.id
            override fun areContentsTheSame(a: Channel, b: Channel) = a == b
        }
    }
}

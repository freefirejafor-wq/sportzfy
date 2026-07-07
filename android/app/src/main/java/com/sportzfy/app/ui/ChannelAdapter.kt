package com.sportzfy.app.ui

import android.view.LayoutInflater
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = getItem(position)
        holder.b.tvChannelName.text = ch.name

        if (ch.logoUrl.isNotEmpty()) {
            Glide.with(holder.b.imgChannel)
                .load(ch.logoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .centerInside()
                .into(holder.b.imgChannel)
        } else {
            holder.b.imgChannel.setImageResource(R.drawable.ic_channel_placeholder)
        }

        holder.b.root.setOnClickListener { onClick(ch) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(a: Channel, b: Channel) = a.id == b.id
            override fun areContentsTheSame(a: Channel, b: Channel) = a == b
        }
    }
}

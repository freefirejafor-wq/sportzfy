package com.sportzfy.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sportzfy.app.data.Channel
import com.sportzfy.app.databinding.ItemChannelBinding

class ChannelAdapter(
    private val onClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val channel = getItem(position)
        with(holder.binding) {
            textChannelName.text = channel.name

            Glide.with(root.context)
                .load(channel.logo)
                .placeholder(com.sportzfy.app.R.drawable.ic_channel_placeholder)
                .error(com.sportzfy.app.R.drawable.ic_channel_placeholder)
                .into(imageChannelLogo)

            root.setOnClickListener { onClick(channel) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(a: Channel, b: Channel) = a.id == b.id
            override fun areContentsTheSame(a: Channel, b: Channel) = a == b
        }
    }
}

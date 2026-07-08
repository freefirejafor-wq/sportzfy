package com.sportzfy.app.ui

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sportzfy.app.R
import com.sportzfy.app.data.FavoriteChannel

class FavoriteAdapter(
    private val items: List<FavoriteChannel>,
    private val onPlay: (FavoriteChannel) -> Unit,
    private val onRemove: (FavoriteChannel) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgChannel)
        val name: TextView = v.findViewById(R.id.tvChannelName)
        val btnRemove: View = v.findViewById(R.id.btnRemoveFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        Glide.with(holder.img).load(item.logo).placeholder(R.drawable.ic_channel_placeholder).into(holder.img)
        holder.itemView.setOnClickListener { onPlay(item) }
        holder.btnRemove.setOnClickListener { onRemove(item) }
    }
}

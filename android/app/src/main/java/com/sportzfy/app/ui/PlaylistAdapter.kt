package com.sportzfy.app.ui

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sportzfy.app.R
import com.sportzfy.app.data.SavedPlaylist

class PlaylistAdapter(
    private val items: List<SavedPlaylist>,
    private val onPlay: (SavedPlaylist) -> Unit,
    private val onDelete: (SavedPlaylist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvPlaylistTitle)
        val url: TextView = v.findViewById(R.id.tvPlaylistUrl)
        val type: TextView = v.findViewById(R.id.tvPlaylistType)
        val btnDelete: View = v.findViewById(R.id.btnDeletePlaylist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.url.text = item.url
        holder.type.text = item.type.uppercase()
        holder.itemView.setOnClickListener { onPlay(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }
}

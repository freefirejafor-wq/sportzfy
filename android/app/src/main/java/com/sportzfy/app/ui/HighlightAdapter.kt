package com.sportzfy.app.ui

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sportzfy.app.R

class HighlightAdapter(
    private val items: List<Highlight>,
    private val onClick: (Highlight) -> Unit
) : RecyclerView.Adapter<HighlightAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvHighlightTitle)
        val sport: TextView = v.findViewById(R.id.tvHighlightSport)
        val duration: TextView = v.findViewById(R.id.tvHighlightDuration)
        val thumb: View = v.findViewById(R.id.layoutThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_highlight, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.sport.text = item.sport
        holder.duration.text = item.duration
        holder.itemView.setOnClickListener { onClick(item) }
    }
}

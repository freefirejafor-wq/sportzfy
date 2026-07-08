package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.R
import com.sportzfy.app.data.DatabaseHelper
import com.sportzfy.app.data.SavedPlaylist
import com.sportzfy.app.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        loadPlaylists()
        binding.fabAddPlaylist.setOnClickListener { showAddDialog() }
    }

    private fun loadPlaylists() {
        val playlists = db.getAllPlaylists()
        if (playlists.isEmpty()) {
            binding.recyclerPlaylists.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerPlaylists.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            val adapter = PlaylistAdapter(playlists,
                onPlay = { pl ->
                    requireContext().startActivity(
                        Intent(requireContext(), PlayerActivity::class.java).apply {
                            putExtra(PlayerActivity.EXTRA_STREAM_URL, pl.url)
                            putExtra(PlayerActivity.EXTRA_STREAM_NAME, pl.title)
                            putExtra(PlayerActivity.EXTRA_MATCH_TITLE, pl.title)
                            putExtra(PlayerActivity.EXTRA_FORMAT, "hls")
                        }
                    )
                },
                onDelete = { pl ->
                    db.removePlaylist(pl.id)
                    loadPlaylists()
                }
            )
            binding.recyclerPlaylists.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerPlaylists.adapter = adapter
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_playlist, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etPlaylistTitle)
        val etUrl   = dialogView.findViewById<EditText>(R.id.etPlaylistUrl)
        val rgType  = dialogView.findViewById<RadioGroup>(R.id.rgPlaylistType)

        AlertDialog.Builder(requireContext(), R.style.DarkDialogTheme)
            .setTitle("Add Playlist")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text.toString().trim()
                val url   = etUrl.text.toString().trim()
                val type  = if (rgType.checkedRadioButtonId == R.id.rbM3u8) "m3u8" else "xtream"
                if (title.isNotEmpty() && url.isNotEmpty()) {
                    db.addPlaylist(SavedPlaylist(title = title, url = url, type = type))
                    loadPlaylists()
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

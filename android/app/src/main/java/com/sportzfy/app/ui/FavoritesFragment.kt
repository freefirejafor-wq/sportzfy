package com.sportzfy.app.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sportzfy.app.R
import com.sportzfy.app.data.DatabaseHelper
import com.sportzfy.app.data.FavoriteChannel
import com.sportzfy.app.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        loadFavorites()
    }

    private fun loadFavorites() {
        val favs = db.getAllFavorites()
        if (favs.isEmpty()) {
            binding.recyclerFavorites.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerFavorites.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            val adapter = FavoriteAdapter(favs,
                onPlay = { ch ->
                    val sheet = StreamPickerBottomSheet.newInstance(ch.name)
                    sheet.show(childFragmentManager, "fav_stream")
                },
                onRemove = { ch ->
                    db.removeFavorite(ch.name)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                    loadFavorites()
                }
            )
            binding.recyclerFavorites.layoutManager = GridLayoutManager(requireContext(), 3)
            binding.recyclerFavorites.adapter = adapter
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

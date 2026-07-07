package com.sportzfy.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sportzfy.app.databinding.FragmentCategoriesBinding

data class Category(val id: String, val name: String, val emoji: String, val color: String)

val CATEGORIES = listOf(
    Category("fancode",    "FanCode",     "🏏", "#00d4e8"),
    Category("sonyliv",   "Sony LIV",    "📺", "#f5a623"),
    Category("hotstar",   "Hotstar",     "⭐", "#1a75ff"),
    Category("cricbuzz",  "CricBuzz",    "🏏", "#00d084"),
    Category("crichd",    "CricHD",      "🎯", "#ff3b3b"),
    Category("football",  "Football",    "⚽", "#4a90d9"),
    Category("cricket",   "Cricket",     "🏏", "#00d4e8"),
    Category("basketball","Basketball",  "🏀", "#f5a623"),
    Category("tennis",    "Tennis",      "🎾", "#00d084"),
    Category("mma",       "MMA / UFC",   "🥊", "#ff3b3b"),
    Category("racing",    "F1 Racing",   "🏎", "#4a90d9"),
    Category("baseball",  "Baseball",    "⚾", "#f5a623"),
    Category("rugby",     "Rugby",       "🏉", "#00d4e8"),
    Category("golf",      "Golf",        "⛳", "#00d084"),
    Category("hockey",    "Ice Hockey",  "🏒", "#4a90d9"),
    Category("swimming",  "Swimming",    "🏊", "#1a75ff"),
    Category("athletics", "Athletics",   "🏃", "#f5a623"),
    Category("wwe",       "WWE / Wrestling","🤼","#ff3b3b"),
    Category("volleyball","Volleyball",  "🏐", "#00d084"),
    Category("esports",   "E-Sports",    "🎮", "#a855f7")
)

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CategoryAdapter { category ->
            val sheet = StreamPickerBottomSheet.newInstance(category.name)
            sheet.show(childFragmentManager, "streams")
        }
        binding.recyclerCategories.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerCategories.adapter = adapter
        adapter.submitList(CATEGORIES)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

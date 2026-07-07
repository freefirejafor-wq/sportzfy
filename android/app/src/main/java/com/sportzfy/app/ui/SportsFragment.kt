package com.sportzfy.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sportzfy.app.R
import com.sportzfy.app.databinding.FragmentSportsBinding

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val category: String
)

val SPORT_CHANNELS = listOf(
    Channel("tnt", "TNT Sports", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/TNT_Sports_logo.svg/200px-TNT_Sports_logo.svg.png", "Sports"),
    Channel("bein1", "beIN Sports 1", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/200px-BeIN_Sports_logo.svg.png", "Sports"),
    Channel("sky", "Sky Sports", "https://upload.wikimedia.org/wikipedia/en/thumb/a/a0/Sky_Sports_logo_2020.svg/200px-Sky_Sports_logo_2020.svg.png", "Sports"),
    Channel("star", "Star Sports", "https://upload.wikimedia.org/wikipedia/en/thumb/4/4e/Star_Sports_logo.svg/200px-Star_Sports_logo.svg.png", "Sports"),
    Channel("espn", "ESPN", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/ESPN_wordmark.svg/200px-ESPN_wordmark.svg.png", "Sports"),
    Channel("dazn", "DAZN", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/DAZN_Logo.svg/200px-DAZN_Logo.svg.png", "Sports"),
    Channel("fifa", "FIFA+", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d4/FIFA_logo_without_slogan.svg/200px-FIFA_logo_without_slogan.svg.png", "Football"),
    Channel("eurosport", "Eurosport", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Logo_eurosport.svg/200px-Logo_eurosport.svg.png", "Sports"),
    Channel("canal", "Canal+", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Canal%2B.svg/200px-Canal%2B.svg.png", "Sports"),
    Channel("tsports", "T Sports", "https://upload.wikimedia.org/wikipedia/en/b/b7/T_Sports_logo.png", "Cricket"),
    Channel("ptv", "PTV Sports", "https://upload.wikimedia.org/wikipedia/en/thumb/f/f9/PTV_Sports_logo.png/200px-PTV_Sports_logo.png", "Cricket"),
    Channel("gazi", "Gazi TV", "https://upload.wikimedia.org/wikipedia/en/thumb/c/c8/Gazi_TV_Logo.png/200px-Gazi_TV_Logo.png", "Cricket"),
    Channel("willow", "Willow TV", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d9/Willow_TV_logo.png/200px-Willow_TV_logo.png", "Cricket"),
    Channel("icc", "ICC TV", "https://upload.wikimedia.org/wikipedia/en/thumb/4/49/ICC-Cricket-logo-logo.png/200px-ICC-Cricket-logo-logo.png", "Cricket"),
    Channel("crichd", "CricHD", "https://www.crichd.com/images/logo.png", "Cricket"),
    Channel("fox", "Fox Sports", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Fox_Sports_logo.svg/200px-Fox_Sports_logo.svg.png", "Sports"),
    Channel("nba", "NBA TV", "https://upload.wikimedia.org/wikipedia/en/thumb/0/03/National_Basketball_Association_logo.svg/200px-National_Basketball_Association_logo.svg.png", "Basketball"),
    Channel("nfl", "NFL Network", "https://upload.wikimedia.org/wikipedia/en/thumb/a/a2/NFL_Network.svg/200px-NFL_Network.svg.png", "Football"),
    Channel("mlb", "MLB Network", "https://upload.wikimedia.org/wikipedia/en/thumb/f/f5/MLB_Network_Logo.svg/200px-MLB_Network_Logo.svg.png", "Baseball"),
    Channel("ufc", "UFC Fight", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/UFC_Logo.svg/200px-UFC_Logo.svg.png", "MMA"),
    Channel("boxing", "Box Nation", "https://upload.wikimedia.org/wikipedia/en/thumb/2/23/BoxNation_Logo.png/200px-BoxNation_Logo.png", "MMA"),
    Channel("laliga", "LaLiga TV", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/13/LaLiga.svg/200px-LaLiga.svg.png", "Football"),
    Channel("bt", "BT Sport", "https://upload.wikimedia.org/wikipedia/en/thumb/5/5e/BT_Sport_logo.svg/200px-BT_Sport_logo.svg.png", "Sports"),
    Channel("abu", "Abu Dhabi Sports", "https://upload.wikimedia.org/wikipedia/en/thumb/c/c9/Abu_Dhabi_Sports_Channel.png/200px-Abu_Dhabi_Sports_Channel.png", "Sports")
)

class SportsFragment : Fragment() {
    private var _binding: FragmentSportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChannelAdapter { channel ->
            val sheet = StreamPickerBottomSheet.newInstance(channel.name)
            sheet.show(childFragmentManager, "streams")
        }
        binding.recyclerChannels.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerChannels.adapter = adapter
        adapter.submitList(SPORT_CHANNELS)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

package com.sportzfy.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.sportzfy.app.PlayerActivity
import com.sportzfy.app.R
import com.sportzfy.app.databinding.FragmentSportsBinding

// ── Channel data class (with optional direct-stream support) ───────────
data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val category: String,
    val streamUrl: String? = null,
    val drmKid: String?   = null,
    val drmKey: String?   = null,
    val format: String    = "hls"
)

// ── NongorPlay DASH stream constants ──────────────────────────────────
private const val NG_DASH_URL = "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd"
private const val NG_KID      = "14eeabf30c14b7fbf3008c03099ce011"
private const val NG_KEY      = "17d2ac8dbc5429bd70af3433aa12158d"

val SPORT_CHANNELS = listOf(

    // ── Popular Sports Channels (stream via picker) ───────────────────
    Channel("tnt",      "TNT Sports",    "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/TNT_Sports_logo.svg/200px-TNT_Sports_logo.svg.png",     "Sports"),
    Channel("bein1",    "beIN Sports 1", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/200px-BeIN_Sports_logo.svg.png",    "Sports"),
    Channel("sky",      "Sky Sports",    "https://upload.wikimedia.org/wikipedia/en/thumb/a/a0/Sky_Sports_logo_2020.svg/200px-Sky_Sports_logo_2020.svg.png", "Sports"),
    Channel("star",     "Star Sports",   "https://upload.wikimedia.org/wikipedia/en/thumb/4/4e/Star_Sports_logo.svg/200px-Star_Sports_logo.svg.png",         "Sports"),
    Channel("espn",     "ESPN",          "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/ESPN_wordmark.svg/200px-ESPN_wordmark.svg.png",          "Sports"),
    Channel("dazn",     "DAZN",          "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/DAZN_Logo.svg/200px-DAZN_Logo.svg.png",                 "Sports"),
    Channel("fifa",     "FIFA+",         "https://upload.wikimedia.org/wikipedia/en/thumb/d/d4/FIFA_logo_without_slogan.svg/200px-FIFA_logo_without_slogan.svg.png", "Football"),
    Channel("eurosport","Eurosport",     "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Logo_eurosport.svg/200px-Logo_eurosport.svg.png",        "Sports"),

    // ── NongorPlay — FIFA World Cup 2026 Live Servers (DASH+ClearKey) ─
    Channel("ng-tsn-4k",        "TSN 1 4k",        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/TNT_Sports_logo.svg/120px-TNT_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-evrenesoglu57", "evrenesoglu57",    "https://i.imgur.com/ZBt6beo.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-soco",          "SOCO",             "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/120px-BeIN_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-cctv5",         "CCTV5 (Backup)",   "https://i.imgur.com/GyHaZ8X.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-beineng",       "BEINENG",          "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/120px-BeIN_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-beinsports",    "Bein Sports",      "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/120px-BeIN_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-irib-4k",       "IRIB 4K",          "https://i.imgur.com/nSSUHcg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-bein-s2",       "Bein S2",          "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/120px-BeIN_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-fussball",      "FussBall",         "https://upload.wikimedia.org/wikipedia/en/thumb/d/d4/FIFA_logo_without_slogan.svg/120px-FIFA_logo_without_slogan.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-bin-arabic",    "BIN ARABIC",       "https://i.imgur.com/A1xzjOI.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-tvp",           "TVP",              "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Logo_eurosport.svg/120px-Logo_eurosport.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-fox-4k",        "Fox Sports 4k",    "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/ESPN_wordmark.svg/120px-ESPN_wordmark.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-ctv-4k",        "CTV 4k",           "https://i.imgur.com/c0I24N6.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-bein3",         "BEIN3",            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/BeIN_Sports_logo.svg/120px-BeIN_Sports_logo.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-telemundu",     "Telemundu",        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Logo_eurosport.svg/120px-Logo_eurosport.svg.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-dsports-4k",    "D Sports 4k",      "https://stmify.com/wp-content/uploads/2024/11/49-s.webp",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),

    // ── NongorPlay — General TV Channels (DASH+ClearKey) ─────────────
    Channel("ng-zee-cinema",    "Zee Cinema HD",    "https://jiotvimages.cdn.jio.com/dare_images/images/channel/d61eef03af878ccecfe169b26b6686b0.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-zee-bangla",    "Zee Bangla Sonar", "https://yt3.googleusercontent.com/3_f350mZQuocm7Lx2eASxHJHG5s5ynrrD0cQaIMeUMpDSYyz29J5FCHqnl14AXsV19D71qDUdg=s160-c-k-c0x00ffffff-no-rj",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-sony-sports2",  "SONY SPORTS 2",    "https://static.wikia.nocookie.net/dreamlogos/images/d/df/Sony_Sports_Ten_2_2022.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-sony-sports5",  "SONY SPORTS 5",    "https://stmify.com/wp-content/uploads/2024/11/49-s.webp",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-dw",            "DW News",          "https://i.imgur.com/A1xzjOI.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-discovery",     "DISCOVERY HD",     "https://i.pinimg.com/736x/a1/99/74/a19974ce222a14ccfa0f26b47013c5e0.jpg",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-nasa",          "NASA TV Media",    "https://i.imgur.com/rmyfoOI.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-pbs-kids",      "PBS Kids",         "https://i.imgur.com/q4cUQKW.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-cctv7",         "CCTV-7",           "https://i.imgur.com/CKcPMQC.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-cctv8",         "CCTV-8",           "https://i.imgur.com/GyHaZ8X.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash"),
    Channel("ng-mr-bean",       "Mr. Bean Anime",   "https://static.wikia.nocookie.net/logopedia/images/2/25/Mr._Bean_Animated_Series_stacked_logo.png",
        "NongorPlay", NG_DASH_URL, NG_KID, NG_KEY, "dash")
)

val CHANNEL_CATEGORIES = listOf("All", "Sports", "Football", "NongorPlay")

fun getChannelsByCategory(cat: String): List<Channel> =
    if (cat == "All") SPORT_CHANNELS else SPORT_CHANNELS.filter { it.category == cat }

// ── SportsFragment ────────────────────────────────────────────────────
class SportsFragment : Fragment() {

    private var _binding: FragmentSportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var channelAdapter: ChannelAdapter
    private var searchQuery = ""
    private var currentCategory = "All"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupCategoryChips()
        setupSearch()
    }

    private fun setupRecycler() {
        channelAdapter = ChannelAdapter { channel ->
            if (channel.streamUrl != null) {
                // Direct play — NongorPlay DASH streams
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_STREAM_URL,  channel.streamUrl)
                    putExtra(PlayerActivity.EXTRA_STREAM_NAME, channel.name)
                    putExtra(PlayerActivity.EXTRA_MATCH_TITLE, channel.name)
                    putExtra(PlayerActivity.EXTRA_FORMAT,      channel.format)
                    channel.drmKid?.let { putExtra(PlayerActivity.EXTRA_DRM_KID, it) }
                    channel.drmKey?.let { putExtra(PlayerActivity.EXTRA_DRM_KEY, it) }
                }
                startActivity(intent)
            } else {
                // Show stream picker for channels without a direct URL
                StreamPickerBottomSheet.newInstance(channel.name)
                    .show(parentFragmentManager, "streams")
            }
        }
        binding.recyclerChannels.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = channelAdapter
        }
        applyFilter()
    }

    private fun setupCategoryChips() {
        CHANNEL_CATEGORIES.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = cat == "All"
                setChipBackgroundColorResource(R.color.chip_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.accent)
            }
            chip.setOnClickListener {
                currentCategory = cat
                applyFilter()
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            searchQuery = text?.toString()?.trim() ?: ""
            applyFilter()
        }
    }

    private fun applyFilter() {
        var list = getChannelsByCategory(currentCategory)
        if (searchQuery.isNotEmpty()) {
            list = list.filter { it.name.lowercase().contains(searchQuery.lowercase()) }
        }
        channelAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

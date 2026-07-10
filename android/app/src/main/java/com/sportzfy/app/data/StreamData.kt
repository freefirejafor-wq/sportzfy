package com.sportzfy.app.data

enum class Quality { FHD, HD, SD }

data class Stream(
    val id: String,
    val name: String,
    val quality: Quality,
    val url: String,
    val category: String,
    val drmKid: String? = null,
    val drmKey: String? = null,
    val format: String = "hls"   // "hls" or "dash"
)

// Verified active streams — July 2026 (from original Sportzfy project)
val ALL_STREAMS: List<Stream> = listOf(

    // ── FIFA / World Cup ──────────────────────────────────────────────
    Stream("fifa-wc-eng-master",   "FIFA WC ENG (Master)",     Quality.FHD, "https://hls.livekhelatv.website/mks/lktv/fifa-worldcup-eng/master.m3u8",                                                                                      "FIFA"),
    Stream("fifa-wc-eng-1080p",    "FIFA WC ENG (1080p)",      Quality.FHD, "https://hls.livekhelatv.website/mks/lktv/s/fifa-worldcup-eng/playlist/1080p_FHD/livekhela.m3u8",                                                              "FIFA"),
    Stream("caze-tv-4k",           "Caze TV (FIFA 4K)",        Quality.FHD, "https://dfr80qz435crc.cloudfront.net/MNOP/Amagi/Caze/Caze_TV_BR/1080p-vtt/index.m3u8",                                                                        "FIFA"),
    Stream("fifa-plus-english",    "FIFA+ English",            Quality.FHD, "https://a62dad94.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWV1X0ZJRkFQbHVzRW5nbGlzaF9ITFM/playlist.m3u8",                       "FIFA"),
    Stream("fifa-plus-usa",        "FIFA+ USA",                Quality.FHD, "https://d2w9q46ikgrcwx.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-of5cbk3sav3w5/v1/sysdata_s_p_a_fifa_7/samsungheadend_us/latest/main/hls/playlist.m3u8", "FIFA"),
    Stream("fifa-plus-espanol",    "FIFA+ Español",            Quality.FHD, "https://d63fabad.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWVzX0ZJRkFQbHVzU3BhbmlzaF9ITFM/playlist.m3u8",                      "FIFA"),
    Stream("fifa-plus-brasil",     "FIFA+ Brasil",             Quality.FHD, "https://e3be9ac5.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfRklGQVBsdXNQb3J0dWd1ZXNlX0hMUw/playlist.m3u8",                            "FIFA"),
    Stream("fifa-plus-deutschland","FIFA+ Deutschland",        Quality.FHD, "https://4397879b.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWRlX0ZJRkFQbHVzR2VybWFuX0hMUw/playlist.m3u8",                      "FIFA"),
    Stream("fifa-plus-argentina",  "FIFA+ Argentina",          Quality.FHD, "https://6c849fb3.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctbXhfRklGQVBsdXNTcGFuaXNoLTFfSExT/playlist.m3u8",                              "FIFA"),
    Stream("fifa-plus-italia",     "FIFA+ Italia",             Quality.FHD, "https://5d95f7d7.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWl0X0ZJRkFQbHVzSXRhbGlhbl9ITFM/playlist.m3u8",                     "FIFA"),
    Stream("fifa-plus-france",     "FIFA+ France",             Quality.FHD, "https://37b4c228.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWZyX0ZJRkFQbHVzRnJlbmNoX0hMUw/playlist.m3u8",                      "FIFA"),
    Stream("telemundo-fifa",       "Telemundo (FIFA)",         Quality.HD,  "https://nbculocallive.akamaized.net/hls/live/2037499/puertorico/stream1/master.m3u8",                                                                          "FIFA"),
    Stream("fifa-stream-a",        "FIFA Stream A",            Quality.HD,  "https://1nyaler.streamhostingcdn.top/stream/23/index.m3u8",                                                                                                    "FIFA"),
    Stream("world-cup-s2",         "World Cup S2",             Quality.HD,  "https://andro.226503.xyz/checklist/androstreamlivebs1.m3u8",                                                                                                   "FIFA"),
    Stream("win-sports-wc",        "Win Sports WC",            Quality.HD,  "https://1nyaler.streamhostingcdn.top/stream/32/index.m3u8",                                                                                                    "FIFA"),
    Stream("2tv-sports-wc",        "2TV Sports WC",            Quality.HD,  "https://tv.cdn.xsg.ge/gpb-2tv/index.m3u8",                                                                                                                    "FIFA"),

    // ── NongorPlay — FIFA World Cup 2026 Servers (DASH + ClearKey DRM) ──
    // DRM KID : 14eeabf30c14b7fbf3008c03099ce011
    // DRM Key : 17d2ac8dbc5429bd70af3433aa12158d
    Stream("ng-tsn-4k",        "TSN 1 4k",        Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-evrenesoglu57", "evrenesoglu57",    Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-soco",          "SOCO",             Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-cctv5",         "CCTV5 (Backup)",   Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-beineng",       "BEINENG",          Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-beinsports",    "Bein Sports",      Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-irib-4k",       "IRIB_4K",          Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-bein-s2",       "Bein_S2",          Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-fussball",      "FussBall",         Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-bin-arabic",    "BIN_ARABIC",       Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-tvp",           "TVP",              Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-fox-4k",        "Fox Sports 4k",    Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-ctv-4k",        "CTV 4k",           Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-bein3",         "BEIN3",            Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-telemundu",     "Telemundu",        Quality.HD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),
    Stream("ng-dsports-4k",    "D Sports 4k",      Quality.FHD,
        "https://otte.cache.aiv-cdn.net/bom-nitro/live/dash/enc/w0rehjjrwe/out/v1/69a2a7041395406b970598f61680e7cf/cenc.mpd",
        "NongorPlay", "14eeabf30c14b7fbf3008c03099ce011", "17d2ac8dbc5429bd70af3433aa12158d", "dash"),

    // ── beIN Sports ──────────────────────────────────────────────────
    Stream("bein-1-uhd",           "beIN Sports 1 UHD",        Quality.FHD, "http://proxpanel.cc/h1wqD6CY/byxHYgX/707929",                                                                                                                 "beIN"),
    Stream("bein-1-amagi",         "beIN Sports 1 (Amagi)",    Quality.FHD, "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg02873-kravemedia-mtrspt1-distrotv/playlist.m3u8",                                                                "beIN"),
    Stream("bein-2",               "beIN Sports 2",            Quality.HD,  "https://andro.226503.xyz/checklist/androstreamlivebs2.m3u8",                                                                                                   "beIN"),
    Stream("bein-3",               "beIN Sports 3",            Quality.HD,  "https://andro.226503.xyz/checklist/androstreamlivebs3.m3u8",                                                                                                   "beIN"),
    Stream("bein-4",               "beIN Sports 4",            Quality.HD,  "https://andro.226503.xyz/checklist/androstreamlivebs4.m3u8",                                                                                                   "beIN"),
    Stream("bein-xtra",            "beIN Sports Xtra",         Quality.HD,  "https://bein-esp-xumo.amagi.tv/playlistR720P.m3u8",                                                                                                            "beIN"),

    // ── Other Sports ─────────────────────────────────────────────────
    Stream("espn-2",               "ESPN 2",                   Quality.HD,  "https://tv.topmediatv.net:25463/live/TopMediaWeb/bOteTR8ED1/108.m3u8",                                                                                        "Sports"),
    Stream("tyc-sports",           "TyC Sports Argentina",     Quality.HD,  "https://amg26268-amg26268c14-freelivesports-emea-10267.playouts.now.amagi.tv/ts-us-e2-n2/playlist/amg26268-sportsstudio-tycsports-freelivesportsemea/playlist.m3u8", "Sports"),
    Stream("euro-tv",              "Euro TV",                  Quality.HD,  "https://stream.ottplus.bd/live/euro_sports_hd_abr/live/euro_sports_hd/chunks.m3u8",                                                                            "Sports"),

    // ── Cricket ──────────────────────────────────────────────────────
    Stream("cricket-gold",         "Cricket Gold",             Quality.HD,  "https://streams2.sofast.tv/ptnr-yupptv/title-cricketgold/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/b2048bb8-1686-4432-aa50-647245383e0c/bfc6a36e-c250-4afe-b6c9-2bc57855bb7d/4.m3u8", "Cricket"),

    // ── YouTube Live Channels ─────────────────────────────────────────
    Stream("cazetv-youtube",    "🔴 CazéTV YouTube Live",     Quality.FHD, "https://www.youtube.com/@CazeTV/live",     "YouTube", format = "youtube"),
    Stream("foxsports-youtube", "🔴 FOX Sports YouTube Live", Quality.FHD, "https://www.youtube.com/@foxsports/live",  "YouTube", format = "youtube"),

)

val STREAM_CATEGORIES = listOf("All", "FIFA", "YouTube", "NongorPlay", "beIN", "Sports", "Cricket")

fun getStreamsByCategory(category: String): List<Stream> =
    if (category == "All") ALL_STREAMS else ALL_STREAMS.filter { it.category == category }

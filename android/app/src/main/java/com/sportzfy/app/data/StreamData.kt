package com.sportzfy.app.data

data class Stream(
    val name: String,
    val quality: String, // "FHD" | "HD" | "SD"
    val url: String
)

val ALL_STREAMS = listOf(
    // ── FIFA / World Cup ──────────────────────────────────────────
    Stream("FIFA WC ENG (Master)",  "FHD", "https://hls.livekhelatv.website/mks/lktv/fifa-worldcup-eng/master.m3u8"),
    Stream("FIFA WC ENG (1080p)",   "FHD", "https://hls.livekhelatv.website/mks/lktv/s/fifa-worldcup-eng/playlist/1080p_FHD/livekhela.m3u8"),
    Stream("Caze TV (FIFA 4K)",     "FHD", "https://dfr80qz435crc.cloudfront.net/MNOP/Amagi/Caze/Caze_TV_BR/1080p-vtt/index.m3u8"),
    Stream("FIFA+ English",         "FHD", "https://a62dad94.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWV1X0ZJRkFQbHVzRW5nbGlzaF9ITFM/playlist.m3u8"),
    Stream("FIFA+ USA",             "FHD", "https://d2w9q46ikgrcwx.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-of5cbk3sav3w5/v1/sysdata_s_p_a_fifa_7/samsungheadend_us/latest/main/hls/playlist.m3u8"),
    Stream("FIFA+ Español",         "FHD", "https://d63fabad.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWVzX0ZJRkFQbHVzU3BhbmlzaF9ITFM/playlist.m3u8"),
    Stream("FIFA+ Brasil",          "FHD", "https://e3be9ac5.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfRklGQVBsdXNQb3J0dWd1ZXNlX0hMUw/playlist.m3u8"),
    Stream("FIFA+ Deutschland",     "FHD", "https://4397879b.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWRlX0ZJRkFQbHVzR2VybWFuX0hMUw/playlist.m3u8"),
    Stream("FIFA+ Argentina",       "FHD", "https://6c849fb3.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctbXhfRklGQVBsdXNTcGFuaXNoLTFfSExT/playlist.m3u8"),
    Stream("FIFA+ Italia",          "FHD", "https://5d95f7d7.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWl0X0ZJRkFQbHVzSXRhbGlhbl9ITFM/playlist.m3u8"),
    Stream("FIFA+ France",          "FHD", "https://37b4c228.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWZyX0ZJRkFQbHVzRnJlbmNoX0hMUw/playlist.m3u8"),
    Stream("Telemundo (FIFA)",      "HD",  "https://nbculocallive.akamaized.net/hls/live/2037499/puertorico/stream1/master.m3u8"),
    Stream("FIFA Stream A",         "HD",  "https://1nyaler.streamhostingcdn.top/stream/23/index.m3u8"),
    Stream("World Cup S2",          "HD",  "https://andro.226503.xyz/checklist/androstreamlivebs1.m3u8"),
    Stream("Win Sports WC",         "HD",  "https://1nyaler.streamhostingcdn.top/stream/32/index.m3u8"),
    Stream("2TV Sports WC",         "HD",  "https://tv.cdn.xsg.ge/gpb-2tv/index.m3u8"),

    // ── beIN Sports ───────────────────────────────────────────────
    Stream("beIN Sports 1 (Amagi)", "FHD", "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg02873-kravemedia-mtrspt1-distrotv/playlist.m3u8"),
    Stream("beIN Sports 2",         "HD",  "https://andro.226503.xyz/checklist/androstreamlivebs2.m3u8"),
    Stream("beIN Sports 3",         "HD",  "https://andro.226503.xyz/checklist/androstreamlivebs3.m3u8"),
    Stream("beIN Sports 4",         "HD",  "https://andro.226503.xyz/checklist/androstreamlivebs4.m3u8"),
    Stream("beIN Sports Xtra",      "HD",  "https://bein-esp-xumo.amagi.tv/playlistR720P.m3u8"),

    // ── ESPN / Sports USA ─────────────────────────────────────────
    Stream("ESPN 2",                "HD",  "https://tv.topmediatv.net:25463/live/TopMediaWeb/bOteTR8ED1/108.m3u8"),
    Stream("TyC Sports Argentina",  "HD",  "https://amg26268-amg26268c14-freelivesports-emea-10267.playouts.now.amagi.tv/ts-us-e2-n2/playlist/amg26268-sportsstudio-tycsports-freelivesportsemea/playlist.m3u8"),
    Stream("Euro TV",               "HD",  "https://stream.ottplus.bd/live/euro_sports_hd_abr/live/euro_sports_hd/chunks.m3u8"),
    Stream("Cricket Gold",          "HD",  "https://streams2.sofast.tv/ptnr-yupptv/title-cricketgold/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/b2048bb8-1686-4432-aa50-647245383e0c/bfc6a36e-c250-4afe-b6c9-2bc57855bb7d/4.m3u8"),

    // ── Cricket / T Sports ────────────────────────────────────────
    Stream("T Sports BD",           "FHD", "https://bdrcs.com:7071/tsports/tsports/playlist.m3u8"),
    Stream("PTV Sports",            "HD",  "https://streaming.arynews.tv/ptvsports/ptvsports.smil/playlist.m3u8"),
    Stream("Star Sports 1",         "HD",  "https://d1g8wgjurz8via.cloudfront.net/bpk-tv/StarSports1HD/default/StarSports1HD.m3u8"),
    Stream("Sony LIV Sports",       "HD",  "https://streams2.sofast.tv/ptnr-yupptv/title-sonyliv/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/84c0eedf-7ffe-4503-85d0-42ff87c28e7b/6d1e2428-4b8f-48d0-a1b2-3b1bf7c30d3e/4.m3u8"),

    // ── General Sports ────────────────────────────────────────────
    Stream("Sky Sports Main",       "HD",  "https://linear305.cdn.plex.tv/library/parts/5f3cca4c-3a84-4cbc-b024-5e93fdba7e24/0/file.m3u8"),
    Stream("TNT Sports 1",          "HD",  "https://linear201.cdn.plex.tv/library/parts/4e0ec2b4-ba7a-49cb-a13c-a7f0fc8b574f/0/file.m3u8"),
    Stream("Eurosport 1",           "HD",  "https://linear403.cdn.plex.tv/library/parts/1f2d4bee-fbf7-4af9-9bc5-3c2c0e0b7023/0/file.m3u8"),
    Stream("DAZN 1",                "HD",  "https://linear302.cdn.plex.tv/library/parts/dazn1/0/file.m3u8"),
    Stream("Fox Sports 1",          "HD",  "https://linear301.cdn.plex.tv/library/parts/foxsports1/0/file.m3u8"),
    Stream("Stream HD 1",           "HD",  "https://1nyaler.streamhostingcdn.top/stream/1/index.m3u8"),
    Stream("Stream HD 2",           "HD",  "https://1nyaler.streamhostingcdn.top/stream/2/index.m3u8"),
    Stream("Stream HD 3",           "HD",  "https://1nyaler.streamhostingcdn.top/stream/3/index.m3u8"),
    Stream("Stream HD 4",           "HD",  "https://1nyaler.streamhostingcdn.top/stream/4/index.m3u8"),
    Stream("Stream SD 1",           "SD",  "https://1nyaler.streamhostingcdn.top/stream/10/index.m3u8"),
    Stream("Stream SD 2",           "SD",  "https://1nyaler.streamhostingcdn.top/stream/11/index.m3u8")
)

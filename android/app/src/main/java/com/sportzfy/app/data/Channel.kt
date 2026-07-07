package com.sportzfy.app.data

data class Channel(
    val id: Int,
    val name: String,
    val logo: String,
    val isCategory: Boolean = false
)

val SPORT_CHANNELS = listOf(
    Channel(1,  "TNT Sports",     "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/TNT_Sports_logo_2023.png/240px-TNT_Sports_logo_2023.png"),
    Channel(2,  "beIN Sports",    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/BeIN_Sports_logo.svg/240px-BeIN_Sports_logo.svg.png"),
    Channel(3,  "Eleven Sports",  "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Eleven_Sports_logo.png/240px-Eleven_Sports_logo.png"),
    Channel(4,  "FIFA+",          "https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/FIFA_logo_without_slogan.svg/240px-FIFA_logo_without_slogan.svg.png"),
    Channel(5,  "Fox Sports",     "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/FOX_Sports_logo.svg/240px-FOX_Sports_logo.svg.png"),
    Channel(6,  "DAZN",          "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/DAZN_logo.svg/240px-DAZN_logo.svg.png"),
    Channel(7,  "Star Sports",    "https://upload.wikimedia.org/wikipedia/en/thumb/d/db/Star_Sports.svg/240px-Star_Sports.svg.png"),
    Channel(8,  "Fancode",       "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png"),
    Channel(9,  "PTV Sports",    "https://upload.wikimedia.org/wikipedia/en/thumb/e/e3/PTV_Sports_logo.png/240px-PTV_Sports_logo.png"),
    Channel(10, "A Sports",       "https://upload.wikimedia.org/wikipedia/en/thumb/5/5e/A_Sports_Official_Logo.png/240px-A_Sports_Official_Logo.png"),
    Channel(11, "TEN Sports",     "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Ten_Sports_new_logo.png/240px-Ten_Sports_new_logo.png"),
    Channel(12, "Willow TV",      "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Willow_tv.png/240px-Willow_tv.png"),
    Channel(13, "Criclife",       "https://play-lh.googleusercontent.com/sUgF3dknbPOSg8VJbRH6sZIBPXTrAz3_WsODXvPrDpzQDYSz5W2T_jAJUMFOhbIFiQ=w240-h480"),
    Channel(14, "Astro Cricket",  "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Astro_SuperSport_logo_%28old%29.png/240px-Astro_SuperSport_logo_%28old%29.png"),
    Channel(15, "T Sports",       "https://upload.wikimedia.org/wikipedia/en/thumb/2/20/T_Sports_TV_logo.png/240px-T_Sports_TV_logo.png"),
    Channel(16, "RTA Sport",      "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/RTA_Sport_HD_Arabic.png/240px-RTA_Sport_HD_Arabic.png"),
    Channel(17, "Sky Sports",     "https://upload.wikimedia.org/wikipedia/en/thumb/a/a3/Sky_Sports_logo_2020.svg/240px-Sky_Sports_logo_2020.svg.png"),
    Channel(18, "Fox Cricket",    "https://upload.wikimedia.org/wikipedia/en/thumb/5/5d/Fox_Cricket_logo.png/240px-Fox_Cricket_logo.png"),
    Channel(19, "SPOTV",          "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/SPOTV_logo.png/240px-SPOTV_logo.png"),
    Channel(20, "Ziggo Sport",    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Ziggo_Sport_logo.svg/240px-Ziggo_Sport_logo.svg.png"),
    Channel(21, "Super Sport",    "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/SuperSport_%28TV_channel%29_logo.svg/240px-SuperSport_%28TV_channel%29_logo.svg.png"),
    Channel(22, "Nova Sports",    "https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Nova_Sports_logo.png/240px-Nova_Sports_logo.png"),
    Channel(23, "Cosmote Sport",  "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Cosmote_TV_logo.png/240px-Cosmote_TV_logo.png"),
    Channel(24, "Premier Sports", "https://upload.wikimedia.org/wikipedia/en/thumb/6/61/Premier_Sports_UK_logo.png/240px-Premier_Sports_UK_logo.png")
)

val CATEGORIES = listOf(
    Channel(101, "Fancode IND",   "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png",  true),
    Channel(102, "Fancode IND 2", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png",  true),
    Channel(103, "Fancode BD",    "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png",  true),
    Channel(104, "ICC TV",        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/ICC_logo.png/240px-ICC_logo.png",           true),
    Channel(105, "Sony LIV LIVE", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png", true),
    Channel(106, "Sony LIV LIVE 2","https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png", true),
    Channel(107, "Sony LIV",      "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png", true),
    Channel(108, "CricHD",        "https://play-lh.googleusercontent.com/KT2kqJ3bMUkMOFjP7Ff6Q6R6SqMW7kq7qhbEiT8jX7OL2f0Hzm0WqM4SQ3XF9v-2wg=w240-h480", true),
    Channel(109, "CricHD 2",      "https://play-lh.googleusercontent.com/KT2kqJ3bMUkMOFjP7Ff6Q6R6SqMW7kq7qhbEiT8jX7OL2f0Hzm0WqM4SQ3XF9v-2wg=w240-h480", true),
    Channel(110, "Pluto TV",      "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Pluto_TV_Logo.svg/240px-Pluto_TV_Logo.svg.png", true),
    Channel(111, "Tubi TV",       "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Tubi_logo.svg/240px-Tubi_logo.svg.png",     true),
    Channel(112, "Roku",          "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Roku_logo.svg/240px-Roku_logo.svg.png",     true),
    Channel(113, "Free TV",       "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/FreeTVAustralia_logo.png/240px-FreeTVAustralia_logo.png", true),
    Channel(114, "Fire TV",       "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f0/Amazon_fire_tv_logo.svg/240px-Amazon_fire_tv_logo.svg.png", true),
    Channel(115, "Pakistan",      "https://upload.wikimedia.org/wikipedia/commons/thumb/3/32/Flag_of_Pakistan.svg/240px-Flag_of_Pakistan.svg.png", true),
    Channel(116, "News",          "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Cnn_logo.svg/240px-Cnn_logo.svg.png",      true),
    Channel(117, "Islamic",       "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Mosque_icon.svg/240px-Mosque_icon.svg.png", true),
    Channel(118, "Kids",          "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Cartoon_Network_2010_logo.svg/240px-Cartoon_Network_2010_logo.svg.png", true),
    Channel(119, "Discovery",     "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Discovery_Channel_logo_2019.svg/240px-Discovery_Channel_logo_2019.svg.png", true),
    Channel(120, "Sun Direct",    "https://upload.wikimedia.org/wikipedia/en/thumb/5/5a/Sun_Direct_Logo.svg/240px-Sun_Direct_Logo.svg.png", true)
)

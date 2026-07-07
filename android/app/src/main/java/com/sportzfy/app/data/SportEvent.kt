package com.sportzfy.app.data

data class SportEvent(
    val id: String,
    val sport: String,
    val league: String,
    val homeName: String,
    val awayName: String,
    val homeLogo: String,
    val awayLogo: String,
    val homeScore: String,
    val awayScore: String,
    val status: String,   // "live" | "upcoming" | "finished"
    val clock: String?,
    val displayTime: String,
    val date: String,
    val hot: Boolean,
    val venue: String?
)

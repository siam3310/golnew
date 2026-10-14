package com.angeldevtech.gol.domain.models

data class ScheduleItem(
    val id: Int,
    val hour: String,
    val name: String,
    val date: String,
    val relativeDay: String,
    val category: String,
    val embeds: List<Embed>,
    val leagueName: String,
    val leagueImageUrl: String,
    val team1Name: String = "",
    val team2Name: String = "",
    val team1Logo: String = "",
    val team2Logo: String = "",
    val isLive: Boolean = false
)

data class Embed(
    val name: String,
    val language: String,
    val url: String
)

data class ScheduleCategories(
    val name: String,
    val items: List<ScheduleItem>
)
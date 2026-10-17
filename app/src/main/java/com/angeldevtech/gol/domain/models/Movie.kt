package com.angeldevtech.gol.domain.models

data class MovieItem(
    val id: Int,
    val title: String,
    val year: String,
    val quality: String,
    val language: String,
    val link: String,
    val allQualities: List<MovieQualitySource> = emptyList()
)

data class MovieQualitySource(
    val quality: String,
    val link: String
)

fun MovieItem.toScheduleItem(): ScheduleItem {
    val embedSources = if (allQualities.isNotEmpty()) {
        allQualities.map {
            Embed(
                name = it.quality,
                language = language,
                url = it.link
            )
        }
    } else {
        listOf(Embed(name = quality.ifBlank { "HD" }, language = language, url = link))
    }

    return ScheduleItem(
        id = id,
        hour = if (allQualities.isNotEmpty()) allQualities.joinToString(" • ") { it.quality } else quality,
        name = title,
        date = year,
        relativeDay = language,
        category = "Movies & Shows",
        embeds = embedSources,
        leagueName = "Movie • $year",
        leagueImageUrl = "",
        team1Name = title,
        team2Name = year,
        team1Logo = "",
        team2Logo = "",
        isLive = false
    )
}

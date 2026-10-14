package com.angeldevtech.gol.data.mappers

import android.util.Base64
import com.angeldevtech.gol.BuildConfig
import com.angeldevtech.gol.data.models.ScheduleItemDto
import com.angeldevtech.gol.domain.models.Embed
import com.angeldevtech.gol.domain.models.ScheduleItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun ScheduleItemDto.toSimplified(): ScheduleItem? {
    if (id == null) return null

    val validEmbeds = attributes.embeds.data.mapNotNull { embed ->
        val decodedUrl = decodeEmbedUrl(embed.attributes.embed_iframe)
        decodedUrl?.let {
            Embed(
                name = embed.attributes.embed_name,
                language = embed.attributes.idioma,
                url = it
            )
        }
    }

    if (validEmbeds.isEmpty()) return null

    val (localDate, localTime) = processApiDateTime(attributes.date_diary, attributes.diary_hour)

    val leagueImg = attributes.country.data.attributes.image.data.attributes.url
    val fullImageUrl = resolveImageUrl(leagueImg)
    val rawTeam1 = attributes.team1_logo
        .ifBlank { attributes.team1Logo }
        .ifBlank { attributes.home_team_logo }
        .ifBlank { attributes.team_1_logo }
        .ifBlank { attributes.home_logo }
        .ifBlank { attributes.logo1 }
        .ifBlank { attributes.team1_image }
    val rawTeam2 = attributes.team2_logo
        .ifBlank { attributes.team2Logo }
        .ifBlank { attributes.away_team_logo }
        .ifBlank { attributes.team_2_logo }
        .ifBlank { attributes.away_logo }
        .ifBlank { attributes.logo2 }
        .ifBlank { attributes.team2_image }

    val team1Logo = resolveImageUrl(rawTeam1)
    val team2Logo = resolveImageUrl(rawTeam2)

    val isExplicitlyLive = attributes.is_live || attributes.isLive || attributes.live ||
        attributes.diary_hour.equals("now", ignoreCase = true) ||
        attributes.diary_hour.equals("live", ignoreCase = true) ||
        (attributes.date_diary.equals("today", ignoreCase = true) && attributes.diary_hour.equals("now", ignoreCase = true))

    val desc = attributes.diary_description.trim()
    val (parsedTeam1, parsedTeam2) = if (attributes.team1_name.isNotBlank() && attributes.team2_name.isNotBlank()) {
        attributes.team1_name to attributes.team2_name
    } else if (" vs " in desc.lowercase()) {
        val parts = desc.split(Regex("(?i)\\s+vs\\.?\\s+"))
        parts.getOrElse(0) { "" }.trim() to parts.getOrElse(1) { "" }.trim()
    } else if (" - " in desc) {
        val parts = desc.split(" - ")
        parts.getOrElse(0) { "" }.trim() to parts.getOrElse(1) { "" }.trim()
    } else {
        desc to ""
    }

    return ScheduleItem(
        id = id,
        hour = localTime,
        name = desc,
        date = localDate,
        relativeDay = if (isExplicitlyLive) "LIVE" else localDate,
        category = attributes.deportes.trim(),
        embeds = validEmbeds,
        leagueName = attributes.country.data.attributes.name.trim(),
        leagueImageUrl = fullImageUrl,
        team1Name = parsedTeam1,
        team2Name = parsedTeam2,
        team1Logo = team1Logo,
        team2Logo = team2Logo,
        isLive = isExplicitlyLive
    )
}

fun resolveImageUrl(url: String): String {
    if (url.isBlank()) return ""
    return if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        val base = BuildConfig.IMG_BASE_URL.trim().trimEnd('/')
        if (base.isNotBlank() && !base.contains("example.com")) {
            val path = if (url.startsWith("/")) url else "/$url"
            "$base$path"
        } else {
            url
        }
    }
}

fun decodeEmbedUrl(iframeUrl: String): String? {
    if (iframeUrl.isBlank()) return null

    if ("r=" in iframeUrl) {
        val base64Encoded = iframeUrl
            .substringAfter("r=", "")
            .replace("//n", "")
            .replace("/n", "")

        if (base64Encoded.isNotBlank()) {
            try {
                val decodedBytes = Base64.decode(base64Encoded, Base64.DEFAULT)
                val decoded = String(decodedBytes)
                if ("?get=" !in decoded && "drm.php" !in decoded) {
                    return decoded
                }
            } catch (e: Exception) {
                // fall through
            }
        }
    }

    if (iframeUrl.startsWith("http://") || iframeUrl.startsWith("https://")) {
        return iframeUrl
    }

    return null
}

private fun processApiDateTime(dateStr: String, timeStr: String): Pair<String, String> {
    val isNow = timeStr.equals("now", ignoreCase = true) || timeStr.equals("live", ignoreCase = true)
    val isToday = dateStr.equals("today", ignoreCase = true) || dateStr.equals("live", ignoreCase = true)

    if (isNow && isToday) {
        val nowDate = java.time.LocalDate.now().toString()
        val nowTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        return Pair(nowDate, nowTime)
    }

    val cleanDate = when {
        isToday -> java.time.LocalDate.now().toString()
        dateStr.equals("tomorrow", ignoreCase = true) -> java.time.LocalDate.now().plusDays(1).toString()
        dateStr.equals("yesterday", ignoreCase = true) -> java.time.LocalDate.now().minusDays(1).toString()
        else -> dateStr
    }
    val cleanTime = if (isNow) {
        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
    } else {
        timeStr
    }

    if (cleanDate == "Fecha desconocida" || cleanTime == "Hora desconocida") return Pair(cleanDate, cleanTime)

    if (isNow) {
        return Pair(cleanDate, cleanTime)
    }

    return try {
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("America/Lima")
            isLenient = false
        }
        val combinedDateTime = "${cleanDate.trim()} ${cleanTime.trim()}"
        val utcDate = utcFormatter.parse(combinedDateTime) ?: return Pair(cleanDate, cleanTime)

        val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

        val localDateTime = localFormatter.format(utcDate)
        val parts = localDateTime.split(" ")
        Pair(parts[0], parts[1])
    } catch (e: Exception) {
        Pair(cleanDate, cleanTime)
    }
}
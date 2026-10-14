package com.angeldevtech.gol.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    val data: List<ScheduleItemDto>
)

@Serializable
data class ScheduleItemDto(
    val id: Int?,
    val attributes: ScheduleAttributesDto
)

@Serializable
data class ScheduleAttributesDto(
    val diary_hour: String = "Hora desconocida",
    val diary_description: String = "Evento desconocido",
    val date_diary: String = "Fecha desconocida",
    val deportes: String = "Deporte desconocido",
    val embeds: EmbedWrapperDto = EmbedWrapperDto(emptyList()),
    val country: CountryWrapperDto = CountryWrapperDto(),
    val is_live: Boolean = false,
    val isLive: Boolean = false,
    val live: Boolean = false,
    val team1_name: String = "",
    val team2_name: String = "",
    val team1_logo: String = "",
    val team2_logo: String = "",
    val team_1_logo: String = "",
    val team_2_logo: String = "",
    val team1Logo: String = "",
    val team2Logo: String = "",
    val home_team_logo: String = "",
    val away_team_logo: String = "",
    val home_logo: String = "",
    val away_logo: String = "",
    val logo1: String = "",
    val logo2: String = "",
    val team1_image: String = "",
    val team2_image: String = ""
)

@Serializable
data class EmbedWrapperDto(
    val data: List<EmbedItemDto>
)

@Serializable
data class EmbedItemDto(
    val id: Int = 1,
    val attributes: EmbedAttributesDto = EmbedAttributesDto()
)

@Serializable
data class EmbedAttributesDto(
    val embed_name: String = "Fuente desconocida",
    val idioma: String = "Idioma desconocido",
    val embed_iframe: String = ""
)

@Serializable
data class CountryWrapperDto(
    val data: CountryDataDto = CountryDataDto()
)

@Serializable
data class CountryDataDto(
    val attributes: CountryAttributesDto = CountryAttributesDto()
)

@Serializable
data class CountryAttributesDto(
    val name: String = "Liga desconocida",
    val image: ImageWrapperDto = ImageWrapperDto()
)

@Serializable
data class ImageWrapperDto(
    val data: ImageDataDto = ImageDataDto()
)

@Serializable
data class ImageDataDto(
    val attributes: ImageAttributesDto = ImageAttributesDto()
)

@Serializable
data class ImageAttributesDto(
    val url: String = ""
)
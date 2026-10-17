package com.angeldevtech.gol.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName("made_by") val madeBy: String? = null,
    @SerialName("last_update_time") val lastUpdateTime: String? = null,
    @SerialName("scraped_media") val media: List<ScrapedMediaDto> = emptyList()
)

@Serializable
data class ScrapedMediaDto(
    val name: String = "",
    val year: String? = null,
    val quality: String? = null,
    val link: String = ""
)

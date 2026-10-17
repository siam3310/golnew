package com.angeldevtech.gol.data.remote

import android.content.Context
import com.angeldevtech.gol.BuildConfig
import com.angeldevtech.gol.data.models.MovieResponseDto
import com.angeldevtech.gol.data.models.ScheduleDto
import com.angeldevtech.gol.data.models.ScheduleItemDto
import com.angeldevtech.gol.data.models.ScrapedMediaDto
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import jakarta.inject.Inject
import kotlinx.serialization.json.Json

class ApiServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lazyClient: Lazy<HttpClient>
): ApiService {

    private val client: HttpClient
        get() = lazyClient.get()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    override suspend fun fetchSchedule(): List<ScheduleItemDto> {
        val configuredUrl = BuildConfig.API_BASE_URL.trim()
        val url = if (configuredUrl.isNotBlank() && configuredUrl != "https://api.example.com" && !configuredUrl.contains("example.com")) {
            configuredUrl
        } else {
            "https://fakeottadmin.ai.studio/api/matches"
        }

        try {
            val response: ScheduleDto = client
                .get(url)
                .body()
            if (response.data.isNotEmpty()) {
                return response.data
            }
        } catch (e: Exception) {
            // Fall back to bundled sample file
        }
        return loadBundledSchedule()
    }

    override suspend fun fetchMovies(): List<ScrapedMediaDto> {
        val movieUrl = "https://raw.githubusercontent.com/siam3310/fibw/refs/heads/main/scraped-links-lxml.json"
        try {
            val response: MovieResponseDto = client
                .get(movieUrl)
                .body()
            if (response.media.isNotEmpty()) {
                return response.media
            }
        } catch (e: Exception) {
            // Fall back to bundled movies
        }
        return loadBundledMovies()
    }

    private fun loadBundledSchedule(): List<ScheduleItemDto> {
        return try {
            val jsonString = context.assets.open("sample_schedule.json").bufferedReader().use { it.readText() }
            val dto = json.decodeFromString<ScheduleDto>(jsonString)
            dto.data
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadBundledMovies(): List<ScrapedMediaDto> {
        return try {
            val jsonString = context.assets.open("sample_movies.json").bufferedReader().use { it.readText() }
            val dto = json.decodeFromString<MovieResponseDto>(jsonString)
            dto.media
        } catch (e: Exception) {
            emptyList()
        }
    }
}
package com.angeldevtech.gol.data.remote

import android.content.Context
import com.angeldevtech.gol.BuildConfig
import com.angeldevtech.gol.data.models.ScheduleDto
import com.angeldevtech.gol.data.models.ScheduleItemDto
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
    }

    override suspend fun fetchSchedule(): List<ScheduleItemDto> {
        val url = BuildConfig.API_BASE_URL.trim()
        if (url.isNotBlank() && url != "https://api.example.com" && !url.contains("example.com")) {
            try {
                val response: ScheduleDto = client
                    .get(url)
                    .body()
                if (response.data.isNotEmpty()) {
                    return response.data
                }
            } catch (e: Exception) {
                // If remote fetch fails, fall back to bundled sample file
            }
        }
        return loadBundledSchedule()
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
}
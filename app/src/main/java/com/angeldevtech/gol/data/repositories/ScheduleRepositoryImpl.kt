package com.angeldevtech.gol.data.repositories

import com.angeldevtech.gol.data.mappers.toSimplified
import com.angeldevtech.gol.data.models.ScrapedMediaDto
import com.angeldevtech.gol.data.remote.ApiService
import com.angeldevtech.gol.domain.models.MovieItem
import com.angeldevtech.gol.domain.models.MovieQualitySource
import com.angeldevtech.gol.domain.models.ScheduleItem
import com.angeldevtech.gol.domain.models.toScheduleItem
import com.angeldevtech.gol.domain.repositories.ScheduleRepository
import com.angeldevtech.gol.utils.DateChangeObserver
import com.angeldevtech.gol.utils.computeRelativeDay
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ScheduleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    dateChangeObserver: DateChangeObserver,
    externalScope: CoroutineScope
): ScheduleRepository {
    private val rawScheduleFlow = MutableStateFlow<List<ScheduleItem>>(emptyList())
    private val rawMoviesFlow = MutableStateFlow<List<MovieItem>?>(null)

    private val _scheduleFlow: StateFlow<List<ScheduleItem>?> = combine(
        rawScheduleFlow,
        dateChangeObserver.onDateChanged.onStart { emit(Unit) }
    ) { items, _ ->
        items.map { item ->
            item.copy(relativeDay = computeRelativeDay(item.date))
        }
    }.stateIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    override fun observeSchedule(): StateFlow<List<ScheduleItem>?> = _scheduleFlow

    override fun observeMovies(): StateFlow<List<MovieItem>?> = rawMoviesFlow.asStateFlow()

    override suspend fun refreshSchedule(): Result<Unit> {
        return try {
            val items = apiService.fetchSchedule()
                .mapNotNull { it.toSimplified() }
            rawScheduleFlow.value = items
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshMovies(): Result<Unit> {
        return try {
            val mediaList = apiService.fetchMovies()
            val movies = mapScrapedMediaToMovies(mediaList)
            rawMoviesFlow.value = movies
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getItemById(id: Int): ScheduleItem? {
        val scheduleItem = _scheduleFlow.value?.find { it.id == id }
        if (scheduleItem != null) return scheduleItem
        return rawMoviesFlow.value?.find { it.id == id }?.toScheduleItem()
    }

    private fun mapScrapedMediaToMovies(mediaList: List<ScrapedMediaDto>): List<MovieItem> {
        val grouped = linkedMapOf<String, MutableList<ScrapedMediaDto>>()
        for (item in mediaList) {
            val key = item.name.trim().lowercase()
            grouped.getOrPut(key) { mutableListOf() }.add(item)
        }

        var nextId = 500000
        return grouped.map { (_, items) ->
            val first = items.first()
            val rawName = first.name.trim()
            val year = items.firstNotNullOfOrNull { it.year } ?: "2026"

            val language = when {
                rawName.contains("Korean", ignoreCase = true) -> "Korean"
                rawName.contains("Dual", ignoreCase = true) -> "Dual Audio"
                rawName.contains("Bengali", ignoreCase = true) || rawName.contains("Bangla", ignoreCase = true) -> "Bengali"
                rawName.contains("Hindi", ignoreCase = true) -> "Hindi"
                rawName.contains("Dubbed", ignoreCase = true) -> "Dubbed"
                rawName.contains("Turkish", ignoreCase = true) -> "Turkish"
                else -> "Multi"
            }

            val cleanTitle = rawName
                .replace(Regex("(?i)\\s+(Korean|Dual|Bengali|Bangla|Hindi|Dubbed|English)\\b"), "")
                .trim()
                .ifBlank { rawName }

            val qualitySources = items.map {
                MovieQualitySource(
                    quality = (it.quality ?: "HD").uppercase(),
                    link = wrapMovieStreamUrl(it.link)
                )
            }.distinctBy { it.quality }

            val primaryLink = qualitySources.firstOrNull()?.link ?: wrapMovieStreamUrl(first.link)

            MovieItem(
                id = nextId++,
                title = cleanTitle,
                year = year,
                quality = qualitySources.firstOrNull()?.quality ?: "HD",
                language = language,
                link = primaryLink,
                allQualities = qualitySources
            )
        }
    }

    private fun wrapMovieStreamUrl(rawUrl: String): String {
        if (rawUrl.isBlank()) return rawUrl
        val proxyPrefix = "https://rename.playztv3310.workers.dev/?url="

        // Normalize outdated CDN pullzones (e.g. mrtgh, crtgh) to the live active pullzone (dertg)
        val normalizedUrl = rawUrl
            .replace("mrtgh.b-cdn.net", "dertg.b-cdn.net")
            .replace("crtgh.b-cdn.net", "dertg.b-cdn.net")
            .replace("mrtgh", "dertg")

        if (normalizedUrl.startsWith(proxyPrefix)) return normalizedUrl

        return try {
            val encodedUrl = java.net.URLEncoder.encode(normalizedUrl, "UTF-8")
                .replace("+", "%20")
            "$proxyPrefix$encodedUrl"
        } catch (e: Exception) {
            "$proxyPrefix$normalizedUrl"
        }
    }
}
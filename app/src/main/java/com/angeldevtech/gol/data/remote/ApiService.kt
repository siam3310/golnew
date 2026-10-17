package com.angeldevtech.gol.data.remote

import com.angeldevtech.gol.data.models.ScheduleItemDto
import com.angeldevtech.gol.data.models.ScrapedMediaDto

interface ApiService {
    suspend fun fetchSchedule(): List<ScheduleItemDto>
    suspend fun fetchMovies(): List<ScrapedMediaDto>
}
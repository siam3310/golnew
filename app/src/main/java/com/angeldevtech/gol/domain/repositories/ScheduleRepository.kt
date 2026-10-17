package com.angeldevtech.gol.domain.repositories

import com.angeldevtech.gol.domain.models.MovieItem
import com.angeldevtech.gol.domain.models.ScheduleItem
import kotlinx.coroutines.flow.StateFlow

interface ScheduleRepository {
    fun observeSchedule(): StateFlow<List<ScheduleItem>?>
    fun observeMovies(): StateFlow<List<MovieItem>?>
    suspend fun refreshSchedule(): Result<Unit>
    suspend fun refreshMovies(): Result<Unit>
    fun getItemById(id: Int): ScheduleItem?
}
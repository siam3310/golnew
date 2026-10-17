package com.angeldevtech.gol.ui.screens.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeldevtech.gol.domain.models.MovieItem
import com.angeldevtech.gol.domain.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MoviesUIState {
    data object Loading : MoviesUIState
    data class Success(
        val movies: List<MovieItem>,
        val filteredMovies: List<MovieItem>,
        val selectedFilter: String,
        val searchQuery: String
    ) : MoviesUIState
    data class Error(val message: String) : MoviesUIState
}

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<MoviesUIState> = combine(
        repository.observeMovies(),
        _searchQuery,
        _selectedFilter
    ) { movies, query, filter ->
        if (movies == null) {
            MoviesUIState.Loading
        } else if (movies.isEmpty()) {
            MoviesUIState.Success(emptyList(), emptyList(), filter, query)
        } else {
            val filtered = movies.filter { movie ->
                val matchesQuery = query.isBlank() ||
                        movie.title.contains(query, ignoreCase = true) ||
                        movie.language.contains(query, ignoreCase = true) ||
                        movie.year.contains(query, ignoreCase = true)

                val matchesFilter = when (filter) {
                    "All" -> true
                    "1080P" -> movie.allQualities.any { it.quality.equals("1080P", ignoreCase = true) }
                    "720P" -> movie.allQualities.any { it.quality.equals("720P", ignoreCase = true) }
                    "480P" -> movie.allQualities.any { it.quality.equals("480P", ignoreCase = true) }
                    else -> movie.language.contains(filter, ignoreCase = true)
                }
                matchesQuery && matchesFilter
            }
            MoviesUIState.Success(movies, filtered, filter, query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoviesUIState.Loading
    )

    init {
        loadMovies()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun refresh(force: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshMovies()
            _isRefreshing.value = false
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            repository.refreshMovies()
        }
    }
}

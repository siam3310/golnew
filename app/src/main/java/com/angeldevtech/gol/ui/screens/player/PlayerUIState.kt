package com.angeldevtech.gol.ui.screens.player

import com.angeldevtech.gol.domain.models.ScheduleItem

enum class AspectRatioMode(val label: String) {
    RESIZE_MODE_FIT("Fit"),
    RESIZE_MODE_ZOOM("Zoom"),
    RESIZE_MODE_FILL("Fill")
}

sealed interface PlayerUIState {
    data object Loading : PlayerUIState
    data class Success(
        val scheduleItem: ScheduleItem,
        val selectedEmbedIndex: Int,
        val isOverlayVisible: Boolean = true,
        val isLoadingNewSource: Boolean = true,
        val isBuffering: Boolean = false,
        val isLive: Boolean = false,
        val error: String? = null,
        val isPlaying: Boolean = false,
        val isMuted: Boolean = false,
        val aspectRatioMode: AspectRatioMode = AspectRatioMode.RESIZE_MODE_FIT,
        val currentPositionMs: Long = 0L,
        val durationMs: Long = 0L,
        val isMovieSeries: Boolean = false
    ) : PlayerUIState
    data class Error(
        val message: String,
        val returnHomeScreen: Boolean = false
    ) : PlayerUIState
}
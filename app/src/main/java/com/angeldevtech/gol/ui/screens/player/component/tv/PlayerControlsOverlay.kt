package com.angeldevtech.gol.ui.screens.player.component.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.angeldevtech.gol.ui.screens.player.PlayerUIState
import com.angeldevtech.gol.ui.screens.player.PlayerViewModel

@Composable
fun PlayerControlsOverlay(
    state: PlayerUIState.Success,
    viewModel: PlayerViewModel,
    isButtonEnabled: Boolean,
    onSeekDelta: (Long) -> Unit = { delta -> viewModel.seekBy(delta) },
    modifier: Modifier = Modifier
) {
    val playPauseButtonFocusRequester = remember { FocusRequester() }
    val errorButtonFocusRequester = remember { FocusRequester() }
    val sourcesListFocusRequester = remember { FocusRequester() }

    var hasRequestedInitialFocus by remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(state.isOverlayVisible) {
        if (state.isOverlayVisible && !hasRequestedInitialFocus) {
            hasRequestedInitialFocus = true
            try {
                if (state.error != null) {
                    errorButtonFocusRequester.requestFocus()
                } else {
                    playPauseButtonFocusRequester.requestFocus()
                }
            } catch (_: Exception) {}
        } else if (!state.isOverlayVisible) {
            hasRequestedInitialFocus = false
        }
    }

    Box(modifier = modifier.background(Color.Black.copy(alpha = 0.4f))) {
        PlayerOverlayHeader(
            item = state.scheduleItem,
            aspectRatioMode = state.aspectRatioMode,
            isMuted = state.isMuted,
            onToggleAspectRatio = { viewModel.toggleAspectRatio() },
            onToggleMute = { viewModel.toggleMute() },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 48.dp, vertical = 24.dp)
        )

        if (state.error != null) {
            PlayerOverlayError(
                error = state.error,
                attemptRecovery = { viewModel.attemptPlayerRecovery() },
                errorButtonFocusRequester = errorButtonFocusRequester,
                modifier = Modifier.fillMaxSize()
            )
        } else if (state.isLoadingNewSource) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else if (state.isMovieSeries) {
            PlayerMovieControlsTv(
                isPlaying = state.isPlaying,
                onPlayPauseClick = {
                    viewModel.togglePlayPause()
                    viewModel.showOverlayTemporarily()
                },
                onSeekBackward = {
                    onSeekDelta(-10_000L)
                },
                onSeekForward = {
                    onSeekDelta(10_000L)
                },
                playPauseFocusRequester = playPauseButtonFocusRequester,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            PlayerOverlayPlayPauseButton(
                isPlaying = state.isPlaying,
                onClick = { viewModel.togglePlayPause() },
                keepOverlay = { viewModel.showOverlayTemporarily() },
                isButtonEnabled = isButtonEnabled,
                modifier = Modifier
                    .align(Alignment.Center)
                    .focusRequester(playPauseButtonFocusRequester)
            )
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            if (state.isMovieSeries) {
                VideoPlayLineTv(
                    currentPositionMs = state.currentPositionMs,
                    durationMs = state.durationMs,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PlayerOverlaySources(
                state = state,
                viewModel = viewModel,
                sourcesListFocusRequester = sourcesListFocusRequester,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
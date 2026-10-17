package com.angeldevtech.gol.ui.screens.player

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.angeldevtech.gol.ui.screens.player.component.tv.ErrorPlayerScreen
import com.angeldevtech.gol.ui.screens.player.component.tv.LoadingIndicator
import com.angeldevtech.gol.ui.screens.player.component.tv.PlayerControlsOverlay
import com.angeldevtech.gol.ui.screens.player.component.tv.VideoPlayer
import com.angeldevtech.gol.utils.isDpadCenterKey
import com.angeldevtech.gol.utils.isDpadMovementKey

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val window = (context as? Activity)?.window
    val player = remember { viewModel.getPlayer() }

    BackHandler(onBack = onBack)

    val isButtonEnabled = remember { mutableStateOf(true) }

    var activeSeekDelta by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(activeSeekDelta) {
        if (activeSeekDelta != null) {
            kotlinx.coroutines.delay(700)
            activeSeekDelta = null
        }
    }

    val triggerSeek: (Long) -> Unit = { deltaMs ->
        viewModel.seekBy(deltaMs)
        activeSeekDelta = deltaMs
        viewModel.showOverlayTemporarily()
    }

    LifecycleStartEffect(Unit) {
        viewModel.onLoad()
        isButtonEnabled.value = true
        onStopOrDispose { viewModel.stopPlayer() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .onKeyEvent { keyEvent ->
                val currentSuccessState = uiState as? PlayerUIState.Success
                if (keyEvent.type == KeyEventType.KeyDown) {
                    if (currentSuccessState != null && currentSuccessState.isMovieSeries) {
                        when (keyEvent.key) {
                            Key.MediaRewind, Key.MediaStepBackward -> {
                                triggerSeek(-10_000L)
                                return@onKeyEvent true
                            }
                            Key.MediaFastForward, Key.MediaStepForward -> {
                                triggerSeek(10_000L)
                                return@onKeyEvent true
                            }
                            Key.DirectionLeft -> {
                                if (!currentSuccessState.isOverlayVisible) {
                                    triggerSeek(-10_000L)
                                    return@onKeyEvent true
                                }
                            }
                            Key.DirectionRight -> {
                                if (!currentSuccessState.isOverlayVisible) {
                                    triggerSeek(10_000L)
                                    return@onKeyEvent true
                                }
                            }
                        }
                    }

                    if (keyEvent.key.isDpadMovementKey()) {
                        isButtonEnabled.value = true
                        viewModel.showOverlayTemporarily()
                        return@onKeyEvent false
                    }
                    if (keyEvent.key.isDpadCenterKey()) {
                        if (currentSuccessState != null && !currentSuccessState.isOverlayVisible) {
                            isButtonEnabled.value = false
                        }
                        viewModel.showOverlayTemporarily()
                        return@onKeyEvent false
                    }
                }
                false
            }
    ) {
        when (val state = uiState) {
            is PlayerUIState.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PlayerUIState.Error -> {
                ErrorPlayerScreen(
                    state = state,
                    retry = { viewModel.onLoad() },
                    onBack = onBack,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is PlayerUIState.Success -> {
                VideoPlayer(
                    isPlaying = state.isPlaying,
                    window = window,
                    player = player,
                    aspectRatioMode = state.aspectRatioMode
                )

                AnimatedVisibility(
                    visible = state.isOverlayVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerControlsOverlay(
                        state = state,
                        viewModel = viewModel,
                        isButtonEnabled = isButtonEnabled.value,
                        onSeekDelta = triggerSeek,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Loading indicator during buffering / seeking
                if (state.isBuffering || state.isLoadingNewSource) {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Material Design animated 10s seek indicator
                com.angeldevtech.gol.ui.screens.player.component.MaterialSeekAnimation(
                    seekDeltaMs = activeSeekDelta
                )
            }
        }
    }
}
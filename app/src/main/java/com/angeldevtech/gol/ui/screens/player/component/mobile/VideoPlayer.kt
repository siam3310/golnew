package com.angeldevtech.gol.ui.screens.player.component.mobile

import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.angeldevtech.gol.ui.screens.player.AspectRatioMode

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    isPlaying: Boolean,
    player: ExoPlayer,
    aspectRatioMode: AspectRatioMode,
    window: Window?,
    modifier: Modifier = Modifier
) {
    DisposableEffect(isPlaying) {
        if (isPlaying) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose { }
    }

    DisposableEffect(Unit) {
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val resizeMode = when (aspectRatioMode) {
        AspectRatioMode.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        AspectRatioMode.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        AspectRatioMode.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    controllerAutoShow = false
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                    this.resizeMode = resizeMode
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                if (playerView.player != player) {
                    playerView.player = player
                }
                if (playerView.resizeMode != resizeMode) {
                    playerView.resizeMode = resizeMode
                }
            },
            onRelease = { playerView ->
                playerView.player = null
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
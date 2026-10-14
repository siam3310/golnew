package com.angeldevtech.gol.ui.screens.player.component.mobile

import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import com.angeldevtech.gol.ui.screens.player.AspectRatioMode

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

    Box(
        modifier = modifier.fillMaxSize().clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val surfaceModifier = when (aspectRatioMode) {
            AspectRatioMode.RESIZE_MODE_FIT -> Modifier.aspectRatio(16f / 9f)
            AspectRatioMode.RESIZE_MODE_ZOOM -> Modifier.fillMaxSize().scale(1.35f)
            AspectRatioMode.RESIZE_MODE_FILL -> Modifier.fillMaxSize()
        }

        AndroidView(
            factory = { context ->
                SurfaceView(context).apply {
                    player.setVideoSurfaceView(this)
                }
            },
            update = { /* Surface is already attached in factory; do not reset on recomposition */ },
            onRelease = { surfaceView ->
                player.clearVideoSurfaceView(surfaceView)
            },
            modifier = surfaceModifier
        )
    }
}
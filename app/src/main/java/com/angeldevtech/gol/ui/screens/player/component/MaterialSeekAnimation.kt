package com.angeldevtech.gol.ui.screens.player.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Material Design 10s seek animation component for TV and Mobile players.
 * Displays an animated circular Material 3 indicator on the left for rewind and right for fast forward.
 */
@Composable
fun MaterialSeekAnimation(
    seekDeltaMs: Long?, // negative for rewind (-10s), positive for forward (+10s), null for hidden
    modifier: Modifier = Modifier
) {
    val isVisible = seekDeltaMs != null
    val isRewind = (seekDeltaMs ?: 0L) < 0

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.7f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "seek_scale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(120)) + scaleIn(tween(180)),
        exit = fadeOut(tween(220)) + scaleOut(tween(220)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 72.dp),
            contentAlignment = if (isRewind) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.6f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRewind) "⏪" else "⏩",
                        fontSize = 26.sp
                    )
                    Text(
                        text = if (isRewind) "-10s" else "+10s",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

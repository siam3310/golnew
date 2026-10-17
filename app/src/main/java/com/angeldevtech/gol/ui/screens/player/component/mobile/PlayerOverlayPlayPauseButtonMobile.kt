package com.angeldevtech.gol.ui.screens.player.component.mobile

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.angeldevtech.gol.R

@Composable
fun PlayerOverlayPlayPauseButtonMobile(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(72.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Icon(
            painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}
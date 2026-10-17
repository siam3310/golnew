package com.angeldevtech.gol.ui.screens.player.component.tv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.Text
import com.angeldevtech.gol.R

@Composable
fun PlayerMovieControlsTv(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeekForward: () -> Unit,
    playPauseFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Rewind 10 Seconds Button
        Button(
            onClick = onSeekBackward,
            shape = ButtonDefaults.shape(RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.colors(
                containerColor = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White,
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            ),
            border = ButtonDefaults.border(
                border = Border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f))),
                focusedBorder = Border(BorderStroke(2.5.dp, Color.White))
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⏪",
                    fontSize = 18.sp
                )
                Text(
                    text = "10s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Play / Pause Button
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(100.dp)
                .focusRequester(playPauseFocusRequester),
            shape = IconButtonDefaults.shape(CircleShape),
            colors = IconButtonDefaults.colors(
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            ),
            border = IconButtonDefaults.border(
                border = Border(BorderStroke(2.dp, Color.White.copy(alpha = 0.6f))),
                focusedBorder = Border(BorderStroke(3.dp, Color.White))
            )
        ) {
            Icon(
                painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(56.dp)
            )
        }

        // Forward 10 Seconds Button
        Button(
            onClick = onSeekForward,
            shape = ButtonDefaults.shape(RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.colors(
                containerColor = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White,
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            ),
            border = ButtonDefaults.border(
                border = Border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f))),
                focusedBorder = Border(BorderStroke(2.5.dp, Color.White))
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "10s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⏩",
                    fontSize = 18.sp
                )
            }
        }
    }
}

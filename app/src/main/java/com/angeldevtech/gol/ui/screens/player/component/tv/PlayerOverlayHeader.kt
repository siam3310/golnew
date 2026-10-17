package com.angeldevtech.gol.ui.screens.player.component.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.angeldevtech.gol.R
import com.angeldevtech.gol.domain.models.ScheduleItem
import com.angeldevtech.gol.ui.screens.player.AspectRatioMode

@Composable
fun PlayerOverlayHeader(
    item: ScheduleItem,
    aspectRatioMode: AspectRatioMode,
    isMuted: Boolean,
    onToggleAspectRatio: () -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Team logos or League logo
            if (item.team1Logo.isNotBlank() && item.team2Logo.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = item.team1Logo,
                        contentDescription = item.team1Name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "vs",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    AsyncImage(
                        model = item.team2Logo,
                        contentDescription = item.team2Name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            } else if (item.leagueImageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.leagueImageUrl,
                    contentDescription = item.leagueName,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column {
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = item.category + if (item.leagueName.isNotBlank()) " • ${item.leagueName}" else "",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onToggleAspectRatio,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_aspect_ratio),
                        contentDescription = "Aspect Ratio",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = aspectRatioMode.label)
                }
            }

            Button(
                onClick = onToggleMute,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    painter = painterResource(id = if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_up),
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
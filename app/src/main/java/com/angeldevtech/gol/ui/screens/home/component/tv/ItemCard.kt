package com.angeldevtech.gol.ui.screens.home.component.tv

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.angeldevtech.gol.R
import com.angeldevtech.gol.domain.models.ScheduleItem
import com.angeldevtech.gol.ui.screens.home.HomeViewModel
import com.angeldevtech.gol.utils.PaletteResult
import com.angeldevtech.gol.utils.darken

@Composable
fun ItemCard(
    item: ScheduleItem,
    viewModel: HomeViewModel,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit = {}
) {
    val paletteKey = item.team1Logo.ifBlank { item.leagueImageUrl }
    val paletteResult = if (paletteKey.isNotBlank()) {
        viewModel.paletteCache[paletteKey] ?: PaletteResult(
            cardColor = Color.DarkGray,
            textColor = Color.White.copy(alpha = 0.8f)
        )
    } else {
        PaletteResult(
            cardColor = Color.DarkGray,
            textColor = Color.White.copy(alpha = 0.8f)
        )
    }

    val animatedCardColor by animateColorAsState(targetValue = paletteResult.cardColor.darken())
    val animatedChipColor by animateColorAsState(targetValue = paletteResult.cardColor)

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    Column(
        modifier = Modifier.size(280.dp, 220.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .then(
                    if (focusRequester != null) {
                        Modifier.focusRequester(focusRequester)
                    } else {
                        Modifier
                    }
                ),
            border = CardDefaults.border(focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White)
            )),
            colors = CardDefaults.colors(containerColor = animatedCardColor)
        ) {
            if (item.team1Logo.isNotBlank() && item.team2Logo.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Team 1 Logo (Left side)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = item.team1Logo,
                            contentDescription = item.team1Name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit,
                            onSuccess = { state ->
                                val bitmap = (state.result.drawable as? BitmapDrawable)?.bitmap
                                bitmap?.let {
                                    viewModel.generatePalette(it, item.team1Logo)
                                }
                            }
                        )
                    }

                    // Exact Center of the card: Relative Day / Live, Simple "vs" (no background), Match Time
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isLive = item.isLive || item.relativeDay == "LIVE"
                        if (isLive) {
                            Text(
                                text = "LIVE",
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        } else {
                            val relativeDayText = when (item.relativeDay.uppercase()) {
                                "TODAY", "HOY" -> stringResource(R.string.today)
                                "TOMORROW", "MAÑANA" -> stringResource(R.string.tomorrow)
                                "YESTERDAY", "AYER" -> stringResource(R.string.yesterday)
                                else -> item.relativeDay
                            }
                            Text(
                                text = relativeDayText,
                                color = paletteResult.textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // Simple 'vs' text without background
                        Text(
                            text = "vs",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = item.hour.dropLast(3),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Team 2 Logo (Right side)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = item.team2Logo,
                            contentDescription = item.team2Name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.leagueImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = item.leagueImageUrl,
                            contentDescription = item.leagueName,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit,
                            onSuccess = { state ->
                                val bitmap = (state.result.drawable as? BitmapDrawable)?.bitmap
                                bitmap?.let {
                                    viewModel.generatePalette(it, item.leagueImageUrl)
                                }
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .background(animatedChipColor, RoundedCornerShape(4.dp))
                                .padding(8.dp, 4.dp),
                        ) {
                            val relativeDayText = when (item.relativeDay.uppercase()) {
                                "TODAY", "HOY" -> stringResource(R.string.today)
                                "TOMORROW", "MAÑANA" -> stringResource(R.string.tomorrow)
                                "YESTERDAY", "AYER" -> stringResource(R.string.yesterday)
                                else -> item.relativeDay
                            }
                            Text(
                                text = relativeDayText,
                                color = paletteResult.textColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = item.hour.dropLast(3),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (item.leagueImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.leagueImageUrl,
                        contentDescription = item.leagueName,
                        modifier = Modifier.size(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = item.leagueName.ifBlank { item.category },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
package com.angeldevtech.gol.ui.screens.home.component.mobile

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.angeldevtech.gol.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.angeldevtech.gol.domain.models.ScheduleItem
import com.angeldevtech.gol.ui.screens.home.HomeViewModel
import com.angeldevtech.gol.utils.PaletteResult
import com.angeldevtech.gol.utils.darken

@Composable
fun ItemCardMobile(
    item: ScheduleItem,
    viewModel: HomeViewModel,
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

    val animatedCardColor by animateColorAsState(targetValue = paletteResult.cardColor.darken(0.6f))
    val animatedChipColor by animateColorAsState(targetValue = paletteResult.cardColor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            colors = CardDefaults.cardColors(containerColor = animatedCardColor),
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val cardWidth = maxWidth
                val cardHeight = maxHeight
                val imageSize = cardWidth * 0.28f
                val paddingHorizontal = cardWidth * 0.05f
                val paddingVertical = cardHeight * 0.04f

                if (item.team1Logo.isNotBlank() && item.team2Logo.isNotBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Team 1 Logo (Left side)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = cardWidth * 0.08f),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = item.team1Logo,
                                contentDescription = item.team1Name,
                                modifier = Modifier
                                    .size(imageSize)
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
                            verticalArrangement = Arrangement.spacedBy(cardHeight * 0.035f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val isLive = item.isLive || item.relativeDay == "LIVE"
                            if (isLive) {
                                Text(
                                    text = "LIVE",
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = (cardHeight * 0.095f).value.sp,
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
                                    fontSize = (cardHeight * 0.085f).value.sp
                                )
                            }

                            // Simple 'vs' text without any background
                            Text(
                                text = "vs",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = (cardHeight * 0.12f).value.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = item.hour.dropLast(3),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = (cardHeight * 0.13f).value.sp
                                ),
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Team 2 Logo (Right side)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = cardWidth * 0.08f),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = item.team2Logo,
                                contentDescription = item.team2Name,
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = paddingVertical, horizontal = paddingHorizontal),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.leagueImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.leagueImageUrl,
                                contentDescription = item.leagueName,
                                modifier = Modifier.size(imageSize * 1.1f),
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
                            verticalArrangement = Arrangement.spacedBy(cardHeight * 0.04f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        animatedChipColor,
                                        RoundedCornerShape(cardWidth * 0.02f)
                                    )
                                    .padding(
                                        cardWidth * 0.03f,
                                        cardHeight * 0.02f
                                    ),
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (cardHeight * 0.10f).value.sp
                                )
                            }
                            Text(
                                text = item.hour.dropLast(3),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = (cardHeight * 0.20f).value.sp
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
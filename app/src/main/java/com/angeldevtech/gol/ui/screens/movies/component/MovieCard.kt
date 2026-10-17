package com.angeldevtech.gol.ui.screens.movies.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angeldevtech.gol.domain.models.MovieItem

@Composable
fun MovieCard(
    movie: MovieItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate distinct dark card and chip colors based on movie id/language
    val cardColor = when (movie.id % 5) {
        0 -> Color(0xFF1E2530)
        1 -> Color(0xFF241E30)
        2 -> Color(0xFF1B2E28)
        3 -> Color(0xFF2E241B)
        else -> Color(0xFF22262B)
    }

    val chipColor = when (movie.id % 5) {
        0 -> Color(0xFF335C8D)
        1 -> Color(0xFF6A3A8A)
        2 -> Color(0xFF2E6E5A)
        3 -> Color(0xFF8A5A2E)
        else -> Color(0xFF455A64)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testTag("movie_card_${movie.id}")
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 16:9 Sports-Style Card (no images)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val cardWidth = maxWidth
                val cardHeight = maxHeight

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(cardHeight * 0.04f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Center Top: Language badge only (no quality)
                    val topBadgeText = movie.language.uppercase()
                    if (topBadgeText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(
                                    chipColor,
                                    RoundedCornerShape(cardWidth * 0.025f)
                                )
                                .padding(
                                    horizontal = cardWidth * 0.04f,
                                    vertical = cardHeight * 0.025f
                                )
                        ) {
                            Text(
                                text = topBadgeText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (cardHeight * 0.09f).value.sp
                            )
                        }
                    }

                    // Card Center: Movie Series Name (Title)
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = (cardHeight * 0.17f).value.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = (cardHeight * 0.21f).value.sp
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = cardWidth * 0.06f)
                    )
                }
            }
        }

        // Details below the card matching Sports ItemCardMobile
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val subtitleParts = buildList {
                if (movie.year.isNotBlank()) add(movie.year)
                if (movie.language.isNotBlank()) add(movie.language)
                if (movie.allQualities.isNotEmpty()) {
                    add(movie.allQualities.joinToString("/") { it.quality })
                }
            }

            Text(
                text = subtitleParts.joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

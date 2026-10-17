package com.angeldevtech.gol.ui.screens.home.component.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.angeldevtech.gol.domain.models.MovieItem

@Composable
fun MovieCardTv(
    movie: MovieItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val baseCardColor = when (movie.id % 5) {
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

    val animatedCardColor by animateColorAsState(
        targetValue = if (isFocused) baseCardColor.copy(alpha = 0.95f) else baseCardColor
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = CardDefaults.colors(
            containerColor = animatedCardColor,
            focusedContainerColor = animatedCardColor
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, Color.White),
                shape = RoundedCornerShape(14.dp)
            )
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.02f
        ),
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(14.dp)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .testTag("movie_card_tv_${movie.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Movie Series Name in center/prominent text
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle: Year and available qualities
                val details = buildList {
                    if (movie.year.isNotBlank()) add(movie.year)
                    if (movie.allQualities.isNotEmpty()) {
                        add(movie.allQualities.joinToString(" • ") { it.quality })
                    } else if (movie.quality.isNotBlank()) {
                        add(movie.quality)
                    }
                }.joinToString("  |  ")

                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color.LightGray.copy(alpha = 0.85f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right side badge: Language & Stream Quality
            Box(
                modifier = Modifier
                    .background(
                        chipColor,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = movie.language.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

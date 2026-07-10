package com.nendo.argosy.ui.components.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.toColorInt
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.generated.ColorTokens

/** Circular initial-letter avatar used for social users; optional presence dot. */
@Composable
fun SocialAvatar(
    displayName: String,
    avatarColor: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    showOnlineDot: Boolean = false
) {
    val fallbackColor = MaterialTheme.colorScheme.primary
    val circleColor = try {
        avatarColor?.let { Color(it.toColorInt()) } ?: fallbackColor
    } catch (_: Exception) {
        fallbackColor
    }

    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayName.take(1).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .size(Dimens.dotLg)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.dotSm)
                        .clip(CircleShape)
                        .background(ColorTokens.Domain.Presence.online)
                )
            }
        }
    }
}

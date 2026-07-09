/**
 * DUAL-SCREEN COMPONENT - Upper display collection showcase.
 * Runs in main process (MainActivity).
 * Shows collection metadata when lower screen is in COLLECTIONS mode.
 */
package com.nendo.argosy.ui.dualscreen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.LocalBoxArtStyle
import java.io.File

data class DualCollectionShowcaseState(
    val name: String = "",
    val description: String? = null,
    val coverPaths: List<String> = emptyList(),
    val gameCount: Int = 0,
    val platformSummary: String = "",
    val totalPlaytimeMinutes: Int = 0
)

@Composable
fun DualCollectionShowcase(
    state: DualCollectionShowcaseState,
    footerHints: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalArgosyTheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.surfaceBase)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.surfaceBase.copy(alpha = 0.7f))
                    .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm + Dimens.spacingXs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.name.ifEmpty { "Collections" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (state.gameCount > 0) {
                    Text(
                        text = "${state.gameCount} games",
                        style = MaterialTheme.typography.labelLarge,
                        color = theme.focusAccent
                    )
                }
            }

            HorizontalDivider(
                color = theme.hairlineLow
            )

            Spacer(modifier = Modifier.weight(1f))

            if (state.coverPaths.isNotEmpty()) {
                val showcaseWidth = 200.dp
                val showcaseHeight = showcaseWidth / LocalBoxArtStyle.current.aspectRatio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacingXxl),
                    contentAlignment = Alignment.Center
                ) {
                    ShowcaseCoverCollage(
                        coverPaths = state.coverPaths,
                        modifier = Modifier
                            .size(width = showcaseWidth, height = showcaseHeight)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.platformSummary.isNotBlank() || state.totalPlaytimeMinutes > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(
                        modifier = Modifier
                            .background(theme.surfaceRaised.copy(alpha = 0.9f))
                            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm + Dimens.spacingXs),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (state.platformSummary.isNotBlank()) {
                            Text(
                                text = state.platformSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = theme.textDim
                            )
                        }
                        if (state.totalPlaytimeMinutes > 0) {
                            Text(
                                text = formatCollectionPlayTime(state.totalPlaytimeMinutes),
                                style = MaterialTheme.typography.titleMedium,
                                color = theme.textPrimary
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = theme.hairlineLow
            )

            footerHints()
        }
    }
}

@Composable
private fun ShowcaseCoverCollage(
    coverPaths: List<String>,
    modifier: Modifier = Modifier
) {
    val theme = LocalArgosyTheme.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(theme.surfaceRaised)
    ) {
        when {
            coverPaths.size == 1 -> {
                AsyncImage(
                    model = File(coverPaths[0]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                val displayed = coverPaths.take(4)
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        displayed.getOrNull(0)?.let { path ->
                            AsyncImage(
                                model = File(path),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                        displayed.getOrNull(1)?.let { path ->
                            AsyncImage(
                                model = File(path),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                    if (displayed.size > 2) {
                        Row(modifier = Modifier.weight(1f)) {
                            displayed.getOrNull(2)?.let { path ->
                                AsyncImage(
                                    model = File(path),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                )
                            }
                            displayed.getOrNull(3)?.let { path ->
                                AsyncImage(
                                    model = File(path),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                )
                            } ?: Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

private fun formatCollectionPlayTime(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m total"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m total"
        else -> "${minutes / 60}h total"
    }
}

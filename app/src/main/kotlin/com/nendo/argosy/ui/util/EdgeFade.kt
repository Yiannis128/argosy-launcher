package com.nendo.argosy.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/** Fades overflowing list content into transparency at the top/bottom edges. */
fun Modifier.verticalEdgeFade(
    fadeHeight: Dp,
    top: Boolean = true,
    bottom: Boolean = true,
): Modifier = graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val fadePx = fadeHeight.toPx().coerceAtMost(size.height / 2f)
        if (top) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = fadePx,
                ),
                size = Size(size.width, fadePx),
                blendMode = BlendMode.DstIn,
            )
        }
        if (bottom) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - fadePx,
                    endY = size.height,
                ),
                topLeft = Offset(0f, size.height - fadePx),
                size = Size(size.width, fadePx),
                blendMode = BlendMode.DstIn,
            )
        }
    }

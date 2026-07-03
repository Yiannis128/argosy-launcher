package com.nendo.argosy.ui.primitives

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.LocalUiScale
import com.nendo.argosy.ui.theme.generated.ColorTokens
import com.nendo.argosy.ui.theme.generated.ComponentDefaults

enum class ProgressBarStyle { Active, Paused, Working }

/** V2 progress bar: 6dp rounded track on surfaceElevated; Working = striped semantic.progress fill. */
@Composable
fun ArgosyProgressBar(
    progress: Float?,
    modifier: Modifier = Modifier,
    style: ProgressBarStyle = ProgressBarStyle.Active,
    tint: Color? = null,
) {
    val theme = LocalArgosyTheme.current
    val s = LocalUiScale.current.scale
    val fillColor = when (style) {
        ProgressBarStyle.Active -> tint ?: theme.focusAccent
        ProgressBarStyle.Paused -> theme.textMute
        ProgressBarStyle.Working ->
            if (theme.isDark) ColorTokens.Semantic.Dark.progress else ColorTokens.Semantic.Light.progress
    }
    val trackColor = theme.surfaceElevated
    Canvas(modifier = modifier.fillMaxWidth().height((ComponentDefaults.ProgressBar.height * s).dp)) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(color = trackColor, cornerRadius = radius)
        val fraction = progress?.coerceIn(0f, 1f) ?: 1f
        val fillWidth = size.width * fraction
        if (fillWidth <= 0f) return@Canvas
        val fillClip = Path().apply {
            addRoundRect(RoundRect(0f, 0f, fillWidth, size.height, radius))
        }
        clipPath(fillClip) {
            if (style == ProgressBarStyle.Working) {
                drawRect(color = fillColor.copy(alpha = 0.35f), size = Size(fillWidth, size.height))
                val stripe = (ComponentDefaults.ProgressBar.stripeWidth * s).dp.toPx()
                val gap = (ComponentDefaults.ProgressBar.stripeGap * s).dp.toPx()
                val period = stripe + gap
                var x = -size.height
                while (x < fillWidth) {
                    drawLine(
                        color = fillColor,
                        start = Offset(x, size.height),
                        end = Offset(x + size.height, 0f),
                        strokeWidth = stripe,
                    )
                    x += period
                }
            } else {
                drawRect(color = fillColor, size = Size(fillWidth, size.height))
            }
        }
    }
}

package com.nendo.argosy.ui.primitives

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.LocalUiScale
import com.nendo.argosy.ui.theme.Motion
import com.nendo.argosy.ui.theme.generated.ComponentDefaults
import com.nendo.argosy.ui.util.clickableNoFocus

/** V2 boxy toggle: rounded-square track + narrow vertical knob, knob right = on. */
@Composable
fun ArgosyToggle(
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    enabled: Boolean = true,
) {
    val theme = LocalArgosyTheme.current
    val s = LocalUiScale.current.scale
    val trackWidth = (ComponentDefaults.Toggle.trackWidth * s).dp
    val trackHeight = (ComponentDefaults.Toggle.trackHeight * s).dp
    val knobWidth = (ComponentDefaults.Toggle.knobWidth * s).dp
    val knobHeight = (ComponentDefaults.Toggle.knobHeight * s).dp
    val inset = (trackHeight - knobHeight) / 2
    val accent = if (focused) lerp(theme.focusAccent, Color.White, 0.25f) else theme.focusAccent
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> theme.surfaceRaised
            checked -> accent
            else -> theme.surfaceElevated
        },
        animationSpec = Motion.focusColorSpec,
        label = "toggle-track",
    )
    val knobX by animateDpAsState(
        targetValue = if (checked) trackWidth - knobWidth - inset else inset,
        animationSpec = Motion.focusSpringDp,
        label = "toggle-knob",
    )
    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape((ComponentDefaults.Toggle.trackRadius * s).dp))
            .background(trackColor)
            .border(
                width = Dimens.borderThin,
                color = if (checked) Color.Transparent else theme.hairlineHigh,
                shape = RoundedCornerShape((ComponentDefaults.Toggle.trackRadius * s).dp),
            )
            .clickableNoFocus(enabled = enabled) { onToggle(!checked) },
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobX, y = inset)
                .size(width = knobWidth, height = knobHeight)
                .clip(RoundedCornerShape((ComponentDefaults.Toggle.knobRadius * s).dp))
                .background(if (checked) Color.White else theme.textDim),
        )
    }
}

enum class TriangleDirection { Left, Right }

/** Filled d-pad-style triangle, the V2 enum cue (never a text chevron). */
@Composable
fun FilledTriangle(
    direction: TriangleDirection,
    tint: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 10.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            when (direction) {
                TriangleDirection.Left -> {
                    moveTo(w, 0f); lineTo(w, h); lineTo(0f, h / 2f)
                }
                TriangleDirection.Right -> {
                    moveTo(0f, 0f); lineTo(0f, h); lineTo(w, h / 2f)
                }
            }
            close()
        }
        drawPath(path, color = tint)
    }
}

/** V2 enum inline: filled triangles flank the value; triangles cycle, the value opens the list. */
@Composable
fun EnumValueControl(
    value: String,
    focused: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color? = null,
) {
    val theme = LocalArgosyTheme.current
    val triangleTint = if (focused) theme.focusAccent else theme.textMute
    val s = LocalUiScale.current.scale
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        FilledTriangle(
            direction = TriangleDirection.Left,
            tint = triangleTint,
            size = (10 * s).dp,
            modifier = Modifier.clickableNoFocus(onClick = onPrev),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor ?: (if (focused) lerp(theme.focusAccent, Color.White, 0.45f) else theme.focusAccent),
            modifier = Modifier.clickableNoFocus(onClick = onOpen),
        )
        FilledTriangle(
            direction = TriangleDirection.Right,
            tint = triangleTint,
            size = (10 * s).dp,
            modifier = Modifier.clickableNoFocus(onClick = onNext),
        )
    }
}

/** V2 stepper: `- value +`, adjust only (A never adjusts). */
@Composable
fun StepperControl(
    display: String,
    focused: Boolean,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalArgosyTheme.current
    val signTint = if (focused) theme.focusAccent else theme.textMute
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
    ) {
        Text(
            text = "-",
            style = MaterialTheme.typography.titleMedium,
            color = signTint,
            modifier = Modifier.clickableNoFocus(onClick = onDecrement).padding(horizontal = Dimens.spacingXs),
        )
        Text(
            text = display,
            style = MaterialTheme.typography.bodyMedium,
            color = if (focused) lerp(theme.focusAccent, Color.White, 0.45f) else theme.textPrimary,
        )
        Text(
            text = "+",
            style = MaterialTheme.typography.titleMedium,
            color = signTint,
            modifier = Modifier.clickableNoFocus(onClick = onIncrement).padding(horizontal = Dimens.spacingXs),
        )
    }
}

/** V2 track slider: squared track + boxy flush thumb at the fill end (no lollipop). */
@Composable
fun ArgosyTrackSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    focused: Boolean = false,
) {
    val theme = LocalArgosyTheme.current
    val s = LocalUiScale.current.scale
    val trackHeight = ComponentDefaults.TrackSlider.trackHeight * s
    val trackRadius = ComponentDefaults.TrackSlider.trackRadius * s
    val thumbSize = ComponentDefaults.TrackSlider.thumbSize * s
    val fillColor = if (focused) lerp(theme.focusAccent, Color.White, 0.25f) else theme.focusAccent
    val trackColor = theme.surfaceElevated
    fun fractionToValue(fraction: Float) = minValue + fraction.coerceIn(0f, 1f) * (maxValue - minValue)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height((thumbSize * 2).dp)
            .pointerInput(minValue, maxValue) {
                detectTapGestures { offset -> onValueChange(fractionToValue(offset.x / size.width)) }
            }
            .pointerInput(minValue, maxValue) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    onValueChange(fractionToValue(change.position.x / size.width))
                }
            },
    ) {
        val fraction = if (maxValue > minValue) ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f) else 0f
        val trackPx = trackHeight.dp.toPx()
        val thumbPx = thumbSize.dp.toPx()
        val centerY = this.size.height / 2f
        val radius = CornerRadius(trackRadius.dp.toPx(), trackRadius.dp.toPx())
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(0f, centerY - trackPx / 2f),
            size = Size(this.size.width, trackPx),
            cornerRadius = radius,
        )
        val fillWidth = this.size.width * fraction
        drawRoundRect(
            color = fillColor,
            topLeft = Offset(0f, centerY - trackPx / 2f),
            size = Size(fillWidth, trackPx),
            cornerRadius = radius,
        )
        drawRoundRect(
            color = theme.textPrimary,
            topLeft = Offset((fillWidth - thumbPx / 2f).coerceIn(0f, this.size.width - thumbPx), centerY - thumbPx / 2f),
            size = Size(thumbPx, thumbPx),
            cornerRadius = CornerRadius(trackRadius.dp.toPx(), trackRadius.dp.toPx()),
        )
    }
}

package com.nendo.argosy.ui.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.Motion
import com.nendo.argosy.ui.theme.generated.ComponentDefaults
import com.nendo.argosy.ui.util.clickableNoFocus
import com.nendo.argosy.ui.util.pressScale

/** V2 action button: wrapped fill (fill inside a brighter rim) that solidifies on focus; never moves. */
@Composable
fun ActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    primary: Boolean = false,
    accentColor: Color? = null,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val theme = LocalArgosyTheme.current
    val accent = accentColor ?: theme.focusAccent
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(Dimens.radiusControl)
    val fillBase = if (primary) accent else theme.surfaceElevated
    val rimBase = if (primary) accent else theme.hairlineHigh
    val fillAlpha by animateFloatAsState(
        targetValue = when {
            !enabled -> ComponentDefaults.ActionButton.disabledFillAlpha
            focused -> 1f
            else -> ComponentDefaults.ActionButton.restFillAlpha
        },
        animationSpec = Motion.focusSpring,
        label = "action-fill",
    )
    val rimAlpha by animateFloatAsState(
        targetValue = when {
            !enabled -> ComponentDefaults.ActionButton.disabledFillAlpha * 0.5f
            focused -> 1f
            else -> ComponentDefaults.ActionButton.restRimAlpha
        },
        animationSpec = Motion.focusSpring,
        label = "action-rim",
    )
    Box(
        modifier = modifier
            .pressScale(interaction)
            .heightIn(min = Dimens.buttonHeight)
            .clip(shape)
            .background(fillBase.copy(alpha = fillAlpha), shape)
            .border(width = Dimens.borderThin, color = rimBase.copy(alpha = rimAlpha), shape = shape)
            .clickableNoFocus(interactionSource = interaction, enabled = enabled, onClick = onClick)
            .padding(horizontal = Dimens.buttonPaddingH, vertical = Dimens.buttonPaddingV),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    primary: Boolean = false,
    accentColor: Color? = null,
    enabled: Boolean = true,
) {
    val theme = LocalArgosyTheme.current
    val labelColor = when {
        !enabled -> theme.textMute
        primary -> Color.White
        else -> theme.textPrimary
    }
    ActionButton(
        onClick = onClick,
        modifier = modifier,
        focused = focused,
        primary = primary,
        accentColor = accentColor,
        enabled = enabled,
    ) {
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = labelColor, maxLines = 1)
    }
}

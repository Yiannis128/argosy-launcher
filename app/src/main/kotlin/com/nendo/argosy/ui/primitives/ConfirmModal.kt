package com.nendo.argosy.ui.primitives

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.Motion
import com.nendo.argosy.ui.theme.generated.ColorTokens
import com.nendo.argosy.ui.util.clickableNoFocus

/** V2 warning/confirm modal: symmetric fill-less bases, focus is the fill, red marks danger. */
@Composable
fun ArgosyConfirmModal(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    focusedIndex: Int,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
    cancelLabel: String = "Cancel",
    visible: Boolean = true,
) {
    val theme = LocalArgosyTheme.current
    ModalScaffold(visible = visible, onDismiss = onDismiss, modifier = modifier) {
        Column(modifier = Modifier.padding(Dimens.spacingLg)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = theme.textPrimary,
            )
            Spacer(Modifier.height(Dimens.spacingSm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = theme.textDim,
            )
            Spacer(Modifier.height(Dimens.spacingLg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm, Alignment.End),
            ) {
                ConfirmModalButton(
                    label = cancelLabel,
                    tint = theme.focusAccent,
                    restLabelColor = theme.textPrimary,
                    focused = focusedIndex == 0,
                    onClick = onDismiss,
                )
                ConfirmModalButton(
                    label = confirmLabel,
                    tint = if (destructive) ColorTokens.Domain.difficulty else theme.focusAccent,
                    restLabelColor = if (destructive) ColorTokens.Domain.difficulty else theme.textPrimary,
                    focused = focusedIndex == 1,
                    onClick = onConfirm,
                )
            }
        }
    }
}

@Composable
private fun ConfirmModalButton(
    label: String,
    tint: Color,
    restLabelColor: Color,
    focused: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalArgosyTheme.current
    val shape = RoundedCornerShape(Dimens.radiusControl)
    val background by animateColorAsState(
        targetValue = if (focused) tint.copy(alpha = 0.18f).compositeOver(theme.surfaceRaised) else Color.Transparent,
        animationSpec = Motion.focusColorSpec,
        label = "confirm-bg",
    )
    val labelColor by animateColorAsState(
        targetValue = if (focused) lerp(tint, Color.White, 0.45f) else restLabelColor,
        animationSpec = Motion.focusColorSpec,
        label = "confirm-label",
    )
    Box(
        modifier = Modifier
            .heightIn(min = Dimens.buttonHeight)
            .clip(shape)
            .background(background)
            .border(
                width = Dimens.borderThin,
                color = if (focused) tint else Color.Transparent,
                shape = shape,
            )
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.buttonPaddingH, vertical = Dimens.buttonPaddingV),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = labelColor, maxLines = 1)
    }
}

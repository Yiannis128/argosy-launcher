package com.nendo.argosy.libretro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import com.nendo.argosy.libretro.LaunchMode
import com.nendo.argosy.ui.components.CenteredModal
import com.nendo.argosy.ui.input.InputHandler
import com.nendo.argosy.ui.input.InputResult
import com.nendo.argosy.ui.theme.ALauncherColors
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.util.clickableNoFocus

/** Launch-mode picker; the returned handler is wired by the host as its active menu handler. */
@Composable
fun GameModeSelectionDialog(
    isOnline: Boolean,
    onSelectMode: (LaunchMode) -> Unit,
    onDismiss: () -> Unit
): InputHandler {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val currentIsOnline by rememberUpdatedState(isOnline)
    val currentOnSelectMode by rememberUpdatedState(onSelectMode)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    val inputHandler = remember {
        object : InputHandler {
            private fun maxIndex(): Int = if (currentIsOnline) 1 else 0

            override fun onUp(): InputResult {
                focusedIndex = (focusedIndex - 1).coerceAtLeast(0)
                return InputResult.HANDLED
            }

            override fun onDown(): InputResult {
                focusedIndex = (focusedIndex + 1).coerceAtMost(maxIndex())
                return InputResult.HANDLED
            }

            override fun onConfirm(): InputResult {
                currentOnSelectMode(if (focusedIndex == 0) LaunchMode.NEW_CASUAL else LaunchMode.NEW_HARDCORE)
                return InputResult.HANDLED
            }

            override fun onBack(): InputResult {
                currentOnDismiss()
                return InputResult.HANDLED
            }

            override fun onLeft(): InputResult = InputResult.HANDLED
            override fun onRight(): InputResult = InputResult.HANDLED
            override fun onSecondaryAction(): InputResult = InputResult.HANDLED
            override fun onContextMenu(): InputResult = InputResult.HANDLED
            override fun onPrevSection(): InputResult = InputResult.HANDLED
            override fun onNextSection(): InputResult = InputResult.HANDLED
        }
    }

    CenteredModal(title = "How do you want to play?", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            GameModeRow(
                icon = Icons.Default.SportsEsports,
                iconTint = null,
                title = "CASUAL",
                subtitle = "Save states, rewind, cheats OK",
                isFocused = focusedIndex == 0,
                enabled = true,
                onClick = { onSelectMode(LaunchMode.NEW_CASUAL) }
            )
            GameModeRow(
                icon = Icons.Default.EmojiEvents,
                iconTint = if (isOnline) ALauncherColors.StarGold else null,
                title = "HARDCORE",
                subtitle = if (isOnline) "Original experience, achievements count more"
                    else "Requires internet connection",
                isFocused = focusedIndex == 1,
                enabled = isOnline,
                onClick = { onSelectMode(LaunchMode.NEW_HARDCORE) }
            )
        }
    }

    return inputHandler
}

@Composable
private fun GameModeRow(
    icon: ImageVector,
    iconTint: Color?,
    title: String,
    subtitle: String,
    isFocused: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalArgosyTheme.current
    val shape = RoundedCornerShape(Dimens.radiusControl)
    val background = if (isFocused) {
        theme.focusAccent.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    val titleColor = if (isFocused) lerp(theme.focusAccent, Color.White, 0.45f)
        else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .clickableNoFocus(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.5f)
            .padding(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.iconMd),
            tint = iconTint ?: if (isFocused) titleColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(Dimens.spacingMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

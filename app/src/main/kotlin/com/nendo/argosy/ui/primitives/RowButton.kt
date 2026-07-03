package com.nendo.argosy.ui.primitives

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nendo.argosy.ui.components.InputButton
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.util.clickableNoFocus
import com.nendo.argosy.ui.util.pressScale

/** V2 list/menu row: focus fill when plain, stripe + fill when accented (nav/drill row). */
@Composable
fun RowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    accent: Color? = null,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val theme = LocalArgosyTheme.current
    val tint = accent ?: theme.focusAccent
    val interaction = remember { MutableInteractionSource() }
    val indicators = if (accent != null) FocusIndicators.NavRow else FocusIndicators.ListRow
    val shape = RoundedCornerShape(Dimens.radiusControl)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction)
            .argosyFocusIndicators(
                focused = focused,
                indicators = indicators,
                tint = tint,
                stripeColor = tint,
                shape = shape,
            )
            .clip(shape)
            .heightIn(min = Dimens.menuRowHeight)
            .clickableNoFocus(interactionSource = interaction, enabled = enabled, onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

@Composable
fun RowButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingHint: InputButton? = null,
    subtitle: String? = null,
    accent: Color? = null,
    enabled: Boolean = true,
) {
    val theme = LocalArgosyTheme.current
    val labelColor = if (enabled) theme.textPrimary else theme.textMute
    val subtitleColor = if (enabled) theme.textDim else theme.textMute
    RowButton(
        onClick = onClick,
        modifier = modifier,
        focused = focused,
        accent = accent,
        enabled = enabled,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.size(Dimens.iconMd),
            )
            Spacer(Modifier.width(Dimens.spacingMd))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.titleSmall, color = labelColor, maxLines = 1)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = subtitleColor, maxLines = 1)
            }
        }
        if (trailingHint != null) {
            Spacer(Modifier.width(Dimens.spacingMd))
            InputGlyph(button = trailingHint, size = Dimens.iconSm)
        }
    }
}

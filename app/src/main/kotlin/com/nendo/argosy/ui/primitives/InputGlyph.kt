package com.nendo.argosy.ui.primitives

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nendo.argosy.ui.components.InputButton
import com.nendo.argosy.ui.icons.InputIcons
import com.nendo.argosy.ui.input.LocalABIconsSwapped
import com.nendo.argosy.ui.input.LocalSwapStartSelect
import com.nendo.argosy.ui.input.LocalXYIconsSwapped
import com.nendo.argosy.ui.theme.LocalArgosyTheme

@Composable
fun inputButtonPainter(button: InputButton): Painter? = when (button) {
    InputButton.A -> if (LocalABIconsSwapped.current) InputIcons.FaceRight else InputIcons.FaceBottom
    InputButton.B -> if (LocalABIconsSwapped.current) InputIcons.FaceBottom else InputIcons.FaceRight
    InputButton.X -> if (LocalXYIconsSwapped.current) InputIcons.FaceTop else InputIcons.FaceLeft
    InputButton.Y -> if (LocalXYIconsSwapped.current) InputIcons.FaceLeft else InputIcons.FaceTop
    InputButton.DPAD -> InputIcons.Dpad
    InputButton.DPAD_UP -> InputIcons.DpadUp
    InputButton.DPAD_DOWN -> InputIcons.DpadDown
    InputButton.DPAD_LEFT -> InputIcons.DpadLeft
    InputButton.DPAD_RIGHT -> InputIcons.DpadRight
    InputButton.DPAD_HORIZONTAL -> InputIcons.DpadHorizontal
    InputButton.DPAD_VERTICAL -> InputIcons.DpadVertical
    InputButton.LB -> InputIcons.BumperLeft
    InputButton.RB -> InputIcons.BumperRight
    InputButton.LB_RB -> null
    InputButton.LT -> InputIcons.TriggerLeft
    InputButton.RT -> InputIcons.TriggerRight
    InputButton.LT_RT -> null
    InputButton.START -> if (LocalSwapStartSelect.current) InputIcons.Options else InputIcons.Menu
    InputButton.SELECT -> if (LocalSwapStartSelect.current) InputIcons.Menu else InputIcons.Options
}

@Composable
fun InputGlyph(
    button: InputButton,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color? = null,
) {
    val painter = inputButtonPainter(button) ?: return
    val effectiveTint = tint ?: LocalArgosyTheme.current.textDim
    Icon(
        painter = painter,
        contentDescription = button.name,
        tint = effectiveTint,
        modifier = modifier.size(size),
    )
}

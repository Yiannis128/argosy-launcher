package com.nendo.argosy.ui.screens.settings.components

import androidx.compose.runtime.Composable
import com.nendo.argosy.ui.components.FooterHints
import com.nendo.argosy.ui.components.InputButton

@Composable
fun SettingsFooter() {
    FooterHints(
        hints = listOf(
            InputButton.DPAD to "Navigate",
            InputButton.A to "Select",
            InputButton.B to "Back"
        )
    )
}

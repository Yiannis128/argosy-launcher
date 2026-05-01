package com.nendo.argosy.hardware

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.nendo.argosy.ui.theme.ProvideArgosyThemeLocals
import com.nendo.argosy.ui.theme.ThemeViewModel
import com.nendo.argosy.ui.theme.Typography
import com.nendo.argosy.ui.theme.argosyColorScheme
import com.nendo.argosy.ui.theme.rememberArgosyPalette

/**
 * Theme for the secondary display. Delegates palette resolution, color-scheme
 * construction, and CompositionLocals provision to the same helpers used by
 * [com.nendo.argosy.ui.theme.ALauncherTheme] so the two displays cannot
 * disagree on dark-mode resolution, accent fallbacks, or cover-art style.
 *
 * The host activity broadcasts its primary color (the user-set or default
 * accent) so live accent changes on the primary screen propagate immediately;
 * the override wins over the user-pref color stored in [ThemeViewModel].
 */
@Composable
fun SecondaryHomeTheme(
    primaryColor: Int? = null,
    viewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val themeState by viewModel.themeState.collectAsState()
    val palette = rememberArgosyPalette(
        themeState = themeState,
        primaryOverride = primaryColor?.let { Color(it) }
    )

    ProvideArgosyThemeLocals(themeState = themeState, palette = palette) {
        MaterialTheme(
            colorScheme = argosyColorScheme(palette),
            typography = Typography,
            content = content
        )
    }
}

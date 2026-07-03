package com.nendo.argosy.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.nendo.argosy.ui.theme.generated.ColorTokens

/** Resolved V2 surface/text ramp; every field is user-theme value falling back to token. */
data class ArgosyThemeTokens(
    val isDark: Boolean,
    val focusAccent: Color,
    val surfaceBase: Color,
    val surfaceRaised: Color,
    val surfaceElevated: Color,
    val hairlineLow: Color,
    val hairlineHigh: Color,
    val textPrimary: Color,
    val textDim: Color,
    val textMute: Color,
)

fun argosyThemeTokens(isDark: Boolean, focusAccent: Color? = null): ArgosyThemeTokens {
    return if (isDark) ArgosyThemeTokens(
        isDark = true,
        focusAccent = focusAccent ?: ColorTokens.Scheme.Dark.primary,
        surfaceBase = ColorTokens.Scheme.Dark.surface,
        surfaceRaised = ColorTokens.Scheme.Dark.surfaceVariant,
        surfaceElevated = ColorTokens.Scheme.Dark.surfaceElevated,
        hairlineLow = ColorTokens.Scheme.Dark.outlineVariant,
        hairlineHigh = ColorTokens.Scheme.Dark.outline,
        textPrimary = ColorTokens.Scheme.Dark.onSurface,
        textDim = ColorTokens.Scheme.Dark.onSurface.copy(alpha = 0.7f),
        textMute = ColorTokens.Scheme.Dark.onSurface.copy(alpha = 0.45f),
    ) else ArgosyThemeTokens(
        isDark = false,
        focusAccent = focusAccent ?: ColorTokens.Scheme.Light.primary,
        surfaceBase = ColorTokens.Scheme.Light.surface,
        surfaceRaised = ColorTokens.Scheme.Light.surfaceVariant,
        surfaceElevated = ColorTokens.Scheme.Light.surfaceElevated,
        hairlineLow = ColorTokens.Scheme.Light.outlineVariant,
        hairlineHigh = ColorTokens.Scheme.Light.outline,
        textPrimary = ColorTokens.Scheme.Light.onSurface,
        textDim = ColorTokens.Scheme.Light.onSurface.copy(alpha = 0.7f),
        textMute = ColorTokens.Scheme.Light.onSurface.copy(alpha = 0.55f),
    )
}

fun argosyThemeTokens(palette: ArgosyPalette): ArgosyThemeTokens =
    argosyThemeTokens(isDark = palette.isDarkTheme, focusAccent = palette.effectivePrimary)

val LocalArgosyTheme = staticCompositionLocalOf { argosyThemeTokens(isDark = true) }

val LocalActiveGamePalette = compositionLocalOf<List<Color>> { emptyList() }

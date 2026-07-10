package com.nendo.argosy.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.nendo.argosy.ui.theme.generated.ColorTokens

/** Resolved V2 surface/text ramp; every field is user-theme value falling back to token. */
data class ArgosyThemeTokens(
    val isDark: Boolean,
    val focusAccent: Color,
    val destructive: Color,
    val surfaceBase: Color,
    val surfaceRaised: Color,
    val surfaceElevated: Color,
    val hairlineLow: Color,
    val hairlineHigh: Color,
    val textPrimary: Color,
    val textDim: Color,
    val textMute: Color,
)

fun argosyThemeTokens(
    isDark: Boolean,
    focusAccent: Color? = null,
    tintHue: Float = 0f,
    tintBleed: Int = 0
): ArgosyThemeTokens {
    return if (isDark) ArgosyThemeTokens(
        isDark = true,
        focusAccent = focusAccent ?: ColorTokens.Scheme.Dark.primary,
        destructive = lerp(ColorTokens.Domain.difficulty, Color.White, 0.25f),
        surfaceBase = tintSurface(ColorTokens.Scheme.Dark.surface, tintHue, tintBleed),
        surfaceRaised = tintSurface(ColorTokens.Scheme.Dark.surfaceVariant, tintHue, tintBleed),
        surfaceElevated = tintSurface(ColorTokens.Scheme.Dark.surfaceElevated, tintHue, tintBleed),
        hairlineLow = tintSurface(ColorTokens.Scheme.Dark.outlineVariant, tintHue, tintBleed),
        hairlineHigh = tintSurface(ColorTokens.Scheme.Dark.outline, tintHue, tintBleed),
        textPrimary = ColorTokens.Scheme.Dark.onSurface,
        textDim = ColorTokens.Scheme.Dark.onSurface.copy(alpha = 0.7f),
        textMute = ColorTokens.Scheme.Dark.onSurface.copy(alpha = 0.45f),
    ) else ArgosyThemeTokens(
        isDark = false,
        focusAccent = focusAccent ?: ColorTokens.Scheme.Light.primary,
        destructive = ColorTokens.Domain.difficulty,
        surfaceBase = tintSurface(ColorTokens.Scheme.Light.surface, tintHue, tintBleed),
        surfaceRaised = tintSurface(ColorTokens.Scheme.Light.surfaceVariant, tintHue, tintBleed),
        surfaceElevated = tintSurface(ColorTokens.Scheme.Light.surfaceElevated, tintHue, tintBleed),
        hairlineLow = tintSurface(ColorTokens.Scheme.Light.outlineVariant, tintHue, tintBleed),
        hairlineHigh = tintSurface(ColorTokens.Scheme.Light.outline, tintHue, tintBleed),
        textPrimary = ColorTokens.Scheme.Light.onSurface,
        textDim = ColorTokens.Scheme.Light.onSurface.copy(alpha = 0.7f),
        textMute = ColorTokens.Scheme.Light.onSurface.copy(alpha = 0.55f),
    )
}

fun argosyThemeTokens(palette: ArgosyPalette): ArgosyThemeTokens =
    argosyThemeTokens(
        isDark = palette.isDarkTheme,
        focusAccent = palette.effectivePrimary,
        tintHue = palette.surfaceTintHue,
        tintBleed = palette.surfaceTintBleed
    )

val LocalArgosyTheme = staticCompositionLocalOf { argosyThemeTokens(isDark = true) }

val LocalActiveGamePalette = compositionLocalOf<List<Color>> { emptyList() }

/** Gradient end for track fills: value shifted by [ratio] (up in dark, down in light), saturation dropped by [ratio]. */
fun trackGradientEnd(base: Color, isDark: Boolean, ratio: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.argb(
            (base.alpha * 255).toInt(),
            (base.red * 255).toInt(),
            (base.green * 255).toInt(),
            (base.blue * 255).toInt()
        ),
        hsv
    )
    hsv[1] = (hsv[1] * (1f - ratio)).coerceIn(0f, 1f)
    hsv[2] = (hsv[2] * if (isDark) 1f + ratio else 1f - ratio).coerceIn(0f, 1f)
    return Color(android.graphics.Color.HSVToColor((base.alpha * 255).toInt(), hsv))
}

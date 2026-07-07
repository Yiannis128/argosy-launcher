package com.nendo.argosy.ui.theme

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.nendo.argosy.ui.theme.generated.TypographyTokens
import java.io.File

data class CustomFontFamilies(
    val display: FontFamily? = null,
    val body: FontFamily? = null
)

object CustomFontLoader {

    fun fontsDir(context: Context): File = File(context.filesDir, "fonts")

    fun loadFamily(path: String): FontFamily? {
        val file = File(path)
        if (!file.isFile) return null
        return try {
            validate(file)
            FontFamily(Typeface.createFromFile(file))
        } catch (e: Exception) {
            null
        }
    }

    /** Throws if the file is not a parseable font. */
    fun validate(file: File) {
        val typeface = Typeface.createFromFile(file)
        check(typeface != null && typeface != Typeface.DEFAULT) { "Typeface could not be loaded" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.graphics.fonts.Font.Builder(file).build()
        }
    }
}

private fun TextStyle.withFamily(family: FontFamily?): TextStyle =
    if (family != null) copy(fontFamily = family) else this

/** Material typography with DISPLAY driving display/headline/title styles and BODY driving body/label styles; defaults pass through untouched. */
fun argosyTypography(fonts: CustomFontFamilies): Typography {
    if (fonts.display == null && fonts.body == null) return TypographyTokens.Material3
    return Typography(
        displayLarge = TypographyTokens.displayLarge.withFamily(fonts.display),
        displayMedium = TypographyTokens.displayMedium.withFamily(fonts.display),
        displaySmall = TypographyTokens.displaySmall.withFamily(fonts.display),
        headlineLarge = TypographyTokens.headlineLarge.withFamily(fonts.display),
        headlineMedium = TypographyTokens.headlineMedium.withFamily(fonts.display),
        headlineSmall = TypographyTokens.headlineSmall.withFamily(fonts.display),
        titleLarge = TypographyTokens.titleLarge.withFamily(fonts.display),
        titleMedium = TypographyTokens.titleMedium.withFamily(fonts.display),
        titleSmall = TypographyTokens.titleSmall.withFamily(fonts.display),
        bodyLarge = TypographyTokens.bodyLarge.withFamily(fonts.body),
        bodyMedium = TypographyTokens.bodyMedium.withFamily(fonts.body),
        bodySmall = TypographyTokens.bodySmall.withFamily(fonts.body),
        labelLarge = TypographyTokens.labelLarge.withFamily(fonts.body),
        labelMedium = TypographyTokens.labelMedium.withFamily(fonts.body),
        labelSmall = TypographyTokens.labelSmall.withFamily(fonts.body)
    )
}

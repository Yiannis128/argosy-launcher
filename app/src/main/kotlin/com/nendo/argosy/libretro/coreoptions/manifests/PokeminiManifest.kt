package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object PokeminiManifest : CoreOptionManifest {
    override val coreId = "pokemini"
    override val options = listOf(
        CoreOptionDef(
            key = "pokemini_video_scale",
            displayName = "Video Scale",
            values = listOf("1x", "2x", "3x", "4x", "5x", "6x", "7x"),
            defaultValue = "4x",
            description = "Multiplies the internal resolution for a larger display"
        ),
        CoreOptionDef(
            key = "pokemini_60hz_mode",
            displayName = "60Hz Mode",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Update the display at 60Hz instead of the native 72Hz by dropping every sixth frame"
        ),
        CoreOptionDef(
            key = "pokemini_lcdfilter",
            displayName = "LCD Filter",
            values = listOf("dotmatrix", "scanline", "none"),
            defaultValue = "dotmatrix",
            description = "Applies a visual filter to simulate the original LCD screen",
            valueLabels = mapOf("dotmatrix" to "Dot Matrix", "scanline" to "Scanlines", "none" to "None")
        ),
        CoreOptionDef(
            key = "pokemini_lcdmode",
            displayName = "LCD Mode",
            values = listOf("analog", "3shades", "2shades"),
            defaultValue = "analog",
            description = "Sets how many shades the LCD simulation uses",
            valueLabels = mapOf("analog" to "Analog", "3shades" to "3 Shades", "2shades" to "2 Shades")
        ),
        CoreOptionDef(
            key = "pokemini_lcdcontrast",
            displayName = "LCD Contrast",
            values = listOf("0", "16", "32", "48", "64", "80", "96"),
            defaultValue = "64"
        ),
        CoreOptionDef(
            key = "pokemini_lcdbright",
            displayName = "LCD Brightness",
            values = listOf("-80", "-60", "-40", "-20", "0", "20", "40", "60", "80"),
            defaultValue = "0"
        ),
        CoreOptionDef(
            key = "pokemini_palette",
            displayName = "Palette",
            values = listOf(
                "Default", "Old", "Monochrome", "Green", "Green Vector",
                "Red", "Red Vector", "Blue LCD", "LEDBacklight", "Girl Power",
                "Blue", "Blue Vector", "Sepia", "Monochrome Vector"
            ),
            defaultValue = "Default",
            description = "Selects the color scheme used to render the display",
            valueLabels = mapOf(
                "Monochrome" to "Black & White",
                "Green Vector" to "Inverted Green",
                "Red Vector" to "Inverted Red",
                "LEDBacklight" to "LED Backlight",
                "Blue Vector" to "Inverted Blue",
                "Monochrome Vector" to "Inverted Black & White"
            )
        ),
        CoreOptionDef(
            key = "pokemini_piezofilter",
            displayName = "Piezo Filter",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Filters the piezo speaker audio to reduce harshness"
        ),
        CoreOptionDef(
            key = "pokemini_lowpass_filter",
            displayName = "Low Pass Filter",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Softens the harsh sound of the piezoelectric speaker"
        ),
        CoreOptionDef(
            key = "pokemini_lowpass_range",
            displayName = "Low Pass Filter Level (percent)",
            values = listOf("5", "15", "25", "35", "45", "55", "60", "65", "75", "85", "95"),
            defaultValue = "60",
            description = "Sets the cut-off frequency of the low pass audio filter"
        ),
        CoreOptionDef(
            key = "pokemini_screen_shake_lv",
            displayName = "Screen Shake Level",
            values = listOf("0", "1", "2", "3"),
            defaultValue = "3",
            description = "Shakes the screen to simulate the Pokemon Mini's rumble motor"
        ),
        CoreOptionDef(
            key = "pokemini_rumble_lv",
            displayName = "Controller Rumble Level",
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            defaultValue = "10",
            description = "Sets the intensity of physical controller rumble"
        ),
        CoreOptionDef(
            key = "pokemini_turbo_period",
            displayName = "Turbo Button Period",
            values = listOf("4", "8", "12", "18", "24", "32", "48", "64", "80", "100", "120"),
            defaultValue = "18",
            description = "Sets the repeat interval in frames when holding the Turbo button"
        ),
    )
}

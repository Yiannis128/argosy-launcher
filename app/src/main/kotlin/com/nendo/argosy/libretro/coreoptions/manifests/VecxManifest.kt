package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object VecxManifest : CoreOptionManifest {
    override val coreId = "vecx"
    override val options = listOf(
        CoreOptionDef(
            key = "vecx_use_hw",
            displayName = "Use Hardware Rendering",
            values = listOf("Software", "Hardware"),
            defaultValue = "Hardware",
            description = "Switches between GPU-accelerated and software rendering"
        ),
        CoreOptionDef(
            key = "vecx_res_multi",
            displayName = "Internal Resolution Multiplier",
            values = listOf("1", "2", "3", "4"),
            defaultValue = "1",
            description = "Scales the software-rendered internal resolution"
        ),
        CoreOptionDef(
            key = "vecx_res_hw",
            displayName = "Hardware Rendering Resolution",
            values = listOf(
                "434x540", "515x640", "580x720", "618x768", "824x1024",
                "845x1050", "869x1080", "966x1200", "1159x1440", "1648x2048"
            ),
            defaultValue = "824x1024",
            description = "Sets the internal resolution when using hardware rendering"
        ),
        CoreOptionDef(
            key = "vecx_line_brightness",
            displayName = "Line Brightness",
            values = listOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            ),
            defaultValue = "4",
            description = "Controls the brightness of vector lines"
        ),
        CoreOptionDef(
            key = "vecx_line_width",
            displayName = "Line Width",
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9"),
            defaultValue = "4",
            description = "Controls the thickness of vector lines"
        ),
        CoreOptionDef(
            key = "vecx_line_color",
            displayName = "Line Color",
            values = listOf("White", "Green", "Cyan", "Yellow", "Magenta", "Red", "Blue"),
            defaultValue = "White",
            description = "Tints the emulated vector lines"
        ),
        CoreOptionDef(
            key = "vecx_bloom_brightness",
            displayName = "Bloom Brightness",
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
            defaultValue = "4",
            description = "Controls the brightness of the glow effect around vector lines"
        ),
        CoreOptionDef(
            key = "vecx_bloom_width",
            displayName = "Bloom Width",
            values = listOf("2x", "3x", "4x", "6x", "8x", "10x", "12x", "14x", "16x"),
            defaultValue = "8x",
            description = "Controls the spread of the glow effect around vector lines"
        ),
        CoreOptionDef(
            key = "vecx_scale_x",
            displayName = "Horizontal Scale",
            values = listOf(
                "0.845", "0.85", "0.86", "0.87", "0.88", "0.89", "0.90",
                "0.91", "0.92", "0.93", "0.94", "0.95", "0.96", "0.97",
                "0.98", "0.99", "1", "1.005", "1.01"
            ),
            defaultValue = "1",
            description = "Stretches or shrinks the image horizontally"
        ),
        CoreOptionDef(
            key = "vecx_scale_y",
            displayName = "Vertical Scale",
            values = listOf(
                "0.845", "0.85", "0.86", "0.87", "0.88", "0.89", "0.90",
                "0.91", "0.92", "0.93", "0.94", "0.95", "0.96", "0.97",
                "0.98", "0.99", "1", "1.005", "1.01"
            ),
            defaultValue = "1",
            description = "Stretches or shrinks the image vertically"
        ),
        CoreOptionDef(
            key = "vecx_shift_x",
            displayName = "Horizontal Shift",
            values = listOf(
                "-0.03", "-0.025", "-0.02", "-0.015", "-0.01", "-0.005",
                "0", "0.005", "0.01", "0.015", "0.02", "0.025", "0.03"
            ),
            defaultValue = "0",
            description = "Moves the image left or right"
        ),
        CoreOptionDef(
            key = "vecx_shift_y",
            displayName = "Vertical Shift",
            values = listOf(
                "-0.035", "-0.03", "-0.025", "-0.02", "-0.015", "-0.01", "-0.005",
                "0", "0.005", "0.01", "0.015", "0.02", "0.025", "0.03",
                "0.035", "0.04", "0.045", "0.05", "0.06", "0.07", "0.08",
                "0.09", "0.1"
            ),
            defaultValue = "0",
            description = "Moves the image up or down"
        ),
    )
}

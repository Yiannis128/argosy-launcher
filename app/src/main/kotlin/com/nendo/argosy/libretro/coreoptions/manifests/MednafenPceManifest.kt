package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object MednafenPceManifest : CoreOptionManifest {
    override val coreId = "mednafen_pce"
    override val options = listOf(
        CoreOptionDef(
            key = "pce_palette",
            displayName = "Palette",
            values = listOf("RGB", "Composite"),
            defaultValue = "RGB",
            description = "Selects between clean RGB output or composite video color simulation"
        ),
        CoreOptionDef(
            key = "pce_aspect_ratio",
            displayName = "Aspect Ratio",
            values = listOf("auto", "6:5", "4:3", "uncorrected"),
            defaultValue = "auto",
            valueLabels = mapOf("auto" to "Auto", "uncorrected" to "Uncorrected")
        ),
        CoreOptionDef(
            key = "pce_scaling",
            displayName = "Resolution Scaling",
            values = listOf("auto", "lores", "hires"),
            defaultValue = "auto",
            description = "High Resolution stays at maximum width; Low Resolution can crush pixels",
            valueLabels = mapOf("auto" to "Auto", "lores" to "Low Resolution", "hires" to "High Resolution")
        ),
        CoreOptionDef(
            key = "pce_hires_blend",
            displayName = "High Resolution Blending Strength",
            values = listOf("disabled", "1", "2", "3", "4", "5", "6", "7", "8"),
            defaultValue = "disabled",
            description = "Blends pixels together in High Resolution mode; higher values blur more"
        ),
        CoreOptionDef(
            key = "pce_h_overscan",
            displayName = "Show Horizontal Overscan",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "Auto tries to adapt to games, cropping empty areas",
            valueLabels = mapOf("auto" to "Auto")
        ),
        CoreOptionDef(
            key = "pce_initial_scanline",
            displayName = "Initial Scanline",
            values = (0..40).map { it.toString() },
            defaultValue = "3",
            description = "Sets the first visible scanline to crop the top of the display"
        ),
        CoreOptionDef(
            key = "pce_last_scanline",
            displayName = "Last Scanline",
            values = (208..242).map { it.toString() },
            defaultValue = "242",
            description = "Sets the last visible scanline to crop the bottom of the display"
        ),
        CoreOptionDef(
            key = "pce_psgrevision",
            displayName = "PSG Audio Chip (Restart Required)",
            values = listOf("HuC6280", "HuC6280A", "auto"),
            defaultValue = "HuC6280A",
            description = "HuC6280 represents the original PC Engine, HuC6280A the SuperGrafx and CoreGrafx I",
            valueLabels = mapOf("auto" to "Auto")
        ),
        CoreOptionDef(
            key = "pce_resamp_quality",
            displayName = "Owl Resampler Quality",
            values = listOf("0", "1", "2", "3", "4", "5", "6"),
            defaultValue = "0",
            description = "Higher values give better signal quality at increased computation cost"
        ),
        CoreOptionDef(
            key = "pce_mouse_sensitivity",
            displayName = "Mouse Sensitivity",
            values = listOf(
                "0.125", "0.250", "0.375", "0.500", "0.625", "0.750", "0.875",
                "1.000", "1.125", "1.25", "1.50", "1.75", "2.00",
                "2.25", "2.50", "2.75", "3.00", "3.25", "3.50", "3.75", "4.00",
                "4.25", "4.50", "4.75", "5.00"
            ),
            defaultValue = "1.25"
        ),
        CoreOptionDef(
            key = "pce_up_down_allowed",
            displayName = "Allow Opposing Directions",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Allows pressing left+right or up+down at the same time"
        ),
        CoreOptionDef(
            key = "pce_disable_softreset",
            displayName = "Disable Soft Reset",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Prevents the Run+Select button combo from triggering a soft reset"
        ),
        CoreOptionDef(
            key = "pce_multitap",
            displayName = "Multitap 5-port Controller",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Enables up to 5-player multitap emulation"
        ),
        CoreOptionDef(
            key = "pce_default_joypad_type_p1",
            displayName = "P1 Default Joypad Type",
            values = listOf("2 Buttons", "6 Buttons"),
            defaultValue = "2 Buttons",
            description = "Sets whether Player 1 uses a 2-button or 6-button controller"
        ),
        CoreOptionDef(
            key = "pce_default_joypad_type_p2",
            displayName = "P2 Default Joypad Type",
            values = listOf("2 Buttons", "6 Buttons"),
            defaultValue = "2 Buttons",
            description = "Sets whether Player 2 uses a 2-button or 6-button controller"
        ),
        CoreOptionDef(
            key = "pce_default_joypad_type_p3",
            displayName = "P3 Default Joypad Type",
            values = listOf("2 Buttons", "6 Buttons"),
            defaultValue = "2 Buttons",
            description = "Sets whether Player 3 uses a 2-button or 6-button controller"
        ),
        CoreOptionDef(
            key = "pce_default_joypad_type_p4",
            displayName = "P4 Default Joypad Type",
            values = listOf("2 Buttons", "6 Buttons"),
            defaultValue = "2 Buttons",
            description = "Sets whether Player 4 uses a 2-button or 6-button controller"
        ),
        CoreOptionDef(
            key = "pce_default_joypad_type_p5",
            displayName = "P5 Default Joypad Type",
            values = listOf("2 Buttons", "6 Buttons"),
            defaultValue = "2 Buttons",
            description = "Sets whether Player 5 uses a 2-button or 6-button controller"
        ),
        CoreOptionDef(
            key = "pce_Turbo_Toggling",
            displayName = "Turbo Hotkey Mode",
            values = listOf("disabled", "toggle", "always"),
            defaultValue = "disabled",
            description = "Sets how turbo hotkey buttons behave",
            valueLabels = mapOf("toggle" to "Toggle", "always" to "Dedicated")
        ),
        CoreOptionDef(
            key = "pce_turbo_toggle_hotkey",
            displayName = "Alternate Turbo Hotkey",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables an alternate hotkey combination for turbo toggling"
        ),
        CoreOptionDef(
            key = "pce_Turbo_Delay",
            displayName = "Turbo Speed",
            values = listOf("Fast", "Medium", "Slow"),
            defaultValue = "Fast",
            description = "Sets how fast button presses are repeated"
        ),
        CoreOptionDef(
            key = "pce_cdimagecache",
            displayName = "CD Image Cache",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Loads the entire CD image into memory for faster access"
        ),
        CoreOptionDef(
            key = "pce_cdbios",
            displayName = "CD BIOS",
            values = listOf(
                "Games Express", "System Card 1", "System Card 2",
                "System Card 3", "System Card 2 US", "System Card 3 US"
            ),
            defaultValue = "System Card 3",
            description = "Selects which CD System Card BIOS to use for CD-ROM games"
        ),
        CoreOptionDef(
            key = "pce_arcadecard",
            displayName = "Arcade Card (Restart Required)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Allows enhanced modes of ACD-enhanced Super CD-ROM games"
        ),
        CoreOptionDef(
            key = "pce_cdspeed",
            displayName = "CD Speed",
            values = listOf("1", "2", "4", "8"),
            defaultValue = "1",
            description = "Sets the CD-ROM read speed multiplier to reduce loading times",
            valueLabels = mapOf("1" to "1x (native)", "2" to "2x", "4" to "4x", "8" to "8x")
        ),
        CoreOptionDef(
            key = "pce_adpcmextraprec",
            displayName = "(CD) ADPCM Precision",
            values = listOf("10-bit", "12-bit"),
            defaultValue = "10-bit",
            description = "Full 12-bit precision can reduce whining noise during ADPCM playback"
        ),
        CoreOptionDef(
            key = "pce_adpcmvolume",
            displayName = "(CD) ADPCM Volume %",
            values = listOf(
                "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
                "110", "120", "130", "140", "150", "160", "170", "180", "190", "200"
            ),
            defaultValue = "100",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%", "100" to "100%",
                "110" to "110%", "120" to "120%", "130" to "130%",
                "140" to "140%", "150" to "150%", "160" to "160%",
                "170" to "170%", "180" to "180%", "190" to "190%", "200" to "200%"
            )
        ),
        CoreOptionDef(
            key = "pce_cddavolume",
            displayName = "(CD) CDDA Volume %",
            values = listOf(
                "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
                "110", "120", "130", "140", "150", "160", "170", "180", "190", "200"
            ),
            defaultValue = "100",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%", "100" to "100%",
                "110" to "110%", "120" to "120%", "130" to "130%",
                "140" to "140%", "150" to "150%", "160" to "160%",
                "170" to "170%", "180" to "180%", "190" to "190%", "200" to "200%"
            )
        ),
        CoreOptionDef(
            key = "pce_cdpsgvolume",
            displayName = "(CD) CD PSG Volume %",
            values = listOf(
                "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
                "110", "120", "130", "140", "150", "160", "170", "180", "190", "200"
            ),
            defaultValue = "100",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%", "100" to "100%",
                "110" to "110%", "120" to "120%", "130" to "130%",
                "140" to "140%", "150" to "150%", "160" to "160%",
                "170" to "170%", "180" to "180%", "190" to "190%", "200" to "200%"
            )
        ),
        CoreOptionDef(
            key = "pce_nospritelimit",
            displayName = "No Sprite Limit",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Removes the per-scanline sprite limit to eliminate flickering"
        ),
        CoreOptionDef(
            key = "pce_ocmultiplier",
            displayName = "CPU Overclock Multiplier",
            values = listOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "20", "30", "40", "50"
            ),
            defaultValue = "1",
            description = "Multiplies the emulated CPU speed to reduce slowdown in games",
            valueLabels = mapOf(
                "1" to "1x (native)", "2" to "2x", "3" to "3x", "4" to "4x",
                "5" to "5x", "6" to "6x", "7" to "7x", "8" to "8x", "9" to "9x",
                "10" to "10x", "20" to "20x", "30" to "30x", "40" to "40x", "50" to "50x"
            )
        ),
    )
}

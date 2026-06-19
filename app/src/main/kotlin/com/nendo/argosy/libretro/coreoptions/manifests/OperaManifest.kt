package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object OperaManifest : CoreOptionManifest {
    override val coreId = "opera"
    override val options = listOf(
        CoreOptionDef(
            key = "opera_cpu_overclock",
            displayName = "CPU Overclock",
            values = listOf(
                "0.5x ( 6.25Mhz)", "0.6x ( 7.50Mhz)", "0.7x ( 8.75Mhz)",
                "0.8x (10.00Mhz)", "0.9x (11.25Mhz)", "1.0x (12.50Mhz)",
                "1.1x (13.75Mhz)", "1.2x (15.00Mhz)", "1.5x (18.75Mhz)",
                "1.6x (20.00Mhz)", "1.8x (22.50Mhz)", "2.0x (25.00Mhz)",
                "3.0x (37.50Mhz)", "4.0x (50.00Mhz)"
            ),
            defaultValue = "1.0x (12.50Mhz)",
            description = "Increases the emulated CPU speed to reduce slowdown in games"
        ),
        CoreOptionDef(
            key = "opera_region",
            displayName = "Region",
            values = listOf("ntsc", "pal1", "pal2"),
            defaultValue = "ntsc",
            description = "Sets the resolution and field rate; some EU games require a EU ROM",
            valueLabels = mapOf(
                "ntsc" to "NTSC", "pal1" to "PAL1", "pal2" to "PAL2"
            )
        ),
        CoreOptionDef(
            key = "opera_high_resolution",
            displayName = "High Resolution",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Doubles the rendering resolution from 320x240 to 640x480"
        ),
        CoreOptionDef(
            key = "opera_cd_speed",
            displayName = "CD-ROM Speed",
            values = listOf(
                "unbounded", "1x", "2x", "4x", "8x", "16x", "24x", "32x"
            ),
            defaultValue = "unbounded",
            description = "Sets the effective CD-ROM read speed; high values can expose timing issues"
        ),
        CoreOptionDef(
            key = "opera_mem_capacity",
            displayName = "Memory Capacity",
            values = listOf("21", "22", "41", "42", "81", "82", "E2", "F1"),
            defaultValue = "21",
            description = "Sets the amount of DRAM and VRAM; non-stock values are experimental"
        ),
        CoreOptionDef(
            key = "opera_vdlp_pixel_format",
            displayName = "VDLP Pixel Format",
            values = listOf("0RGB1555", "RGB565", "XRGB8888"),
            defaultValue = "RGB565",
            description = "Selects the output pixel format converted from the internal 16bpp format"
        ),
        CoreOptionDef(
            key = "opera_vdlp_bypass_clut",
            displayName = "VDLP Bypass CLUT",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Bypasses the color lookup table for faster but potentially incorrect colors"
        ),
        CoreOptionDef(
            key = "opera_madam_matrix_engine",
            displayName = "MADAM Matrix Engine",
            values = listOf("hardware", "software"),
            defaultValue = "hardware",
            description = "Selects the matrix co-processor; some games run faster in software mode"
        ),
        CoreOptionDef(
            key = "opera_swi_hle",
            displayName = "SWI HLE",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "High-level emulation of Portfolio OS functions; may improve performance"
        ),
        CoreOptionDef(
            key = "opera_dsp_threaded",
            displayName = "Threaded DSP",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Runs the audio processor on a separate thread; experimental"
        ),
        CoreOptionDef(
            key = "opera_nvram_storage",
            displayName = "NVRAM Storage",
            values = listOf("per game", "shared"),
            defaultValue = "per game",
            description = "Stores save data separately per game or in a single shared file"
        ),
        CoreOptionDef(
            key = "opera_nvram_version",
            displayName = "NVRAM Version",
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
            defaultValue = "0",
            description = "Selects the NVRAM format version"
        ),
        CoreOptionDef(
            key = "opera_active_devices",
            displayName = "Active Devices",
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8"),
            defaultValue = "1",
            description = "Sets the number of connected controller devices",
            valueLabels = mapOf(
                "0" to "None", "1" to "1 player", "2" to "2 players", "3" to "3 players",
                "4" to "4 players", "5" to "5 players", "6" to "6 players",
                "7" to "7 players", "8" to "8 players"
            )
        ),
        CoreOptionDef(
            key = "opera_hide_lightgun_crosshairs",
            displayName = "Hide Lightgun Crosshairs",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Hides lightgun crosshairs for all players"
        ),
        CoreOptionDef(
            key = "opera_kprint",
            displayName = "Debug Output",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Prints 3DO debug port output to stderr"
        )
    )
}

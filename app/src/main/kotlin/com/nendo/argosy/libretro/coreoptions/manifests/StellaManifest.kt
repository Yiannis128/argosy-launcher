package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object StellaManifest : CoreOptionManifest {
    override val coreId = "stella"
    override val options = listOf(
        CoreOptionDef(
            key = "stella_console",
            displayName = "Console Display",
            values = listOf("auto", "ntsc", "pal", "secam", "ntsc50", "pal60", "secam60"),
            defaultValue = "auto",
            description = "Sets the TV standard and refresh rate for the emulated console",
            valueLabels = mapOf(
                "auto" to "Auto", "ntsc" to "NTSC", "pal" to "PAL", "secam" to "SECAM",
                "ntsc50" to "NTSC 50 Hz", "pal60" to "PAL 60 Hz", "secam60" to "SECAM 60 Hz"
            )
        ),
        CoreOptionDef(
            key = "stella_palette",
            displayName = "Palette Colors",
            values = listOf("standard", "z26", "user", "custom"),
            defaultValue = "standard",
            description = "Selects the color palette used to render Atari 2600 graphics",
            valueLabels = mapOf(
                "standard" to "Standard", "z26" to "z26", "user" to "User-defined", "custom" to "Custom"
            )
        ),
        CoreOptionDef(
            key = "stella_filter",
            displayName = "TV Effects",
            values = listOf("disabled", "composite", "s-video", "rgb", "badly adjusted"),
            defaultValue = "disabled",
            description = "Applies a video filter that simulates different TV signal types"
        ),
        CoreOptionDef(
            key = "stella_crop_hoverscan",
            displayName = "Crop Horizontal Overscan",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Removes the left and right border areas outside the visible screen"
        ),
        CoreOptionDef(
            key = "stella_crop_voverscan",
            displayName = "Crop Vertical Overscan",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24"
            ),
            defaultValue = "0",
            description = "Sets how many scanlines to crop from the top and bottom edges"
        ),
        CoreOptionDef(
            key = "stella_ntsc_aspect",
            displayName = "NTSC Aspect %",
            values = listOf(
                "par",
                "75", "76", "77", "78", "79", "80", "81", "82", "83", "84",
                "85", "86", "87", "88", "89", "90", "91", "92", "93", "94",
                "95", "96", "97", "98", "99", "100", "101", "102", "103", "104",
                "105", "106", "107", "108", "109", "110", "111", "112", "113", "114",
                "115", "116", "117", "118", "119", "120", "121", "122", "123", "124",
                "125"
            ),
            defaultValue = "par",
            description = "Adjusts the horizontal stretch for NTSC display output",
            valueLabels = mapOf("par" to "Pixel Aspect Ratio")
        ),
        CoreOptionDef(
            key = "stella_pal_aspect",
            displayName = "PAL Aspect %",
            values = listOf(
                "par",
                "75", "76", "77", "78", "79", "80", "81", "82", "83", "84",
                "85", "86", "87", "88", "89", "90", "91", "92", "93", "94",
                "95", "96", "97", "98", "99", "100", "101", "102", "103", "104",
                "105", "106", "107", "108", "109", "110", "111", "112", "113", "114",
                "115", "116", "117", "118", "119", "120", "121", "122", "123", "124",
                "125"
            ),
            defaultValue = "par",
            description = "Adjusts the horizontal stretch for PAL display output",
            valueLabels = mapOf("par" to "Pixel Aspect Ratio")
        ),
        CoreOptionDef(
            key = "stella_stereo",
            displayName = "Stereo Sound",
            values = listOf("auto", "off", "on"),
            defaultValue = "auto",
            description = "Outputs audio in stereo by panning channels left and right"
        ),
        CoreOptionDef(
            key = "stella_phosphor",
            displayName = "Phosphor Mode",
            values = listOf("auto", "off", "on"),
            defaultValue = "auto",
            description = "Simulates CRT phosphor persistence to reduce flicker"
        ),
        CoreOptionDef(
            key = "stella_phosphor_blend",
            displayName = "Phosphor Blend %",
            values = listOf(
                "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50",
                "55", "60", "65", "70", "75", "80", "85", "90", "95", "100"
            ),
            defaultValue = "60",
            description = "Sets how strongly the previous frame blends into the current one",
            valueLabels = mapOf(
                "0" to "0%", "5" to "5%", "10" to "10%", "15" to "15%",
                "20" to "20%", "25" to "25%", "30" to "30%", "35" to "35%",
                "40" to "40%", "45" to "45%", "50" to "50%", "55" to "55%",
                "60" to "60%", "65" to "65%", "70" to "70%", "75" to "75%",
                "80" to "80%", "85" to "85%", "90" to "90%", "95" to "95%", "100" to "100%"
            )
        ),
        CoreOptionDef(
            key = "stella_paddle_mouse_sensitivity",
            displayName = "Paddle Mouse Sensitivity",
            values = listOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
                "41", "42", "43", "44", "45", "46", "47", "48", "49", "50"
            ),
            defaultValue = "20"
        ),
        CoreOptionDef(
            key = "stella_paddle_joypad_sensitivity",
            displayName = "Paddle Joypad Sensitivity",
            values = listOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            ),
            defaultValue = "3"
        ),
        CoreOptionDef(
            key = "stella_paddle_analog_sensitivity",
            displayName = "Paddle Analog Sensitivity",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
            ),
            defaultValue = "20"
        ),
        CoreOptionDef(
            key = "stella_paddle_analog_deadzone",
            displayName = "Paddle Analog Deadzone",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
            ),
            defaultValue = "15"
        ),
        CoreOptionDef(
            key = "stella_paddle_analog_absolute",
            displayName = "Paddle Analog Absolute",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Maps analog stick position directly to paddle position instead of velocity"
        ),
        CoreOptionDef(
            key = "stella_lightgun_crosshair",
            displayName = "Lightgun Crosshair",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Displays an aiming crosshair when using the light gun"
        ),
    )
}

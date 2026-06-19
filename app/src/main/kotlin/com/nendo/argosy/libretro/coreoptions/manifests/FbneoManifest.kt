package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object FbneoManifest : CoreOptionManifest {
    override val coreId = "fbneo"
    override val options = listOf(
        CoreOptionDef(
            key = "fbneo-allow-depth-32",
            displayName = "Use 32-bit Color Depth When Available",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Renders in 32-bit color for higher quality when the game supports it"
        ),
        CoreOptionDef(
            key = "fbneo-vertical-mode",
            displayName = "Vertical Mode",
            values = listOf("disabled", "enabled", "alternate", "TATE", "TATE alternate"),
            defaultValue = "disabled",
            description = "Rotates the screen for vertically oriented arcade games"
        ),
        CoreOptionDef(
            key = "fbneo-force-60hz",
            displayName = "Force 60Hz",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Forces 60Hz refresh rate regardless of the game's native rate"
        ),
        CoreOptionDef(
            key = "fbneo-resolution",
            displayName = "Resolution",
            values = listOf(
                "640x480", "800x600", "1024x768", "1080x810",
                "1280x960", "1440x1080", "1600x1200", "1920x1440",
                "2160x1620", "2880x2160"
            ),
            defaultValue = "640x480"
        ),
        CoreOptionDef(
            key = "fbneo-samplerate",
            displayName = "Samplerate",
            values = listOf("44100", "48000"),
            defaultValue = "48000",
            description = "Sets the audio output sample rate in Hz",
            valueLabels = mapOf("44100" to "44.1 kHz", "48000" to "48 kHz")
        ),
        CoreOptionDef(
            key = "fbneo-sample-interpolation",
            displayName = "Sample Interpolation",
            values = listOf("disabled", "2-point 1st order", "4-point 3rd order"),
            defaultValue = "4-point 3rd order",
            description = "Sets the quality of audio sample interpolation"
        ),
        CoreOptionDef(
            key = "fbneo-fm-interpolation",
            displayName = "FM Interpolation",
            values = listOf("disabled", "4-point 3rd order"),
            defaultValue = "4-point 3rd order",
            description = "Sets the quality of FM synthesis audio interpolation"
        ),
        CoreOptionDef(
            key = "fbneo-lowpass-filter",
            displayName = "LowPass Filter",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Applies a low-pass filter to soften harsh audio"
        ),
        CoreOptionDef(
            key = "fbneo-analog-speed",
            displayName = "Analog Speed",
            values = listOf(
                "25%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%",
                "125%", "150%", "175%", "200%", "225%", "250%", "300%", "350%", "400%"
            ),
            defaultValue = "100%",
            description = "Adjusts the movement speed of analog stick input"
        ),
        CoreOptionDef(
            key = "fbneo-socd",
            displayName = "SOCD Setting",
            values = listOf(
                "disabled", "Simultaneous Neutral",
                "Last Input Priority (4 Way)", "Last Input Priority (8 Way)",
                "First Input Priority", "Up Priority", "Down Priority"
            ),
            defaultValue = "Last Input Priority (8 Way)",
            description = "Controls how simultaneous opposing cardinal directions are resolved"
        ),
        CoreOptionDef(
            key = "fbneo-lightgun-crosshair-emulation",
            displayName = "Crosshair Emulation",
            values = listOf("hide with lightgun device", "always hide", "always show"),
            defaultValue = "hide with lightgun device",
            description = "Controls when the aiming crosshair is visible on screen"
        ),
        CoreOptionDef(
            key = "fbneo-diagnostic-input",
            displayName = "Diagnostic Input",
            values = listOf(
                "None", "Hold Start", "Start + A + B", "Hold Start + A + B",
                "Start + L + R", "Hold Start + L + R", "Hold Select",
                "Select + A + B", "Hold Select + A + B",
                "Select + L + R", "Hold Select + L + R"
            ),
            defaultValue = "Hold Start",
            description = "Sets the button combination that opens the arcade diagnostic menu"
        ),
        CoreOptionDef(
            key = "fbneo-frameskip-type",
            displayName = "Frameskip",
            values = listOf("disabled", "Fixed", "Auto", "Manual"),
            defaultValue = "disabled",
            description = "Selects the frameskip method used to improve performance"
        ),
        CoreOptionDef(
            key = "fbneo-frameskip-manual-threshold",
            displayName = "Frameskip Threshold (%)",
            values = listOf(
                "15", "18", "21", "24", "27", "30", "33",
                "36", "39", "42", "45", "48", "51", "54", "57", "60"
            ),
            defaultValue = "33",
            description = "Sets the audio buffer occupancy below which frames will be skipped",
            valueLabels = mapOf(
                "15" to "15%", "18" to "18%", "21" to "21%", "24" to "24%",
                "27" to "27%", "30" to "30%", "33" to "33%", "36" to "36%",
                "39" to "39%", "42" to "42%", "45" to "45%", "48" to "48%",
                "51" to "51%", "54" to "54%", "57" to "57%", "60" to "60%"
            )
        ),
        CoreOptionDef(
            key = "fbneo-fixed-frameskip",
            displayName = "Fixed Frameskip",
            values = listOf("0", "1", "2", "3", "4", "5"),
            defaultValue = "0",
            description = "Sets how many frames to skip between each rendered frame",
            valueLabels = mapOf(
                "0" to "Off", "1" to "1 frame", "2" to "2 frames",
                "3" to "3 frames", "4" to "4 frames", "5" to "5 frames"
            )
        ),
        CoreOptionDef(
            key = "fbneo-neogeo-mode",
            displayName = "Neo-Geo Mode",
            values = listOf(
                "DIPSWITCH", "MVS_EUR", "MVS_USA", "MVS_JAP",
                "AES_EUR", "AES_JAP", "UNIBIOS"
            ),
            defaultValue = "DIPSWITCH",
            description = "Selects the Neo-Geo system BIOS and region to emulate",
            valueLabels = mapOf(
                "DIPSWITCH" to "Use DIP Switches", "MVS_EUR" to "MVS Europe",
                "MVS_USA" to "MVS USA", "MVS_JAP" to "MVS Japan",
                "AES_EUR" to "AES Europe", "AES_JAP" to "AES Japan",
                "UNIBIOS" to "UniBIOS"
            )
        ),
        CoreOptionDef(
            key = "fbneo-memcard-mode",
            displayName = "Memory Card Mode",
            values = listOf("disabled", "shared", "per-game"),
            defaultValue = "disabled",
            description = "Controls whether Neo-Geo memory card saves are shared or per-game"
        ),
        CoreOptionDef(
            key = "fbneo-cpu-speed-adjust",
            displayName = "CPU Clock",
            values = listOf(
                "25%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%",
                "125%", "150%", "175%", "200%", "225%", "250%", "300%", "350%", "400%"
            ),
            defaultValue = "100%",
            description = "Overclock or underclock the emulated CPU; raise to fix native slowdowns, lower to help performance on weak devices"
        ),
        CoreOptionDef(
            key = "fbneo-allow-patched-romsets",
            displayName = "Allow Patched Romsets",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Allows loading ROM sets that have been modified or patched"
        ),
        CoreOptionDef(
            key = "fbneo-hiscores",
            displayName = "Hiscores",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Enables saving and loading high scores for supported games"
        ),
    )
}

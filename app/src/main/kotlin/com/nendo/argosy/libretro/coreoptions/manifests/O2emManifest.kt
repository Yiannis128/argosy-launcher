package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object O2emManifest : CoreOptionManifest {
    override val coreId = "o2em"
    override val options = listOf(
        CoreOptionDef(
            key = "o2em_bios",
            displayName = "Emulated Hardware",
            values = listOf("o2rom.bin", "c52.bin", "g7400.bin", "jopac.bin"),
            defaultValue = "o2rom.bin",
            description = "Selects the console hardware variant and BIOS to emulate",
            valueLabels = mapOf(
                "o2rom.bin" to "Odyssey 2 (NTSC)",
                "c52.bin" to "Videopac G7000 (European)",
                "g7400.bin" to "Videopac+ G7400 (European)",
                "jopac.bin" to "Videopac+ G7400 (French)"
            )
        ),
        CoreOptionDef(
            key = "o2em_region",
            displayName = "Console Region",
            values = listOf("auto", "NTSC", "PAL"),
            defaultValue = "auto",
            valueLabels = mapOf("auto" to "Auto")
        ),
        CoreOptionDef(
            key = "o2em_swap_gamepads",
            displayName = "Swap Gamepads",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Swaps controller port 1 and port 2 inputs"
        ),
        CoreOptionDef(
            key = "o2em_vkbd_transparency",
            displayName = "Virtual Keyboard Transparency",
            values = listOf("0", "25", "50", "75"),
            defaultValue = "0",
            description = "Sets how transparent the on-screen virtual keyboard overlay appears",
            valueLabels = mapOf("0" to "0%", "25" to "25%", "50" to "50%", "75" to "75%")
        ),
        CoreOptionDef(
            key = "o2em_crop_overscan",
            displayName = "Crop Overscan",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Removes the border area around the visible screen"
        ),
        CoreOptionDef(
            key = "o2em_mix_frames",
            displayName = "Interframe Blending",
            values = listOf("disabled", "mix", "ghost_65", "ghost_75", "ghost_85", "ghost_95"),
            defaultValue = "disabled",
            description = "Blends consecutive frames to simulate CRT persistence or reduce flicker",
            valueLabels = mapOf(
                "mix" to "Simple",
                "ghost_65" to "Ghosting (65%)",
                "ghost_75" to "Ghosting (75%)",
                "ghost_85" to "Ghosting (85%)",
                "ghost_95" to "Ghosting (95%)"
            )
        ),
        CoreOptionDef(
            key = "o2em_audio_volume",
            displayName = "Audio Volume",
            values = listOf(
                "0", "10", "20", "30", "40", "50",
                "60", "70", "80", "90", "100"
            ),
            defaultValue = "50",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%", "100" to "100%"
            )
        ),
        CoreOptionDef(
            key = "o2em_voice_volume",
            displayName = "Voice Volume",
            values = listOf(
                "0", "10", "20", "30", "40", "50", "60",
                "70", "80", "90", "100"
            ),
            defaultValue = "70",
            description = "Sets the volume level for The Voice speech synthesis module",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%", "100" to "100%"
            )
        ),
        CoreOptionDef(
            key = "o2em_low_pass_filter",
            displayName = "Audio Filter",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Applies a low-pass filter to soften harsh audio"
        ),
        CoreOptionDef(
            key = "o2em_low_pass_range",
            displayName = "Audio Filter Level",
            values = listOf(
                "5", "20", "40", "60", "80", "95"
            ),
            defaultValue = "60",
            description = "Sets the cutoff strength of the low-pass audio filter",
            valueLabels = mapOf(
                "5" to "5%", "20" to "20%", "40" to "40%",
                "60" to "60%", "80" to "80%", "95" to "95%"
            )
        ),
    )
}

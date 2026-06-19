package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object FceummManifest : CoreOptionManifest {
    override val coreId = "fceumm"
    override val options = listOf(
        CoreOptionDef(
            key = "fceumm_region",
            displayName = "Region",
            values = listOf("Auto", "NTSC", "PAL", "Dendy"),
            defaultValue = "Auto"
        ),
        CoreOptionDef(
            key = "fceumm_aspect",
            displayName = "Preferred Aspect Ratio",
            values = listOf("8:7 PAR", "4:3", "PP"),
            defaultValue = "8:7 PAR",
            description = "Sets the display aspect ratio for rendered output",
            valueLabels = mapOf("PP" to "Pixel Perfect")
        ),
        CoreOptionDef(
            key = "fceumm_game_genie",
            displayName = "Game Genie Add-On",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables emulation of the Game Genie cheat cartridge"
        ),
        CoreOptionDef(
            key = "fceumm_palette",
            displayName = "Color Palette",
            values = listOf(
                "default", "asqrealc", "restored-wii-vc", "wii-vc", "rgb",
                "yuv-v3", "unsaturated-final", "sony-cxa2025as-us", "pal",
                "bmf-final2", "bmf-final3", "smooth-fbx", "composite-direct-fbx",
                "pvm-style-d93-fbx", "ntsc-hardware-fbx", "nes-classic-fbx-fs",
                "nescap", "wavebeam", "digital-prime-fbx", "magnum-fbx",
                "smooth-v2-fbx", "nes-classic-fbx", "royaltea", "mugicha",
                "raw", "custom"
            ),
            defaultValue = "default",
            description = "Selects the color palette used to render NES graphics"
        ),
        CoreOptionDef(
            key = "fceumm_up_down_allowed",
            displayName = "Allow Opposing Directions",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Allows pressing left+right or up+down at the same time"
        ),
        CoreOptionDef(
            key = "fceumm_overscan_h_left",
            displayName = "Crop Overscan (Left)",
            values = listOf("0", "4", "8", "12", "16"),
            defaultValue = "0",
            description = "Trims pixels from the left border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "fceumm_overscan_h_right",
            displayName = "Crop Overscan (Right)",
            values = listOf("0", "4", "8", "12", "16"),
            defaultValue = "0",
            description = "Trims pixels from the right border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "fceumm_overscan_v_top",
            displayName = "Crop Overscan (Top)",
            values = listOf("0", "4", "8", "12", "16", "20", "24"),
            defaultValue = "8",
            description = "Trims pixels from the top border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "fceumm_overscan_v_bottom",
            displayName = "Crop Overscan (Bottom)",
            values = listOf("0", "4", "8", "12", "16", "20", "24"),
            defaultValue = "8",
            description = "Trims pixels from the bottom border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "fceumm_nospritelimit",
            displayName = "No Sprite Limit",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Removes the 8-per-scanline hardware sprite limit to reduce flicker"
        ),
        CoreOptionDef(
            key = "fceumm_sndvolume",
            displayName = "Sound Volume",
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            defaultValue = "7",
            valueLabels = mapOf(
                "0" to "Mute", "1" to "10%", "2" to "20%", "3" to "30%",
                "4" to "40%", "5" to "50%", "6" to "60%", "7" to "70%",
                "8" to "80%", "9" to "90%", "10" to "100%"
            )
        ),
        CoreOptionDef(
            key = "fceumm_sndrate_hint",
            displayName = "Sound Output Rate (Hz)",
            values = listOf("Auto", "32KHz", "44KHz", "48KHz", "96KHz"),
            defaultValue = "Auto",
            description = "Sets the audio sample rate, higher values cost more CPU"
        ),
        CoreOptionDef(
            key = "fceumm_sndquality",
            displayName = "Sound Quality",
            values = listOf("Low", "High", "Very High"),
            defaultValue = "Low",
            description = "Sets the audio resampling quality level, higher uses more CPU"
        ),
        CoreOptionDef(
            key = "fceumm_sndlowpass",
            displayName = "Audio DPCM Low Pass Filter",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Applies a low-pass filter to soften harsh audio"
        ),
        CoreOptionDef(
            key = "fceumm_removetrianglenoise",
            displayName = "Reduce Triangle Channel Noise",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reduces audible noise from the triangle wave channel"
        ),
        CoreOptionDef(
            key = "fceumm_reducedmcpopping",
            displayName = "Reduce DMC Popping",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reduces popping artifacts from the DMC sample channel"
        ),
        CoreOptionDef(
            key = "fceumm_sndstereodelay",
            displayName = "Stereo Sound Effect Delay",
            values = listOf(
                "disabled", "05_ms_delay", "10_ms_delay", "15_ms_delay",
                "20_ms_delay", "25_ms_delay", "30_ms_delay", "32_ms_delay"
            ),
            defaultValue = "disabled",
            description = "Adds a delay between left and right channels for a stereo effect",
            valueLabels = mapOf(
                "05_ms_delay" to "5 ms", "10_ms_delay" to "10 ms",
                "15_ms_delay" to "15 ms", "20_ms_delay" to "20 ms",
                "25_ms_delay" to "25 ms", "30_ms_delay" to "30 ms",
                "32_ms_delay" to "32 ms"
            )
        ),
        CoreOptionDef(
            key = "fceumm_swapduty",
            displayName = "Swap Duty Cycles",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Swaps pulse wave duty cycles to fix audio in some Famiclone games"
        ),
        CoreOptionDef(
            key = "fceumm_apu_1",
            displayName = "Audio Channel 1 (Square 1)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Toggles the first pulse wave audio channel"
        ),
        CoreOptionDef(
            key = "fceumm_apu_2",
            displayName = "Audio Channel 2 (Square 2)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Toggles the second pulse wave audio channel"
        ),
        CoreOptionDef(
            key = "fceumm_apu_3",
            displayName = "Audio Channel 3 (Triangle)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Toggles the triangle wave audio channel"
        ),
        CoreOptionDef(
            key = "fceumm_apu_4",
            displayName = "Audio Channel 4 (Noise)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Toggles the noise audio channel"
        ),
        CoreOptionDef(
            key = "fceumm_apu_5",
            displayName = "Audio Channel 5 (PCM)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Toggles the DPCM sample audio channel"
        ),
        CoreOptionDef(
            key = "fceumm_apu_fds",
            displayName = "FDS Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the FDS expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_apu_s5b",
            displayName = "Sunsoft 5B Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Sunsoft 5B expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_apu_n163",
            displayName = "Namco 163 Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Namco 163 expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_apu_vrc6",
            displayName = "Konami VRC6 Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Konami VRC6 expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_apu_vrc7",
            displayName = "Konami VRC7 Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Konami VRC7 expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_apu_mmc5",
            displayName = "MMC5 Sound Volume",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the MMC5 expansion audio"
        ),
        CoreOptionDef(
            key = "fceumm_turbo_enable",
            displayName = "Turbo Enable",
            values = listOf("None", "Player 1", "Player 2", "Both"),
            defaultValue = "None",
            description = "Enables turbo (rapid-fire) button support for selected players"
        ),
        CoreOptionDef(
            key = "fceumm_turbo_delay",
            displayName = "Turbo Delay (in frames)",
            values = listOf("1", "2", "3", "5", "10", "15", "30", "60"),
            defaultValue = "3",
            description = "Sets how many frames between each turbo button press"
        ),
        CoreOptionDef(
            key = "fceumm_zapper_mode",
            displayName = "Zapper Mode",
            values = listOf("clightgun", "stlightgun", "touchscreen", "mouse"),
            defaultValue = "clightgun",
            description = "Selects the input device used to emulate the NES Zapper light gun",
            valueLabels = mapOf(
                "clightgun" to "Crosshair light gun",
                "stlightgun" to "Sequential Targets light gun",
                "touchscreen" to "Touchscreen",
                "mouse" to "Mouse"
            )
        ),
        CoreOptionDef(
            key = "fceumm_show_crosshair",
            displayName = "Show Crosshair",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Displays an aiming crosshair when using the Zapper"
        ),
        CoreOptionDef(
            key = "fceumm_zapper_tolerance",
            displayName = "Zapper Tolerance",
            values = listOf("0", "2", "4", "6", "8", "10", "12", "14", "16", "18", "20"),
            defaultValue = "6",
            description = "Sets how many pixels from the target still count as on target"
        ),
        CoreOptionDef(
            key = "fceumm_zapper_trigger",
            displayName = "Invert Zapper Trigger Signal",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Inverts trigger logic, disabling resembles original hardware"
        ),
        CoreOptionDef(
            key = "fceumm_zapper_sensor",
            displayName = "Invert Zapper Sensor Signal",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Inverts sensor logic for Sequential Targets light gun mode"
        ),
        CoreOptionDef(
            key = "fceumm_arkanoid_mode",
            displayName = "Arkanoid Mode",
            values = listOf("abs_mouse", "mouse", "stelladaptor", "touchscreen"),
            defaultValue = "mouse",
            description = "Selects the input device used for Arkanoid paddle games",
            valueLabels = mapOf(
                "abs_mouse" to "Absolute mouse",
                "mouse" to "Mouse",
                "stelladaptor" to "Stelladaptor",
                "touchscreen" to "Touchscreen"
            )
        ),
        CoreOptionDef(
            key = "fceumm_mouse_sensitivity",
            displayName = "Mouse Sensitivity",
            values = listOf("20", "40", "60", "80", "100", "120", "140", "160", "180", "200"),
            defaultValue = "100",
            description = "Sets mouse sensitivity in percent"
        ),
        CoreOptionDef(
            key = "fceumm_overclocking",
            displayName = "Overclocking",
            values = listOf("disabled", "2x-Postrender", "2x-VBlank"),
            defaultValue = "disabled",
            description = "Adds extra CPU cycles during blanking periods to reduce slowdown"
        ),
        CoreOptionDef(
            key = "fceumm_ramstate",
            displayName = "RAM Power Up State",
            values = listOf("fill \$ff", "fill \$00", "random"),
            defaultValue = "fill \$ff",
            description = "Sets the initial value of RAM on startup, which some games depend on",
            valueLabels = mapOf("fill \$ff" to "All 1s (0xFF)", "fill \$00" to "All 0s (0x00)", "random" to "Random")
        ),
        CoreOptionDef(
            key = "fceumm_ntsc_filter",
            displayName = "NTSC Filter",
            values = listOf("disabled", "composite", "svideo", "rgb", "monochrome"),
            defaultValue = "disabled",
            description = "Applies a video filter that simulates different NTSC signal types"
        ),
        CoreOptionDef(
            key = "fceumm_show_adv_system_options",
            displayName = "Show Advanced System Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reveals additional system configuration options in the core menu"
        ),
        CoreOptionDef(
            key = "fceumm_show_adv_sound_options",
            displayName = "Show Advanced Sound Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reveals per-channel sound volume controls in the core menu"
        )
    )
}

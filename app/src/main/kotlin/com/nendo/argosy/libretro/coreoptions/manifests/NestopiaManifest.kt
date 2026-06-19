package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object NestopiaManifest : CoreOptionManifest {
    override val coreId = "nestopia"
    override val options = listOf(
        CoreOptionDef(
            key = "nestopia_blargg_ntsc_filter",
            displayName = "Blargg NTSC Filter",
            values = listOf("disabled", "composite", "svideo", "rgb", "monochrome"),
            defaultValue = "disabled",
            description = "Applies a video filter that simulates different NTSC signal types"
        ),
        CoreOptionDef(
            key = "nestopia_palette",
            displayName = "Palette",
            values = listOf(
                "cxa2025as", "cxa2025as_jp", "royaltea", "consumer", "canonical",
                "alternative", "rgb", "pal", "digital-prime-fbx", "magnum-fbx",
                "smoothv2-fbx", "composite-direct-fbx", "pvm-style-d93-fbx",
                "ntsc-hardware-fbx", "nes-classic-fbx-fs", "restored-wii-vc",
                "wii-vc", "raw", "custom"
            ),
            defaultValue = "cxa2025as",
            description = "Selects the color palette used to render NES graphics"
        ),
        CoreOptionDef(
            key = "nestopia_nospritelimit",
            displayName = "Remove Sprite Limit",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Removes the 8-per-scanline hardware sprite limit to reduce flicker"
        ),
        CoreOptionDef(
            key = "nestopia_overclock",
            displayName = "CPU Speed (Overclock)",
            values = listOf("1x", "2x"),
            defaultValue = "1x",
            description = "Doubles the emulated CPU speed to reduce slowdown in games"
        ),
        CoreOptionDef(
            key = "nestopia_select_adapter",
            displayName = "4 Player Adapter",
            values = listOf("auto", "ntsc", "famicom"),
            defaultValue = "auto",
            description = "Selects which multitap adapter type to use for 4-player games"
        ),
        CoreOptionDef(
            key = "nestopia_fds_auto_insert",
            displayName = "FDS Auto Insert",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Automatically inserts disk side A when loading FDS games"
        ),
        CoreOptionDef(
            key = "nestopia_overscan_v_top",
            displayName = "Mask Overscan (Top)",
            values = listOf("0", "4", "8", "12", "16", "20", "24"),
            defaultValue = "8",
            description = "Hides pixels from the top border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "nestopia_overscan_v_bottom",
            displayName = "Mask Overscan (Bottom)",
            values = listOf("0", "4", "8", "12", "16", "20", "24"),
            defaultValue = "8",
            description = "Hides pixels from the bottom border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "nestopia_overscan_h_left",
            displayName = "Mask Overscan (Left)",
            values = listOf("0", "4", "8", "12", "16"),
            defaultValue = "0",
            description = "Hides pixels from the left border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "nestopia_overscan_h_right",
            displayName = "Mask Overscan (Right)",
            values = listOf("0", "4", "8", "12", "16"),
            defaultValue = "0",
            description = "Hides pixels from the right border that may contain artifacts"
        ),
        CoreOptionDef(
            key = "nestopia_aspect",
            displayName = "Preferred Aspect Ratio",
            values = listOf("auto", "ntsc", "pal", "4:3", "uncorrected"),
            defaultValue = "auto"
        ),
        CoreOptionDef(
            key = "nestopia_genie_distortion",
            displayName = "Game Genie Sound Distortion",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Simulates the audio distortion caused by the Game Genie hardware"
        ),
        CoreOptionDef(
            key = "nestopia_favored_system",
            displayName = "System Region",
            values = listOf("auto", "ntsc", "pal", "famicom", "dendy"),
            defaultValue = "auto"
        ),
        CoreOptionDef(
            key = "nestopia_ram_power_state",
            displayName = "RAM Power-on State",
            values = listOf("0x00", "0xFF", "random"),
            defaultValue = "0x00",
            description = "Sets the initial value of RAM on startup, which some games depend on",
            valueLabels = mapOf("0x00" to "All 0s", "0xFF" to "All 1s", "random" to "Random")
        ),
        CoreOptionDef(
            key = "nestopia_turbo_pulse",
            displayName = "Turbo Pulse Speed",
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9"),
            defaultValue = "2",
            description = "Sets how many frames between each turbo button press",
            valueLabels = mapOf(
                "1" to "1 frame", "2" to "2 frames", "3" to "3 frames",
                "4" to "4 frames", "5" to "5 frames", "6" to "6 frames",
                "7" to "7 frames", "8" to "8 frames", "9" to "9 frames"
            )
        ),
        CoreOptionDef(
            key = "nestopia_fds_savefile_format",
            displayName = "FDS Save File Format",
            values = listOf("sav_ups", "ups", "ips"),
            defaultValue = "sav_ups",
            description = "Selects the format used when saving FDS disk modifications"
        ),
        CoreOptionDef(
            key = "nestopia_show_advanced_av_settings",
            displayName = "Show Advanced A/V Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reveals per-channel volume and audio output options in the core menu"
        ),
        CoreOptionDef(
            key = "nestopia_audio_type",
            displayName = "Audio Output",
            values = listOf("mono", "stereo"),
            defaultValue = "stereo",
            description = "Selects mono or stereo audio output"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_sq1",
            displayName = "Square 1 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the first square wave channel"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_sq2",
            displayName = "Square 2 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the second square wave channel"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_tri",
            displayName = "Triangle Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the triangle wave channel"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_noise",
            displayName = "Noise Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the noise channel"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_dpcm",
            displayName = "DPCM Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the DPCM sample channel"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_fds",
            displayName = "FDS Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the FDS expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_mmc5",
            displayName = "MMC5 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the MMC5 expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_vrc6",
            displayName = "VRC6 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Konami VRC6 expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_vrc7",
            displayName = "VRC7 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Konami VRC7 expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_n163",
            displayName = "Namco 163 Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Namco 163 expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_audio_vol_s5b",
            displayName = "Sunsoft 5B Volume",
            values = listOf("0", "20", "40", "60", "80", "90", "100"),
            defaultValue = "100",
            description = "Sets the volume of the Sunsoft 5B expansion audio"
        ),
        CoreOptionDef(
            key = "nestopia_button_shift",
            displayName = "Shift A/B and X/Y Buttons",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Remaps face buttons so B/A map to the lower row"
        ),
        CoreOptionDef(
            key = "nestopia_arkanoid_device",
            displayName = "Arkanoid Input Device",
            values = listOf("mouse", "pointer"),
            defaultValue = "mouse",
            description = "Selects the input device used for Arkanoid paddle games"
        ),
        CoreOptionDef(
            key = "nestopia_arkanoid_paddle_range",
            displayName = "Arkanoid Paddle Range",
            values = listOf("combined", "arkanoidI", "arkanoidII"),
            defaultValue = "combined",
            description = "Selects the paddle movement range emulated for Arkanoid"
        ),
        CoreOptionDef(
            key = "nestopia_zapper_device",
            displayName = "Zapper Input Device",
            values = listOf("lightgun", "mouse", "pointer"),
            defaultValue = "lightgun",
            description = "Selects the input device used to emulate the NES Zapper light gun"
        ),
        CoreOptionDef(
            key = "nestopia_show_crosshair",
            displayName = "Show Crosshair",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Displays an aiming crosshair when using the Zapper"
        )
    )
}

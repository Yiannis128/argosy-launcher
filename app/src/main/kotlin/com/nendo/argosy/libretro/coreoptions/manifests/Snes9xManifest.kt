package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object Snes9xManifest : CoreOptionManifest {
    override val coreId = "snes9x"
    override val options = listOf(
        CoreOptionDef(
            key = "snes9x_region",
            displayName = "Console Region",
            values = listOf("auto", "ntsc", "pal"),
            defaultValue = "auto"
        ),
        CoreOptionDef(
            key = "snes9x_aspect",
            displayName = "Preferred Aspect Ratio",
            values = listOf("4:3", "4:3 scaled", "uncorrected", "auto", "ntsc", "pal"),
            defaultValue = "4:3",
            valueLabels = mapOf("4:3 scaled" to "4:3 (Preserved)")
        ),
        CoreOptionDef(
            key = "snes9x_overscan",
            displayName = "Crop Overscan",
            values = listOf("enabled", "12_pixels", "16_pixels", "auto", "disabled"),
            defaultValue = "enabled",
            description = "Removes the empty border lines around the edges of the screen",
            valueLabels = mapOf(
                "enabled" to "~8 Pixels", "12_pixels" to "12 Pixels",
                "16_pixels" to "16 Pixels", "auto" to "Auto (~8 Pixels)"
            )
        ),
        CoreOptionDef(
            key = "snes9x_up_down_allowed",
            displayName = "Allow Opposing Directions",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Allows pressing left+right or up+down simultaneously"
        ),
        CoreOptionDef(
            key = "snes9x_overclock_superfx",
            displayName = "SuperFX Frequency",
            values = listOf(
                "50%", "60%", "70%", "80%", "90%", "100%",
                "150%", "200%", "250%", "300%", "350%", "400%",
                "450%", "500%"
            ),
            defaultValue = "100%",
            description = "Adjusts the SuperFX chip clock speed for games like Star Fox"
        ),
        CoreOptionDef(
            key = "snes9x_overclock_cycles",
            displayName = "Reduce Slowdown (Hack, Unsafe)",
            values = listOf("disabled", "light", "compatible", "max"),
            defaultValue = "disabled",
            description = "Overclocks the CPU to reduce slowdown in demanding games"
        ),
        CoreOptionDef(
            key = "snes9x_reduce_sprite_flicker",
            displayName = "Reduce Flickering (Hack, Unsafe)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Increases the per-scanline sprite limit to reduce flickering"
        ),
        CoreOptionDef(
            key = "snes9x_randomize_memory",
            displayName = "Randomize Memory (Unsafe)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Fills memory with random data at startup instead of zeroes"
        ),
        CoreOptionDef(
            key = "snes9x_hires_blend",
            displayName = "Hires Blending",
            values = listOf("disabled", "merge", "blur"),
            defaultValue = "disabled",
            description = "Controls how hi-res mode pixels are blended for standard displays"
        ),
        CoreOptionDef(
            key = "snes9x_audio_interpolation",
            displayName = "Audio Interpolation",
            values = listOf("gaussian", "cubic", "sinc", "none", "linear"),
            defaultValue = "gaussian",
            description = "Selects the audio sample interpolation method"
        ),
        CoreOptionDef(
            key = "snes9x_blargg",
            displayName = "Blargg NTSC Filter",
            values = listOf("disabled", "monochrome", "rf", "composite", "s-video", "rgb"),
            defaultValue = "disabled",
            description = "Applies an NTSC signal filter to simulate different cable types"
        ),
        CoreOptionDef(
            key = "snes9x_layer_1",
            displayName = "Show Layer 1",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "snes9x_layer_2",
            displayName = "Show Layer 2",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "snes9x_layer_3",
            displayName = "Show Layer 3",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "snes9x_layer_4",
            displayName = "Show Layer 4",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "snes9x_layer_5",
            displayName = "Show Sprite Layer",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "snes9x_gfx_clip",
            displayName = "Enable Graphic Clip Windows",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Enables hardware clipping windows used for special visual effects"
        ),
        CoreOptionDef(
            key = "snes9x_gfx_transp",
            displayName = "Enable Transparency Effects",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Enables transparency and blending effects used by many games"
        ),
        CoreOptionDef(
            key = "snes9x_gfx_hires",
            displayName = "Enable Hires Mode",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Enables the SNES 512-pixel hi-res mode used by some games"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_1",
            displayName = "Volume % for Sound Channel 1",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_2",
            displayName = "Volume % for Sound Channel 2",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_3",
            displayName = "Volume % for Sound Channel 3",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_4",
            displayName = "Volume % for Sound Channel 4",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_5",
            displayName = "Volume % for Sound Channel 5",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_6",
            displayName = "Volume % for Sound Channel 6",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_7",
            displayName = "Volume % for Sound Channel 7",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_sndchan_volume_8",
            displayName = "Volume % for Sound Channel 8",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "snes9x_lightgun_mode",
            displayName = "Light Gun Mode",
            values = listOf("Lightgun", "Touchscreen"),
            defaultValue = "Lightgun",
            description = "Selects mouse-controlled light gun or touchscreen input",
            valueLabels = mapOf("Lightgun" to "Light Gun")
        ),
        CoreOptionDef(
            key = "snes9x_superscope_reverse_buttons",
            displayName = "Super Scope Reverse Trigger Buttons",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Swaps the Super Scope fire and cursor buttons"
        ),
        CoreOptionDef(
            key = "snes9x_superscope_crosshair",
            displayName = "Super Scope Crosshair",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16"
            ),
            defaultValue = "2",
            description = "Sets the size of the Super Scope crosshair cursor",
            valueLabels = mapOf("0" to "Off")
        ),
        CoreOptionDef(
            key = "snes9x_superscope_color",
            displayName = "Super Scope Color",
            values = listOf(
                "White", "White (blend)", "Red", "Red (blend)",
                "Orange", "Orange (blend)", "Yellow", "Yellow (blend)",
                "Green", "Green (blend)", "Cyan", "Cyan (blend)",
                "Sky", "Sky (blend)", "Blue", "Blue (blend)",
                "Violet", "Violet (blend)", "Pink", "Pink (blend)",
                "Purple", "Purple (blend)", "Black", "Black (blend)",
                "25% Grey", "25% Grey (blend)", "50% Grey", "50% Grey (blend)",
                "75% Grey", "75% Grey (blend)"
            ),
            defaultValue = "White"
        ),
        CoreOptionDef(
            key = "snes9x_justifier1_crosshair",
            displayName = "Justifier 1 Crosshair",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16"
            ),
            defaultValue = "4",
            description = "Sets the size of the Justifier 1 crosshair cursor",
            valueLabels = mapOf("0" to "Off")
        ),
        CoreOptionDef(
            key = "snes9x_justifier1_color",
            displayName = "Justifier 1 Color",
            values = listOf(
                "Blue", "Blue (blend)", "Violet", "Violet (blend)",
                "Pink", "Pink (blend)", "Purple", "Purple (blend)",
                "Black", "Black (blend)", "25% Grey", "25% Grey (blend)",
                "50% Grey", "50% Grey (blend)", "75% Grey", "75% Grey (blend)",
                "White", "White (blend)", "Red", "Red (blend)",
                "Orange", "Orange (blend)", "Yellow", "Yellow (blend)",
                "Green", "Green (blend)", "Cyan", "Cyan (blend)",
                "Sky", "Sky (blend)"
            ),
            defaultValue = "Blue"
        ),
        CoreOptionDef(
            key = "snes9x_justifier2_crosshair",
            displayName = "Justifier 2 Crosshair",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16"
            ),
            defaultValue = "4",
            description = "Sets the size of the Justifier 2 crosshair cursor",
            valueLabels = mapOf("0" to "Off")
        ),
        CoreOptionDef(
            key = "snes9x_justifier2_color",
            displayName = "Justifier 2 Color",
            values = listOf(
                "Pink", "Pink (blend)", "Purple", "Purple (blend)",
                "Black", "Black (blend)", "25% Grey", "25% Grey (blend)",
                "50% Grey", "50% Grey (blend)", "75% Grey", "75% Grey (blend)",
                "White", "White (blend)", "Red", "Red (blend)",
                "Orange", "Orange (blend)", "Yellow", "Yellow (blend)",
                "Green", "Green (blend)", "Cyan", "Cyan (blend)",
                "Sky", "Sky (blend)", "Blue", "Blue (blend)",
                "Violet", "Violet (blend)"
            ),
            defaultValue = "Pink"
        ),
        CoreOptionDef(
            key = "snes9x_rifle_crosshair",
            displayName = "M.A.C.S. Rifle Crosshair",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16"
            ),
            defaultValue = "2",
            description = "Sets the size of the M.A.C.S. rifle crosshair cursor",
            valueLabels = mapOf("0" to "Off")
        ),
        CoreOptionDef(
            key = "snes9x_rifle_color",
            displayName = "M.A.C.S. Rifle Color",
            values = listOf(
                "White", "White (blend)", "Red", "Red (blend)",
                "Orange", "Orange (blend)", "Yellow", "Yellow (blend)",
                "Green", "Green (blend)", "Cyan", "Cyan (blend)",
                "Sky", "Sky (blend)", "Blue", "Blue (blend)",
                "Violet", "Violet (blend)", "Pink", "Pink (blend)",
                "Purple", "Purple (blend)", "Black", "Black (blend)",
                "25% Grey", "25% Grey (blend)", "50% Grey", "50% Grey (blend)",
                "75% Grey", "75% Grey (blend)"
            ),
            defaultValue = "White"
        ),
        CoreOptionDef(
            key = "snes9x_block_invalid_vram_access",
            displayName = "Block Invalid VRAM Access",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Prevents games from writing to VRAM during disallowed periods"
        ),
        CoreOptionDef(
            key = "snes9x_echo_buffer_hack",
            displayName = "Echo Buffer Hack (Unsafe)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Fixes audio in certain ROMs by preventing echo buffer overwrites"
        )
    )
}

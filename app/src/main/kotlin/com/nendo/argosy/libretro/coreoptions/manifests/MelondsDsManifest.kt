package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

private val LAYOUT_VALUES = listOf(
    "top-bottom", "bottom-top", "left-right", "right-left",
    "top", "bottom", "hybrid-top", "hybrid-bottom",
    "flipped-hybrid-top", "flipped-hybrid-bottom",
    "rotate-left", "rotate-right", "rotate-180"
)

private val LAYOUT_LABELS = mapOf(
    "top-bottom" to "Top/Bottom",
    "bottom-top" to "Bottom/Top",
    "left-right" to "Left/Right",
    "right-left" to "Right/Left",
    "top" to "Top Only",
    "bottom" to "Bottom Only",
    "hybrid-top" to "Hybrid (Focus Top)",
    "hybrid-bottom" to "Hybrid (Focus Bottom)",
    "flipped-hybrid-top" to "Flipped Hybrid (Focus Top)",
    "flipped-hybrid-bottom" to "Flipped Hybrid (Focus Bottom)",
    "rotate-left" to "Rotated Left",
    "rotate-right" to "Rotated Right",
    "rotate-180" to "Upside Down"
)

private fun layoutSlot(index: Int, default: String) = CoreOptionDef(
    key = "melonds_screen_layout$index",
    displayName = "Screen Layout #$index",
    values = LAYOUT_VALUES,
    defaultValue = default,
    description = "Layout used in slot #$index; the Next Screen Layout hotkey (R2) cycles through the active slots",
    valueLabels = LAYOUT_LABELS
)

object MelondsDsManifest : CoreOptionManifest {
    override val coreId = "melondsds"
    override val options = listOf(
        CoreOptionDef(
            key = "melonds_console_mode",
            displayName = "Console Mode",
            values = listOf("ds", "dsi"),
            defaultValue = "ds",
            description = "Emulate DS or DSi hardware",
            valueLabels = mapOf("ds" to "DS", "dsi" to "DSi")
        ),
        CoreOptionDef(
            key = "melonds_boot_mode",
            displayName = "Boot Mode",
            values = listOf("direct", "native"),
            defaultValue = "direct",
            description = "Boot the game directly, or boot into the DS firmware menu first",
            valueLabels = mapOf("direct" to "Direct", "native" to "Native")
        ),
        CoreOptionDef(
            key = "melonds_sysfile_mode",
            displayName = "BIOS/Firmware Mode",
            values = listOf("native", "builtin"),
            defaultValue = "native",
            description = "Use your provided DS BIOS/firmware, or the built-in open-source replacement",
            valueLabels = mapOf("native" to "Native", "builtin" to "Built-In")
        ),
        CoreOptionDef(
            key = "melonds_render_mode",
            displayName = "Render Mode",
            values = listOf("software", "opengl"),
            defaultValue = "software",
            description = "Software renderer, or hardware OpenGL renderer with upscaling",
            valueLabels = mapOf("software" to "Software", "opengl" to "OpenGL")
        ),
        CoreOptionDef(
            key = "melonds_opengl_resolution",
            displayName = "Internal Resolution",
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
            defaultValue = "1",
            description = "OpenGL upscale factor over native 256x192",
            valueLabels = mapOf(
                "1" to "1x (native)", "2" to "2x", "3" to "3x", "4" to "4x",
                "5" to "5x", "6" to "6x", "7" to "7x", "8" to "8x"
            )
        ),
        CoreOptionDef(
            key = "melonds_opengl_better_polygons",
            displayName = "Improved Polygon Splitting",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reduces polygon distortion at a small performance cost (OpenGL only)"
        ),
        CoreOptionDef(
            key = "melonds_threaded_renderer",
            displayName = "Threaded Software Renderer",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Runs the software renderer on a separate thread for better performance"
        ),
        CoreOptionDef(
            key = "melonds_number_of_screen_layouts",
            displayName = "Number of Screen Layouts",
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
            defaultValue = "2",
            description = "How many layout slots the Next Screen Layout hotkey cycles through"
        ),
        layoutSlot(1, "top-bottom"),
        layoutSlot(2, "left-right"),
        layoutSlot(3, "top"),
        layoutSlot(4, "bottom"),
        layoutSlot(5, "hybrid-top"),
        layoutSlot(6, "hybrid-bottom"),
        layoutSlot(7, "bottom-top"),
        layoutSlot(8, "right-left"),
        CoreOptionDef(
            key = "melonds_screen_gap",
            displayName = "Screen Gap",
            values = listOf("0", "8", "16", "32", "48", "64", "90", "126"),
            defaultValue = "0",
            description = "Pixel gap between the top and bottom screens in vertical layouts",
            valueLabels = mapOf(
                "0" to "None", "8" to "8px", "16" to "16px", "32" to "32px",
                "48" to "48px", "64" to "64px", "90" to "90px", "126" to "126px"
            )
        ),
        CoreOptionDef(
            key = "melonds_hybrid_ratio",
            displayName = "Hybrid Ratio",
            values = listOf("2", "3"),
            defaultValue = "2",
            description = "Size ratio of the large to small screen in hybrid layouts",
            valueLabels = mapOf("2" to "2:1", "3" to "3:1")
        ),
        CoreOptionDef(
            key = "melonds_hybrid_small_screen",
            displayName = "Hybrid Small Screen",
            values = listOf("both", "one"),
            defaultValue = "both",
            description = "Which screen appears as the small inset in hybrid layouts",
            valueLabels = mapOf("both" to "Show Both Screens", "one" to "Show Opposite Screen")
        ),
        CoreOptionDef(
            key = "melonds_opengl_filtering",
            displayName = "Hybrid Screen Filtering",
            values = listOf("nearest", "linear"),
            defaultValue = "nearest",
            description = "Scaling filter applied to the hybrid-layout small screen",
            valueLabels = mapOf("nearest" to "Nearest", "linear" to "Linear")
        ),
        CoreOptionDef(
            key = "melonds_touch_mode",
            displayName = "Touch Mode",
            values = listOf("touch", "joystick", "auto"),
            defaultValue = "touch",
            description = "How the DS touchscreen is driven (Pointer = direct taps)",
            valueLabels = mapOf("touch" to "Pointer", "joystick" to "Joystick", "auto" to "Auto")
        ),
        CoreOptionDef(
            key = "melonds_show_cursor",
            displayName = "Cursor Mode",
            values = listOf("disabled", "touching", "timeout", "always"),
            defaultValue = "disabled",
            description = "When to draw the touch cursor on the bottom screen",
            valueLabels = mapOf(
                "disabled" to "Never", "touching" to "While Touching",
                "timeout" to "Until Timeout", "always" to "Always"
            )
        ),
        CoreOptionDef(
            key = "melonds_mic_input",
            displayName = "Microphone Input Mode",
            values = listOf("microphone", "silence", "blow", "noise"),
            defaultValue = "microphone",
            description = "Sound source fed to the emulated microphone",
            valueLabels = mapOf(
                "microphone" to "Microphone", "silence" to "Silence",
                "blow" to "Blow", "noise" to "Noise"
            )
        ),
        CoreOptionDef(
            key = "melonds_mic_input_active",
            displayName = "Microphone Button Mode",
            values = listOf("hold", "toggle", "always"),
            defaultValue = "hold",
            description = "Whether the Microphone hotkey is held, toggled, or always on",
            valueLabels = mapOf("hold" to "Hold", "toggle" to "Toggle", "always" to "Always")
        ),
        CoreOptionDef(
            key = "melonds_audio_bitdepth",
            displayName = "Audio Bit Depth",
            values = listOf("auto", "10bit", "16bit"),
            defaultValue = "auto",
            description = "DS audio output bit depth",
            valueLabels = mapOf("auto" to "Automatic", "10bit" to "10-bit", "16bit" to "16-bit")
        ),
        CoreOptionDef(
            key = "melonds_audio_interpolation",
            displayName = "Audio Interpolation",
            values = listOf("disabled", "linear", "cosine", "cubic", "gaussian"),
            defaultValue = "disabled",
            description = "Smooths upsampled audio at a small performance cost",
            valueLabels = mapOf(
                "linear" to "Linear", "cosine" to "Cosine",
                "cubic" to "Cubic", "gaussian" to "Gaussian"
            )
        ),
        CoreOptionDef(
            key = "melonds_slot2_device",
            displayName = "Slot-2 Device",
            values = listOf("auto", "rumble-pak", "expansion-pak", "solar1", "solar2", "solar3"),
            defaultValue = "auto",
            description = "Emulated GBA-slot accessory",
            valueLabels = mapOf(
                "auto" to "Auto", "rumble-pak" to "Rumble Pak",
                "expansion-pak" to "Memory Expansion Pak",
                "solar1" to "Solar Sensor (Boktai)",
                "solar2" to "Solar Sensor (Boktai 2)",
                "solar3" to "Solar Sensor (Boktai 3)"
            )
        ),
        CoreOptionDef(
            key = "melonds_jit_enable",
            displayName = "JIT Recompiler",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Dynamic recompiler for much faster emulation"
        ),
        CoreOptionDef(
            key = "melonds_jit_block_size",
            displayName = "JIT Block Size",
            values = listOf("32", "16", "8", "4", "2", "1"),
            defaultValue = "32"
        ),
        CoreOptionDef(
            key = "melonds_jit_branch_optimisations",
            displayName = "JIT Branch Optimizations",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "melonds_jit_literal_optimisations",
            displayName = "JIT Literal Optimizations",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "melonds_jit_fast_memory",
            displayName = "JIT Fast Memory",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled"
        )
    )
}

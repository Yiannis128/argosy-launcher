package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object PcsxRearmedManifest : CoreOptionManifest {
    override val coreId = "pcsx_rearmed"
    override val options = listOf(
        CoreOptionDef(
            key = "pcsx_rearmed_region",
            displayName = "Region",
            values = listOf("auto", "NTSC", "PAL"),
            defaultValue = "auto",
            description = "Selects the system region; mismatched regions may run too fast or slow"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_bios",
            displayName = "BIOS Selection",
            values = listOf("auto", "HLE"),
            defaultValue = "auto",
            description = "Selects between the real PSX BIOS or high-level emulation"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_show_bios_bootlogo",
            displayName = "Show BIOS Name/Boot Logo (Breaks some games)",
            values = listOf("disabled", "enabled", "enabled_no_pcsx"),
            defaultValue = "disabled",
            description = "Shows the PlayStation startup animation when booting",
            valueLabels = mapOf("enabled_no_pcsx" to "ON, w/o PCSXtm")
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_memcard1",
            displayName = "Memory Card 1 Type (Restart)",
            values = listOf("libretro", "serial", "shared", "none"),
            defaultValue = "libretro",
            description = "Selects how the slot 1 memory card is stored",
            valueLabels = mapOf(
                "libretro" to "Libretro (Default)", "serial" to "Game Code (Serial)",
                "shared" to "Shared Between All Games", "none" to "No Memory Card"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_memcard2",
            displayName = "Memory Card 2 Type",
            values = listOf("serial", "shared", "none"),
            defaultValue = "shared",
            description = "Selects how the slot 2 memory card is stored",
            valueLabels = mapOf(
                "serial" to "Game Code (Serial)", "shared" to "Shared Between All Games",
                "none" to "No Memory Card"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_cd_readahead",
            displayName = "CD Read-Ahead",
            values = listOf(
                "0", "1", "2", "4", "8", "12", "16", "32", "64", "128", "256", "512", "1024", "333000"
            ),
            defaultValue = "12",
            description = "Reads sectors ahead of time to avoid stalls; 333000 caches the whole disc"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_drc",
            displayName = "Dynamic Recompiler",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Uses a dynamic recompiler for faster CPU emulation"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_drc_thread",
            displayName = "DynaRec Threading",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "Runs the dynamic recompiler on a separate thread"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_psxclock",
            displayName = "PSX CPU Clock Speed (%)",
            values = listOf(
                "auto", "30", "40", "50", "57", "60", "70", "80", "90", "100"
            ),
            defaultValue = "auto",
            description = "Adjusts the emulated PSX CPU clock speed percentage"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_dithering",
            displayName = "Dithering Pattern",
            values = listOf("disabled", "enabled", "force"),
            defaultValue = "enabled",
            description = "Applies the PSX dithering pattern to simulate more colors",
            valueLabels = mapOf("force" to "Force")
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_thread_rendering",
            displayName = "Threaded Rendering",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "Offloads GPU rendering to a separate thread for better performance"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_frameskip_type",
            displayName = "Frameskip",
            values = listOf("disabled", "auto", "auto_threshold", "fixed_interval"),
            defaultValue = "disabled",
            description = "Skips frames to avoid audio crackling at the cost of smoothness",
            valueLabels = mapOf(
                "auto" to "Auto", "auto_threshold" to "Auto (Threshold)",
                "fixed_interval" to "Fixed Interval"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_frameskip_threshold",
            displayName = "Frameskip Threshold (%)",
            values = listOf(
                "15", "18", "21", "24", "27", "30", "33", "36", "39", "42",
                "45", "48", "51", "54", "57", "60", "65", "70", "75", "80"
            ),
            defaultValue = "33",
            description = "Audio buffer threshold below which frames are skipped in threshold mode"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_frameskip_interval",
            displayName = "Frameskip Interval",
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            defaultValue = "3",
            description = "Maximum frames skipped before a new frame is rendered"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_display_fps_v2",
            displayName = "Display Internal FPS",
            values = listOf("disabled", "enabled", "extra"),
            defaultValue = "disabled",
            description = "Shows the emulated system's internal frame rate on screen"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_fractional_framerate",
            displayName = "Use Fractional Frame Rate",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "Uses the console's true ~59.81/49.75 fps to fix audio/video desync"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_alt_flip",
            displayName = "Framebuffer Readout",
            values = listOf("auto", "early", "late"),
            defaultValue = "auto",
            description = "Selects whether the video frame is taken before or after active display",
            valueLabels = mapOf("auto" to "Auto", "early" to "Early", "late" to "Late")
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_rgb32_output",
            displayName = "RGB32 Output",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Improves color depth for true color modes at higher CPU cost"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_scale_hires",
            displayName = "Hi-Res Downscaling",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Downscales high-resolution video modes to 320x240 by skipping lines"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_slow_llists",
            displayName = "(GPU) Slow Linked List Processing",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "More accurate GPU linked list processing needed by a few games"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_show_overscan",
            displayName = "(GPU) Horizontal Overscan",
            values = listOf("disabled", "auto", "hack"),
            defaultValue = "disabled",
            description = "Displays graphics drawn into the horizontal borders",
            valueLabels = mapOf("auto" to "Auto", "hack" to "Hack")
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_screen_centering",
            displayName = "(GPU) Screen Centering",
            values = listOf("auto", "game", "borderless", "manual"),
            defaultValue = "auto",
            description = "Corrects miscentered image position used by some games",
            valueLabels = mapOf(
                "auto" to "Auto", "game" to "Game-controlled",
                "borderless" to "Borderless", "manual" to "Manual"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_screen_centering_x",
            displayName = "(GPU) Manual Position X",
            values = listOf(
                "-16", "-14", "-12", "-10", "-8", "-6", "-4", "-2", "0",
                "2", "4", "6", "8", "10", "12", "14", "16"
            ),
            defaultValue = "0",
            description = "X offset of the frame buffer in Manual centering mode"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_screen_centering_y",
            displayName = "(GPU) Manual Position Y",
            values = (-16..16).map { it.toString() },
            defaultValue = "0",
            description = "Y offset of the frame buffer in Manual centering mode"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_screen_centering_h_adj",
            displayName = "(GPU) Manual Height Adjustment",
            values = listOf(
                "-64", "-48", "-40", "-32", "-24", "-16", "-8",
                "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0"
            ),
            defaultValue = "0",
            description = "Height adjustment in Manual centering mode"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_neon_interlace_enable_v2",
            displayName = "(GPU) Show Interlaced Video",
            values = listOf("auto", "disabled", "enabled"),
            defaultValue = "auto",
            description = "Outputs interlaced video for high-resolution video modes"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_neon_enhancement_enable",
            displayName = "(GPU) Enhanced Resolution (Slow)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Doubles the internal rendering resolution at a performance cost"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_neon_enhancement_no_main",
            displayName = "(GPU) Enhanced Resolution Speed Hack",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Improves enhanced-resolution performance at reduced compatibility"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_neon_enhancement_tex_adj_v2",
            displayName = "(GPU) Enhanced Resolution Texture Adjustment",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Fixes texturing issues in some games in enhanced-resolution mode"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_odd_even_bit",
            displayName = "(GPU) Odd/Even Bit Hack",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Fixes lock-ups in games such as Chrono Cross"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_expand_screen_width",
            displayName = "(GPU) Expand Screen Width",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enlarges the display area for Capcom 2D fighting games"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_ignore_brightness",
            displayName = "(GPU) Ignore Brightness Color",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Repairs black screens in Lunar Silver Star Story Complete"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_disable_coord_check",
            displayName = "(GPU) Disable Coordinate Check",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Legacy compatibility mode for games failing on newer GPU hardware"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_lazy_screen_update",
            displayName = "(GPU) Lazy Screen Update",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Prevents text box flickering in Dragon Warrior VII"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_repeated_triangles",
            displayName = "(GPU) Repeat Flat Tex Triangles",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Corrects rendering errors in Star Wars: Dark Forces"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_quads_with_triangles",
            displayName = "(GPU) Draw Tex-Quads as Triangles",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Corrects Gouraud-shading distortions at reduced texture quality"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_peops_fake_busy_state",
            displayName = "(GPU) Fake 'GPU Busy' States",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Emulates the GPU busy flag to improve compatibility"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_unai_old_renderer",
            displayName = "(GPU) Old Renderer",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables faster but less accurate UNAI rendering"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_unai_blending",
            displayName = "(GPU) Texture Blending",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables semi-transparency blending effects"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_unai_skipline",
            displayName = "(GPU) Skip Every 2nd Line",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Skips every second scanline to improve performance"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_unai_lighting",
            displayName = "(GPU) Lighting Effects",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables vertex lighting calculations for shaded polygons"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gpu_unai_fast_lighting",
            displayName = "(GPU) Fast Lighting",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Uses a simplified lighting algorithm for better performance"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_spu_reverb",
            displayName = "Audio Reverb Effects",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables the SPU reverb effect for ambient audio"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_spu_interpolation",
            displayName = "Sound Interpolation",
            values = listOf("simple", "gaussian", "cubic", "off"),
            defaultValue = "simple",
            description = "Selects the audio sample interpolation method",
            valueLabels = mapOf(
                "simple" to "Simple", "gaussian" to "Gaussian", "cubic" to "Cubic", "off" to "Off"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_nocdaudio",
            displayName = "CD Audio",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables playback of CD-DA audio tracks"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_noxadecoding",
            displayName = "XA Decoding",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables decoding of XA audio streams used for voice and music"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_spu_thread",
            displayName = "Threaded SPU",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Emulates the SPU on a separate thread; may cause audio glitches"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_analog_axis_modifier",
            displayName = "Analog Axis Bounds",
            values = listOf("circle", "square"),
            defaultValue = "square",
            description = "Sets the analog stick range shape to match the DualShock's circular gate",
            valueLabels = mapOf("circle" to "Circle", "square" to "Square")
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_vibration",
            displayName = "Rumble Effects",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Enables DualShock controller vibration feedback"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_analog_combo",
            displayName = "DualShock Analog Mode Toggle Key Combo",
            values = listOf(
                "disabled", "l1+r1+select", "l1+r1+start", "l1+r1+l3", "l1+r1+r3", "l3+r3"
            ),
            defaultValue = "l1+r1+select",
            description = "Sets the button combination that toggles analog mode",
            valueLabels = mapOf(
                "l1+r1+select" to "L1 + R1 + Select", "l1+r1+start" to "L1 + R1 + Start",
                "l1+r1+l3" to "L1 + R1 + L3", "l1+r1+r3" to "L1 + R1 + R3", "l3+r3" to "L3 + R3"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_multitap",
            displayName = "Multitap Mode",
            values = listOf("disabled", "port 1", "port 2", "ports 1 and 2"),
            defaultValue = "disabled",
            description = "Enables multitap adapters for more than 2 players",
            valueLabels = mapOf(
                "port 1" to "Port 1", "port 2" to "Port 2", "ports 1 and 2" to "Ports 1 and 2"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_negcon_deadzone",
            displayName = "NegCon Twist Deadzone",
            values = listOf("0", "5", "10", "15", "20", "25", "30"),
            defaultValue = "0",
            description = "Sets the deadzone for NegCon twist input used in racing games",
            valueLabels = mapOf(
                "0" to "0%", "5" to "5%", "10" to "10%", "15" to "15%",
                "20" to "20%", "25" to "25%", "30" to "30%"
            )
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_negcon_response",
            displayName = "NegCon Twist Response",
            values = listOf("linear", "quadratic", "cubic"),
            defaultValue = "linear",
            description = "Sets the response curve for NegCon twist input"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_input_sensitivity",
            displayName = "Mouse Sensitivity",
            values = listOf(
                "0.05", "0.10", "0.15", "0.20", "0.25", "0.30", "0.35", "0.40", "0.45", "0.50",
                "0.55", "0.60", "0.65", "0.70", "0.75", "0.80", "0.85", "0.90", "0.95", "1.00",
                "1.05", "1.10", "1.15", "1.20", "1.25", "1.30", "1.35", "1.40", "1.45", "1.50",
                "1.55", "1.60", "1.65", "1.70", "1.75", "1.80", "1.85", "1.90", "1.95", "2.00"
            ),
            defaultValue = "1.00"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_crosshair1",
            displayName = "Player 1 Lightgun Crosshair",
            values = listOf("disabled", "blue", "green", "red", "white"),
            defaultValue = "disabled",
            description = "Toggles the player 1 crosshair (requires RGB32 output off)"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_crosshair2",
            displayName = "Player 2 Lightgun Crosshair",
            values = listOf("disabled", "blue", "green", "red", "white"),
            defaultValue = "disabled",
            description = "Toggles the player 2 crosshair (requires RGB32 output off)"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_konamigunadjustx",
            displayName = "Konami Gun X Axis Offset",
            values = (-40..40).map { it.toString() },
            defaultValue = "0",
            description = "Applies an X offset to Konami Gun input to correct aiming"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_konamigunadjusty",
            displayName = "Konami Gun Y Axis Offset",
            values = (-40..40).map { it.toString() },
            defaultValue = "0",
            description = "Applies a Y offset to Konami Gun input to correct aiming"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gunconadjustx",
            displayName = "Guncon X Axis Offset",
            values = (-40..40).map { it.toString() },
            defaultValue = "0",
            description = "Applies an X offset to Guncon input to correct aiming"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gunconadjusty",
            displayName = "Guncon Y Axis Offset",
            values = (-40..40).map { it.toString() },
            defaultValue = "0",
            description = "Applies a Y offset to Guncon input to correct aiming"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gunconadjustratiox",
            displayName = "Guncon X Axis Response",
            values = listOf(
                "0.75", "0.76", "0.77", "0.78", "0.79", "0.80", "0.81", "0.82", "0.83", "0.84",
                "0.85", "0.86", "0.87", "0.88", "0.89", "0.90", "0.91", "0.92", "0.93", "0.94",
                "0.95", "0.96", "0.97", "0.98", "0.99", "1.00", "1.01", "1.02", "1.03", "1.04",
                "1.05", "1.06", "1.07", "1.08", "1.09", "1.10", "1.11", "1.12", "1.13", "1.14",
                "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21", "1.22", "1.23", "1.24",
                "1.25"
            ),
            defaultValue = "1.00",
            description = "Adjusts the horizontal magnitude of Guncon motion"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gunconadjustratioy",
            displayName = "Guncon Y Axis Response",
            values = listOf(
                "0.75", "0.76", "0.77", "0.78", "0.79", "0.80", "0.81", "0.82", "0.83", "0.84",
                "0.85", "0.86", "0.87", "0.88", "0.89", "0.90", "0.91", "0.92", "0.93", "0.94",
                "0.95", "0.96", "0.97", "0.98", "0.99", "1.00", "1.01", "1.02", "1.03", "1.04",
                "1.05", "1.06", "1.07", "1.08", "1.09", "1.10", "1.11", "1.12", "1.13", "1.14",
                "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21", "1.22", "1.23", "1.24",
                "1.25"
            ),
            defaultValue = "1.00",
            description = "Adjusts the vertical magnitude of Guncon motion"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_icache_emulation",
            displayName = "Instruction Cache Emulation",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Emulates the CPU instruction cache; required for the Formula One games"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_exception_emulation",
            displayName = "Exception and Breakpoint Emulation",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Emulates rarely-used debug features for homebrew developers"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_nocompathacks",
            displayName = "Disable Automatic Compatibility Hacks",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Disables the auto-applied per-game compatibility hacks"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_cd_turbo",
            displayName = "Turbo CD",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Makes the emulated CD-ROM extremely fast; many games may crash"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_nosmccheck",
            displayName = "(Speed Hack) Disable SMC Checks",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Disables self-modifying code checks; may corrupt saves and crash"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_gteregsunneeded",
            displayName = "(Speed Hack) Assume GTE Registers Unneeded",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Skips GTE register tracking; may cause rendering errors"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_nogteflags",
            displayName = "(Speed Hack) Disable GTE Flags",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Disables GTE flag calculation; will cause rendering errors"
        ),
        CoreOptionDef(
            key = "pcsx_rearmed_nostalls",
            displayName = "Disable CPU/GTE Stalls",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Removes CPU/GTE stall emulation; makes some games run too fast"
        ),
    )
}

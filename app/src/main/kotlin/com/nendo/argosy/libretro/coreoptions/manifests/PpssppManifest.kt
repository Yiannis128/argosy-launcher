package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object PpssppManifest : CoreOptionManifest {
    override val coreId = "ppsspp"
    override val options = listOf(
        CoreOptionDef(
            key = "ppsspp_cpu_core",
            displayName = "CPU Core",
            values = listOf("JIT", "IR JIT", "Interpreter"),
            defaultValue = "JIT",
            description = "Selects the CPU emulation method, JIT is fastest but less compatible",
            valueLabels = mapOf("JIT" to "Dynarec (JIT)", "IR JIT" to "IR Interpreter")
        ),
        CoreOptionDef(
            key = "ppsspp_fast_memory",
            displayName = "Fast Memory (Speedhack)",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Skips memory access safety checks for faster emulation"
        ),
        CoreOptionDef(
            key = "ppsspp_ignore_bad_memory_access",
            displayName = "Ignore Bad Memory Accesses",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled"
        ),
        CoreOptionDef(
            key = "ppsspp_io_timing_method",
            displayName = "I/O Timing Method",
            values = listOf("Fast", "Host", "Simulate UMD delays"),
            defaultValue = "Fast"
        ),
        CoreOptionDef(
            key = "ppsspp_force_lag_sync",
            displayName = "Force Real Clock Sync",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Slower, less lag"
        ),
        CoreOptionDef(
            key = "ppsspp_locked_cpu_speed",
            displayName = "Locked CPU Speed",
            values = listOf(
                "disabled", "222MHz", "266MHz", "333MHz", "366MHz", "399MHz",
                "432MHz", "444MHz", "466MHz", "499MHz", "532MHz", "555MHz",
                "566MHz", "599MHz", "632MHz", "666MHz", "777MHz", "888MHz", "999MHz"
            ),
            defaultValue = "disabled",
            description = "Forces the emulated PSP CPU to run at a fixed clock speed"
        ),
        CoreOptionDef(
            key = "ppsspp_memstick_inserted",
            displayName = "Memory Stick Inserted",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Some games require ejecting/inserting the Memory Stick"
        ),
        CoreOptionDef(
            key = "ppsspp_cache_iso",
            displayName = "Cache Full ISO in RAM",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled"
        ),
        CoreOptionDef(
            key = "ppsspp_cheats",
            displayName = "Internal Cheats Support",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables loading and applying cheat codes from cheat database files"
        ),
        CoreOptionDef(
            key = "ppsspp_language",
            displayName = "Game Language",
            values = listOf(
                "Automatic", "English", "Japanese", "French", "Spanish", "German",
                "Italian", "Dutch", "Portuguese", "Russian", "Korean",
                "Chinese Traditional", "Chinese Simplified"
            ),
            defaultValue = "Automatic",
            description = "Automatic will use the frontend language"
        ),
        CoreOptionDef(
            key = "ppsspp_psp_model",
            displayName = "PSP Model",
            values = listOf("psp_1000", "psp_2000_3000"),
            defaultValue = "psp_2000_3000",
            valueLabels = mapOf("psp_1000" to "PSP-1000", "psp_2000_3000" to "PSP-2000/3000")
        ),
        CoreOptionDef(
            key = "ppsspp_backend",
            displayName = "Backend",
            values = listOf("auto", "opengl", "vulkan", "none"),
            defaultValue = "auto",
            description = "Automatic will use the frontend video driver",
            valueLabels = mapOf(
                "auto" to "Automatic", "opengl" to "OpenGL", "vulkan" to "Vulkan", "none" to "None"
            )
        ),
        CoreOptionDef(
            key = "ppsspp_software_rendering",
            displayName = "Software Rendering",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Slow but accurate, requires core restart"
        ),
        CoreOptionDef(
            key = "ppsspp_internal_resolution",
            displayName = "Rendering Resolution",
            values = listOf(
                "480x272", "960x544", "1440x816", "1920x1088", "2400x1360",
                "2880x1632", "3360x1904", "3840x2176", "4320x2448", "4800x2720"
            ),
            defaultValue = "480x272",
            description = "Sets the 3D rendering resolution, higher values look sharper",
            valueLabels = mapOf(
                "480x272" to "1x (480x272)", "960x544" to "2x (960x544)",
                "1440x816" to "3x (1440x816)", "1920x1088" to "4x (1920x1088)",
                "2400x1360" to "5x (2400x1360)", "2880x1632" to "6x (2880x1632)",
                "3360x1904" to "7x (3360x1904)", "3840x2176" to "8x (3840x2176)",
                "4320x2448" to "9x (4320x2448)", "4800x2720" to "10x (4800x2720)"
            )
        ),
        CoreOptionDef(
            key = "ppsspp_mulitsample_level",
            displayName = "MSAA Antialiasing",
            values = listOf("Disabled", "x2", "x4", "x8"),
            defaultValue = "Disabled",
            description = "Vulkan only, requires core restart"
        ),
        CoreOptionDef(
            key = "ppsspp_cropto16x9",
            displayName = "Crop to 16x9",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Removes one line from top and bottom to get exact 16:9"
        ),
        CoreOptionDef(
            key = "ppsspp_frameskip",
            displayName = "Frameskip",
            values = listOf("disabled", "1", "2", "3", "4", "5", "6", "7", "8"),
            defaultValue = "disabled",
            description = "Sets how many frames to skip between each rendered frame",
            valueLabels = mapOf("disabled" to "Off")
        ),
        CoreOptionDef(
            key = "ppsspp_frameskiptype",
            displayName = "Frameskip Type",
            values = listOf("Number of frames", "Percent of FPS"),
            defaultValue = "Number of frames"
        ),
        CoreOptionDef(
            key = "ppsspp_auto_frameskip",
            displayName = "Auto Frameskip",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Automatically skips frames to maintain full speed"
        ),
        CoreOptionDef(
            key = "ppsspp_frame_duplication",
            displayName = "Render Duplicate Frames to 60 Hz",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Can make framerate smoother in games that run at lower framerates"
        ),
        CoreOptionDef(
            key = "ppsspp_detect_vsync_swap_interval",
            displayName = "Detect Frame Rate Changes",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Notify frontend of frame rate changes"
        ),
        CoreOptionDef(
            key = "ppsspp_inflight_frames",
            displayName = "Buffer Graphics Commands",
            values = listOf("No buffer", "Up to 1", "Up to 2"),
            defaultValue = "Up to 2",
            description = "GL/Vulkan only, slower but less lag, requires restart"
        ),
        CoreOptionDef(
            key = "ppsspp_button_preference",
            displayName = "Confirmation Button",
            values = listOf("Cross", "Circle"),
            defaultValue = "Cross",
            description = "Sets which button is used for confirm actions in the PSP menu"
        ),
        CoreOptionDef(
            key = "ppsspp_analog_is_circular",
            displayName = "Analog Circle vs Square Gate Compensation",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled"
        ),
        CoreOptionDef(
            key = "ppsspp_analog_deadzone",
            displayName = "Analog Deadzone",
            values = listOf("0.0", "0.05", "0.10", "0.15", "0.20", "0.25", "0.30", "0.35", "0.40", "0.45", "0.50"),
            defaultValue = "0.0",
            description = "Additional deadzone to apply after frontend input",
            valueLabels = mapOf(
                "0.0" to "0%", "0.05" to "5%", "0.10" to "10%", "0.15" to "15%",
                "0.20" to "20%", "0.25" to "25%", "0.30" to "30%", "0.35" to "35%",
                "0.40" to "40%", "0.45" to "45%", "0.50" to "50%"
            )
        ),
        CoreOptionDef(
            key = "ppsspp_analog_sensitivity",
            displayName = "Analog Axis Scale",
            values = listOf("1.00", "1.05", "1.10", "1.15", "1.20", "1.25", "1.30", "1.35", "1.40", "1.45", "1.50"),
            defaultValue = "1.00",
            description = "Additional sensitivity factor to apply after frontend input"
        ),
        CoreOptionDef(
            key = "ppsspp_skip_buffer_effects",
            displayName = "Skip Buffer Effects",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Faster, but nothing may draw in some games"
        ),
        CoreOptionDef(
            key = "ppsspp_skip_gpu_readbacks",
            displayName = "Skip GPU Readbacks",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Some games require GPU readbacks, so be careful"
        ),
        CoreOptionDef(
            key = "ppsspp_lazy_texture_caching",
            displayName = "Lazy Texture Caching (Speedup)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Faster, but can cause text problems in a few games"
        ),
        CoreOptionDef(
            key = "ppsspp_spline_quality",
            displayName = "Spline/Bezier Curves Quality",
            values = listOf("Low", "Medium", "High"),
            defaultValue = "High",
            description = "Only used by some games, controls smoothness of curves"
        ),
        CoreOptionDef(
            key = "ppsspp_lower_resolution_for_effects",
            displayName = "Lower Resolution for Effects",
            values = listOf("disabled", "Safe", "Balanced", "Aggressive"),
            defaultValue = "disabled",
            description = "Reduces artifacts"
        ),
        CoreOptionDef(
            key = "ppsspp_gpu_hardware_transform",
            displayName = "Hardware Transform",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Uses GPU hardware for transform and lighting calculations"
        ),
        CoreOptionDef(
            key = "ppsspp_software_skinning",
            displayName = "Software Skinning",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Combine skinned model draws on the CPU, faster in most games"
        ),
        CoreOptionDef(
            key = "ppsspp_hardware_tesselation",
            displayName = "Hardware Tesselation",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Uses hardware to make curves"
        ),
        CoreOptionDef(
            key = "ppsspp_texture_scaling_type",
            displayName = "Texture Upscale Type",
            values = listOf("xbrz", "hybrid", "bicubic", "hybrid_bicubic"),
            defaultValue = "xbrz",
            description = "Selects the algorithm used for texture upscaling",
            valueLabels = mapOf(
                "xbrz" to "xBRZ", "hybrid" to "Hybrid", "bicubic" to "Bicubic",
                "hybrid_bicubic" to "Hybrid + Bicubic"
            )
        ),
        CoreOptionDef(
            key = "ppsspp_texture_scaling_level",
            displayName = "Texture Upscaling Level",
            values = listOf("disabled", "2x", "3x", "4x", "5x"),
            defaultValue = "disabled",
            description = "CPU heavy, some scaling may be delayed to avoid stutter",
            valueLabels = mapOf("disabled" to "Off")
        ),
        CoreOptionDef(
            key = "ppsspp_texture_deposterize",
            displayName = "Texture Deposterize",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Smooths color banding artifacts in textures"
        ),
        CoreOptionDef(
            key = "ppsspp_texture_shader",
            displayName = "Texture Shader",
            values = listOf("disabled", "2xBRZ", "4xBRZ", "MMPX"),
            defaultValue = "disabled",
            description = "Vulkan only, overrides Texture Upscale Type",
            valueLabels = mapOf("2xBRZ" to "Tex2xBRZ", "4xBRZ" to "Tex4xBRZ", "MMPX" to "TexMMPX")
        ),
        CoreOptionDef(
            key = "ppsspp_texture_anisotropic_filtering",
            displayName = "Anisotropic Filtering",
            values = listOf("disabled", "2x", "4x", "8x", "16x"),
            defaultValue = "16x",
            description = "Improves texture clarity at steep viewing angles"
        ),
        CoreOptionDef(
            key = "ppsspp_texture_filtering",
            displayName = "Texture Filtering",
            values = listOf("Auto", "Nearest", "Linear", "Auto max quality"),
            defaultValue = "Auto",
            description = "Selects the texture sampling filter method"
        ),
        CoreOptionDef(
            key = "ppsspp_smart_2d_texture_filtering",
            displayName = "Smart 2D Texture Filtering",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Switches to nearest filtering to remove artifacts in some 2D games"
        ),
        CoreOptionDef(
            key = "ppsspp_texture_replacement",
            displayName = "Texture Replacement",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled"
        ),
    )
}

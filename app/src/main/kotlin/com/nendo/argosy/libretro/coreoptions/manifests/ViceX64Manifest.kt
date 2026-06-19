package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object ViceX64Manifest : CoreOptionManifest {
    override val coreId = "vice_x64"
    override val options = listOf(
        CoreOptionDef(
            key = "vice_c64_model",
            displayName = "Model",
            values = listOf(
                "C64 PAL auto", "C64 NTSC auto", "C64C PAL auto", "C64C NTSC auto",
                "C64 PAL", "C64 NTSC", "C64C PAL", "C64C NTSC",
                "C64SX PAL", "C64SX NTSC", "PET64 PAL", "PET64 NTSC",
                "C64 GS PAL", "C64 JAP NTSC"
            ),
            defaultValue = "C64 PAL auto",
            description = "Selects the C64 hardware model and video standard to emulate"
        ),
        CoreOptionDef(
            key = "vice_jiffydos",
            displayName = "JiffyDOS",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables the JiffyDOS speed-enhanced disk operating system"
        ),
        CoreOptionDef(
            key = "vice_ram_expansion_unit",
            displayName = "RAM Expansion Unit",
            values = listOf("none", "128kB", "256kB", "512kB", "1024kB", "2048kB", "4096kB", "8192kB", "16384kB"),
            defaultValue = "none",
            description = "Attaches a RAM expansion unit with the selected capacity"
        ),
        CoreOptionDef(
            key = "vice_printer",
            displayName = "Printer",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables the emulated printer output device"
        ),
        CoreOptionDef(
            key = "vice_read_vicerc",
            displayName = "Read 'vicerc'",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Loads settings from the vicerc configuration file"
        ),
        CoreOptionDef(
            key = "vice_reset",
            displayName = "Reset Type",
            values = listOf("autostart", "soft", "hard", "freeze"),
            defaultValue = "autostart",
            description = "Selects which reset method to use when restarting content"
        ),
        CoreOptionDef(
            key = "vice_autoloadwarp",
            displayName = "Automatic Load Warp",
            values = listOf("disabled", "enabled", "mute", "disk", "disk_mute", "tape", "tape_mute"),
            defaultValue = "disabled",
            description = "Activates warp speed during disk or tape loading",
            valueLabels = mapOf(
                "disabled" to "Off", "enabled" to "On", "mute" to "On (muted)",
                "disk" to "Disk only", "disk_mute" to "Disk only (muted)",
                "tape" to "Tape only", "tape_mute" to "Tape only (muted)"
            )
        ),
        CoreOptionDef(
            key = "vice_warp_boost",
            displayName = "Warp Boost",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables additional warp speed optimizations"
        ),
        CoreOptionDef(
            key = "vice_autostart",
            displayName = "Autostart",
            values = listOf("disabled", "enabled", "warp"),
            defaultValue = "enabled",
            description = "Automatically runs loaded content on startup"
        ),
        CoreOptionDef(
            key = "vice_drive_true_emulation",
            displayName = "True Drive Emulation",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Emulates disk drive hardware at cycle-exact level for compatibility"
        ),
        CoreOptionDef(
            key = "vice_virtual_device_traps",
            displayName = "Virtual Device Traps",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables fast I/O traps that bypass true drive emulation"
        ),
        CoreOptionDef(
            key = "vice_floppy_multidrive",
            displayName = "Floppy MultiDrive",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables multiple floppy drives for multi-disk programs"
        ),
        CoreOptionDef(
            key = "vice_floppy_write_protection",
            displayName = "Floppy Write Protection",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Write-protects all inserted floppy disk images"
        ),
        CoreOptionDef(
            key = "vice_easyflash_write_protection",
            displayName = "EasyFlash Write Protection",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Write-protects EasyFlash cartridge images"
        ),
        CoreOptionDef(
            key = "vice_work_disk",
            displayName = "Global Work Disk",
            values = listOf("disabled", "8_d64", "9_d64", "8_d71", "9_d71", "8_d81", "9_d81", "8_fs", "9_fs"),
            defaultValue = "disabled",
            description = "Attaches a work disk for saving data across sessions",
            valueLabels = mapOf(
                "disabled" to "Disabled", "8_d64" to "Drive 8 (D64)", "9_d64" to "Drive 9 (D64)",
                "8_d71" to "Drive 8 (D71)", "9_d71" to "Drive 9 (D71)",
                "8_d81" to "Drive 8 (D81)", "9_d81" to "Drive 9 (D81)",
                "8_fs" to "Drive 8 (Filesystem)", "9_fs" to "Drive 9 (Filesystem)"
            )
        ),
        CoreOptionDef(
            key = "vice_video_options_display",
            displayName = "Show Video Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reveals additional video configuration options"
        ),
        CoreOptionDef(
            key = "vice_aspect_ratio",
            displayName = "Pixel Aspect Ratio",
            values = listOf("auto", "pal", "ntsc", "raw"),
            defaultValue = "auto",
            valueLabels = mapOf("auto" to "Auto", "pal" to "PAL", "ntsc" to "NTSC", "raw" to "Raw (1:1)")
        ),
        CoreOptionDef(
            key = "vice_crop",
            displayName = "Crop",
            values = listOf("disabled", "small", "medium", "maximum", "auto", "auto_disable", "manual"),
            defaultValue = "disabled",
            description = "Removes border areas from the display"
        ),
        CoreOptionDef(
            key = "vice_crop_delay",
            displayName = "Automatic Crop Delay",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Adds a delay before automatic crop adjustments to prevent flicker"
        ),
        CoreOptionDef(
            key = "vice_crop_mode",
            displayName = "Crop Mode",
            values = listOf("both", "horizontal", "vertical", "16:9", "16:10", "4:3", "5:4"),
            defaultValue = "both",
            description = "Selects which edges or aspect ratio to crop to"
        ),
        CoreOptionDef(
            key = "vice_manual_crop_top",
            displayName = "Manual Crop Top",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "40", "50", "60"),
            defaultValue = "0",
            description = "Pixels to crop from the top edge when using manual crop"
        ),
        CoreOptionDef(
            key = "vice_manual_crop_bottom",
            displayName = "Manual Crop Bottom",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "40", "50", "60"),
            defaultValue = "0",
            description = "Pixels to crop from the bottom edge when using manual crop"
        ),
        CoreOptionDef(
            key = "vice_manual_crop_left",
            displayName = "Manual Crop Left",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "40", "50", "60"),
            defaultValue = "0",
            description = "Pixels to crop from the left edge when using manual crop"
        ),
        CoreOptionDef(
            key = "vice_manual_crop_right",
            displayName = "Manual Crop Right",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "40", "50", "60"),
            defaultValue = "0",
            description = "Pixels to crop from the right edge when using manual crop"
        ),
        CoreOptionDef(
            key = "vice_vicii_filter_oddline_offset",
            displayName = "VIC-II Filter Oddline Offset",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "PAL emulation filter oddline offset",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_filter_oddline_phase",
            displayName = "VIC-II Filter Oddline Phase",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "PAL emulation filter oddline phase, internal palette only",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_color_gamma",
            displayName = "VIC-II Color Gamma",
            values = listOf("1000", "1400", "1800", "2000", "2200", "2400", "2600", "2800", "3000", "3400", "4000"),
            defaultValue = "2800",
            description = "Gamma for the internal palette",
            valueLabels = mapOf(
                "1000" to "1.00", "1400" to "1.40", "1800" to "1.80", "2000" to "2.00",
                "2200" to "2.20", "2400" to "2.40", "2600" to "2.60", "2800" to "2.80",
                "3000" to "3.00", "3400" to "3.40", "4000" to "4.00"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_color_brightness",
            displayName = "VIC-II Color Brightness",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "Brightness for the internal palette",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_color_contrast",
            displayName = "VIC-II Color Contrast",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "Contrast for the internal palette",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_color_saturation",
            displayName = "VIC-II Color Saturation",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "Saturation for the internal palette",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_vicii_color_tint",
            displayName = "VIC-II Color Tint",
            values = listOf("20", "100", "200", "400", "600", "800", "1000", "1200", "1400", "1600", "1800", "2000"),
            defaultValue = "1000",
            description = "Tint for the internal palette",
            valueLabels = mapOf(
                "20" to "1%", "100" to "5%", "200" to "10%", "400" to "20%",
                "600" to "30%", "800" to "40%", "1000" to "50%", "1200" to "60%",
                "1400" to "70%", "1600" to "80%", "1800" to "90%", "2000" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_gfx_colors",
            displayName = "Color Depth",
            values = listOf("16bit", "24bit"),
            defaultValue = "16bit",
            valueLabels = mapOf("16bit" to "16-bit", "24bit" to "24-bit")
        ),
        CoreOptionDef(
            key = "vice_vicii_filter",
            displayName = "VIC-II Filter",
            values = listOf("disabled", "enabled_noblur", "enabled_lowblur", "enabled_medblur", "enabled"),
            defaultValue = "disabled",
            description = "Applies CRT-style filtering to the VIC-II video output",
            valueLabels = mapOf(
                "disabled" to "Off", "enabled_noblur" to "On (no blur)",
                "enabled_lowblur" to "On (low blur)", "enabled_medblur" to "On (medium blur)",
                "enabled" to "On (full blur)"
            )
        ),
        CoreOptionDef(
            key = "vice_external_palette",
            displayName = "VIC-II Color Palette",
            values = listOf(
                "default", "c64hq", "c64s", "ccs64", "cjam", "colodore",
                "community-colors", "deekay", "frodo", "godot", "lemon64",
                "palette", "palette_6569R1_v1r", "palette_6569R5_v1r",
                "palette_8565R2_v1r", "palette_C64_amber", "palette_C64_cyan",
                "palette_C64_green", "pc64", "pepto-pal", "pepto-palold",
                "pepto-ntsc", "pepto-ntsc-sony", "pixcen", "ptoing", "rgb",
                "the64", "vice"
            ),
            defaultValue = "default",
            description = "Selects a color palette to match different C64 hardware revisions"
        ),
        CoreOptionDef(
            key = "vice_vkbd_theme",
            displayName = "Virtual KBD Theme",
            values = listOf(
                "auto", "auto_outline", "brown", "brown_outline",
                "beige", "beige_outline", "dark", "dark_outline",
                "light", "light_outline"
            ),
            defaultValue = "auto"
        ),
        CoreOptionDef(
            key = "vice_vkbd_transparency",
            displayName = "Virtual KBD Transparency",
            values = listOf("0%", "25%", "50%", "75%", "100%"),
            defaultValue = "25%"
        ),
        CoreOptionDef(
            key = "vice_vkbd_dimming",
            displayName = "Virtual KBD Dimming",
            values = listOf("0%", "25%", "50%", "75%", "100%"),
            defaultValue = "25%"
        ),
        CoreOptionDef(
            key = "vice_statusbar",
            displayName = "Statusbar Mode",
            values = listOf(
                "bottom", "bottom_minimal", "bottom_basic", "bottom_basic_minimal",
                "top", "top_minimal", "top_basic", "top_basic_minimal"
            ),
            defaultValue = "bottom"
        ),
        CoreOptionDef(
            key = "vice_statusbar_startup",
            displayName = "Statusbar Startup",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Shows the statusbar when the core first starts"
        ),
        CoreOptionDef(
            key = "vice_statusbar_messages",
            displayName = "Statusbar Messages",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Shows emulator status messages on the statusbar"
        ),
        CoreOptionDef(
            key = "vice_joyport_pointer_color",
            displayName = "Light Pen/Gun Pointer Color",
            values = listOf("disabled", "black", "white", "red", "green", "blue", "yellow", "purple"),
            defaultValue = "blue"
        ),
        CoreOptionDef(
            key = "vice_audio_options_display",
            displayName = "Show Audio Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Reveals additional audio configuration options"
        ),
        CoreOptionDef(
            key = "vice_drive_sound_emulation",
            displayName = "Drive Sound Emulation",
            values = listOf(
                "disabled", "5%", "10%", "15%", "20%", "25%", "30%", "35%",
                "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%",
                "80%", "85%", "90%", "95%", "100%"
            ),
            defaultValue = "20%",
            description = "Plays floppy drive mechanical sounds at the selected volume"
        ),
        CoreOptionDef(
            key = "vice_datasette_sound",
            displayName = "Datasette Sound",
            values = listOf(
                "disabled", "5%", "10%", "15%", "20%", "25%", "30%", "35%",
                "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%",
                "80%", "85%", "90%", "95%", "100%", "-1"
            ),
            defaultValue = "disabled",
            description = "Plays datasette tape sounds at the selected volume",
            valueLabels = mapOf("-1" to "100% + Mute")
        ),
        CoreOptionDef(
            key = "vice_audio_leak_emulation",
            displayName = "Audio Leak Emulation",
            values = listOf("disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            defaultValue = "disabled",
            description = "Simulates audio signal leakage between channels",
            valueLabels = mapOf(
                "disabled" to "Off", "1" to "1 (lowest)", "2" to "2", "3" to "3",
                "4" to "4", "5" to "5", "6" to "6", "7" to "7", "8" to "8",
                "9" to "9", "10" to "10 (highest)"
            )
        ),
        CoreOptionDef(
            key = "vice_sid_engine",
            displayName = "SID Engine",
            values = listOf("FastSID", "ReSID", "ReSID-FP"),
            defaultValue = "ReSID",
            description = "Selects the sound chip emulation engine for audio quality vs performance"
        ),
        CoreOptionDef(
            key = "vice_sid_model",
            displayName = "SID Model",
            values = listOf("default", "6581", "8580", "8580RD"),
            defaultValue = "default",
            description = "Selects the SID chip revision to emulate, each with a distinct sound"
        ),
        CoreOptionDef(
            key = "vice_sid_extra",
            displayName = "SID Extra",
            values = listOf("disabled", "0xd420", "0xd500", "0xde00", "0xdf00"),
            defaultValue = "disabled",
            description = "Enables a second SID chip at the specified address for stereo music",
            valueLabels = mapOf(
                "disabled" to "Off", "0xd420" to "\$D420", "0xd500" to "\$D500",
                "0xde00" to "\$DE00", "0xdf00" to "\$DF00"
            )
        ),
        CoreOptionDef(
            key = "vice_resid_sampling",
            displayName = "ReSID Sampling",
            values = listOf("fast", "interpolation", "fast resampling", "resampling"),
            defaultValue = "fast",
            description = "Selects the ReSID resampling method for audio quality vs performance",
            valueLabels = mapOf(
                "fast" to "Fast", "interpolation" to "Interpolation",
                "fast resampling" to "Fast Resampling", "resampling" to "Resampling"
            )
        ),
        CoreOptionDef(
            key = "vice_resid_passband",
            displayName = "ReSID Filter Passband",
            values = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90"),
            defaultValue = "90",
            description = "Adjusts the ReSID resampling filter passband percentage",
            valueLabels = mapOf(
                "0" to "0%", "10" to "10%", "20" to "20%", "30" to "30%",
                "40" to "40%", "50" to "50%", "60" to "60%", "70" to "70%",
                "80" to "80%", "90" to "90%"
            )
        ),
        CoreOptionDef(
            key = "vice_resid_gain",
            displayName = "ReSID Filter Gain",
            values = listOf("90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"),
            defaultValue = "97",
            description = "Adjusts the ReSID filter output gain percentage",
            valueLabels = mapOf(
                "90" to "90%", "91" to "91%", "92" to "92%", "93" to "93%",
                "94" to "94%", "95" to "95%", "96" to "96%", "97" to "97%",
                "98" to "98%", "99" to "99%", "100" to "100%"
            )
        ),
        CoreOptionDef(
            key = "vice_resid_filterbias",
            displayName = "ReSID Filter 6581 Bias",
            values = listOf(
                "-5000", "-4500", "-4000", "-3500", "-3000", "-2500", "-2000",
                "-1500", "-1000", "-500", "0", "500", "1000", "1500", "2000",
                "2500", "3000", "3500", "4000", "4500", "5000"
            ),
            defaultValue = "500",
            description = "Fine-tunes the 6581 SID filter characteristics"
        ),
        CoreOptionDef(
            key = "vice_resid_8580filterbias",
            displayName = "ReSID Filter 8580 Bias",
            values = listOf(
                "-5000", "-4500", "-4000", "-3500", "-3000", "-2500", "-2000",
                "-1500", "-1000", "-500", "0", "500", "1000", "1500", "2000",
                "2500", "3000", "3500", "4000", "4500", "5000"
            ),
            defaultValue = "0",
            description = "Fine-tunes the 8580 SID filter characteristics"
        ),
        CoreOptionDef(
            key = "vice_sfx_sound_expander",
            displayName = "SFX Sound Expander",
            values = listOf("disabled", "3526", "3812"),
            defaultValue = "disabled",
            description = "Enables the SFX Sound Expander FM synthesis add-on",
            valueLabels = mapOf("disabled" to "Off", "3526" to "YM3526 (OPL)", "3812" to "YM3812 (OPL2)")
        ),
        CoreOptionDef(
            key = "vice_sound_sample_rate",
            displayName = "Sample Rate",
            values = listOf("22050", "44100", "48000", "96000"),
            defaultValue = "48000",
            valueLabels = mapOf(
                "22050" to "22 kHz", "44100" to "44.1 kHz",
                "48000" to "48 kHz", "96000" to "96 kHz"
            )
        ),
        CoreOptionDef(
            key = "vice_analogmouse",
            displayName = "Analog Stick Mouse",
            values = listOf("disabled", "left", "right", "both"),
            defaultValue = "left",
            description = "Maps analog sticks to mouse movement"
        ),
        CoreOptionDef(
            key = "vice_analogmouse_deadzone",
            displayName = "Analog Stick Mouse Deadzone",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50"),
            defaultValue = "20",
            valueLabels = mapOf(
                "0" to "0%", "5" to "5%", "10" to "10%", "15" to "15%",
                "20" to "20%", "25" to "25%", "30" to "30%", "35" to "35%",
                "40" to "40%", "45" to "45%", "50" to "50%"
            )
        ),
        CoreOptionDef(
            key = "vice_analogmouse_speed",
            displayName = "Left Analog Stick Mouse Speed",
            values = listOf(
                "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9",
                "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8",
                "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7",
                "2.8", "2.9", "3.0"
            ),
            defaultValue = "1.0"
        ),
        CoreOptionDef(
            key = "vice_analogmouse_speed_right",
            displayName = "Right Analog Stick Mouse Speed",
            values = listOf(
                "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9",
                "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8",
                "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7",
                "2.8", "2.9", "3.0"
            ),
            defaultValue = "1.0"
        ),
        CoreOptionDef(
            key = "vice_dpadmouse_speed",
            displayName = "D-Pad Mouse Speed",
            values = listOf(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "10", "11", "12", "13", "14", "15", "16", "17", "18"
            ),
            defaultValue = "6"
        ),
        CoreOptionDef(
            key = "vice_mouse_speed",
            displayName = "Mouse Speed",
            values = listOf(
                "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
                "110", "120", "130", "140", "150", "160", "170", "180", "190",
                "200", "210", "220", "230", "240", "250", "260", "270", "280",
                "290", "300"
            ),
            defaultValue = "100",
            valueLabels = mapOf(
                "10" to "10%", "20" to "20%", "30" to "30%", "40" to "40%",
                "50" to "50%", "60" to "60%", "70" to "70%", "80" to "80%",
                "90" to "90%", "100" to "100%", "110" to "110%", "120" to "120%",
                "130" to "130%", "140" to "140%", "150" to "150%", "160" to "160%",
                "170" to "170%", "180" to "180%", "190" to "190%", "200" to "200%",
                "210" to "210%", "220" to "220%", "230" to "230%", "240" to "240%",
                "250" to "250%", "260" to "260%", "270" to "270%", "280" to "280%",
                "290" to "290%", "300" to "300%"
            )
        ),
        CoreOptionDef(
            key = "vice_physical_keyboard_pass_through",
            displayName = "Keyboard Pass-through",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Passes physical keyboard input directly to the emulated machine"
        ),
        CoreOptionDef(
            key = "vice_datasette_hotkeys",
            displayName = "Datasette Hotkeys",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables hotkey shortcuts for datasette tape deck controls"
        ),
        CoreOptionDef(
            key = "vice_keyrah_keypad_mappings",
            displayName = "Keyrah Keypad Mappings",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables Keyrah keyboard adapter numpad mappings"
        ),
        CoreOptionDef(
            key = "vice_keyboard_keymap",
            displayName = "Keyboard Keymap",
            values = listOf("positional", "symbolic", "positional-user", "symbolic-user"),
            defaultValue = "positional",
            description = "Selects how physical keyboard keys map to the C64 keyboard"
        ),
        CoreOptionDef(
            key = "vice_turbo_fire",
            displayName = "Turbo Fire",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables rapid-fire auto-repeat on the selected button"
        ),
        CoreOptionDef(
            key = "vice_turbo_fire_button",
            displayName = "Turbo Button",
            values = listOf("B", "A", "Y", "X", "L", "R", "L2", "R2"),
            defaultValue = "B",
            description = "Selects which gamepad button gets turbo fire functionality"
        ),
        CoreOptionDef(
            key = "vice_turbo_pulse",
            displayName = "Turbo Pulse",
            values = listOf("2", "4", "6", "8", "10", "12"),
            defaultValue = "6",
            description = "Sets the number of frames between each turbo fire press",
            valueLabels = mapOf(
                "2" to "2 frames", "4" to "4 frames", "6" to "6 frames",
                "8" to "8 frames", "10" to "10 frames", "12" to "12 frames"
            )
        ),
        CoreOptionDef(
            key = "vice_userport_joytype",
            displayName = "Userport Joystick Adapter",
            values = listOf("disabled", "CGA", "HIT", "Kingsoft", "Starbyte", "Synergy", "Hummer", "OEM", "PET"),
            defaultValue = "disabled",
            description = "Selects the userport joystick adapter type for additional players"
        ),
        CoreOptionDef(
            key = "vice_joyport",
            displayName = "Joystick Port",
            values = listOf("1", "2"),
            defaultValue = "2",
            description = "Selects which controller port the primary joystick is plugged into",
            valueLabels = mapOf("1" to "Port 1", "2" to "Port 2")
        ),
        CoreOptionDef(
            key = "vice_joyport_type",
            displayName = "Joystick Port Type",
            values = listOf(
                "1", "2", "2R", "3", "4", "5", "6", "7", "8", "9",
                "10", "11", "12", "13", "16", "14", "15"
            ),
            defaultValue = "1",
            description = "Selects the device plugged into the joystick port",
            valueLabels = mapOf(
                "1" to "Joystick", "2" to "Paddles (Split)", "2R" to "Paddles (Raw)",
                "3" to "Mouse (1351)", "4" to "Mouse (NEOS)", "5" to "Mouse (Amiga)",
                "6" to "Trackball (Atari CX-22)", "7" to "Mouse (Atari ST)",
                "8" to "Mouse (SmartMouse)", "9" to "Mouse (Micromys)", "10" to "Koalapad",
                "11" to "Light Pen (Up trigger)", "12" to "Light Pen (Left trigger)",
                "13" to "Light Pen (Datel)", "16" to "Light Pen (Inkwell)",
                "14" to "Light Gun (Magnum Light Phaser)", "15" to "Light Gun (Stack Light Rifle)"
            )
        ),
        CoreOptionDef(
            key = "vice_retropad_options",
            displayName = "RetroPad Face Button Options",
            values = listOf("disabled", "jump", "rotate", "rotate_jump"),
            defaultValue = "disabled",
            description = "Remaps face buttons for common joystick control schemes",
            valueLabels = mapOf(
                "disabled" to "Off", "jump" to "Jump", "rotate" to "Rotate",
                "rotate_jump" to "Rotate + Jump"
            )
        ),
        CoreOptionDef(
            key = "vice_mapping_options_display",
            displayName = "Show Mapping Options",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Reveals additional button mapping configuration options"
        ),
    )
}

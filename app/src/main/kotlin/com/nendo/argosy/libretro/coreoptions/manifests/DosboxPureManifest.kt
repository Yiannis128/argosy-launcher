package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object DosboxPureManifest : CoreOptionManifest {
    override val coreId = "dosbox_pure"
    override val options = listOf(
        CoreOptionDef(
            key = "dosbox_pure_force60fps",
            displayName = "Force Output FPS",
            values = listOf("false", "30", "50", "true", "70", "90", "120", "144", "240"),
            defaultValue = "false",
            description = "Forces video output at a fixed rate to reduce tearing or vsync issues",
            valueLabels = mapOf(
                "false" to "Off", "30" to "On (30 FPS)", "50" to "On (50 FPS)",
                "true" to "On (60 FPS)", "70" to "On (70 FPS)", "90" to "On (90 FPS)",
                "120" to "On (120 FPS)", "144" to "On (144 FPS)", "240" to "On (240 FPS)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_perfstats",
            displayName = "Show Performance Statistics",
            values = listOf("none", "simple", "detailed"),
            defaultValue = "none",
            description = "Displays emulation performance info on screen",
            valueLabels = mapOf(
                "none" to "Disabled", "simple" to "Simple", "detailed" to "Detailed information"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_savestate",
            displayName = "Save States Support",
            values = listOf("on", "rewind", "disabled"),
            defaultValue = "on",
            description = "Controls whether save states and rewind functionality are available",
            valueLabels = mapOf(
                "on" to "Enable save states",
                "rewind" to "Enable save states with rewind",
                "disabled" to "Disabled"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_conf",
            displayName = "Loading of dosbox.conf",
            values = listOf("false", "inside", "outside"),
            defaultValue = "false",
            description = "Controls whether DOSBox config files are loaded alongside the game",
            valueLabels = mapOf(
                "false" to "Disabled conf support (default)",
                "inside" to "Try 'dosbox.conf' in loaded content",
                "outside" to "Try '.conf' with same name as loaded content"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_menu_transparency",
            displayName = "Menu Transparency",
            values = listOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),
            defaultValue = "70",
            valueLabels = mapOf(
                "10" to "10%", "20" to "20%", "30" to "30%", "40" to "40%", "50" to "50%",
                "60" to "60%", "70" to "70%", "80" to "80%", "90" to "90%", "100" to "100%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_on_screen_keyboard",
            displayName = "Use L3 Button to Show Menu",
            values = listOf("true", "keyboard", "onlyosk", "false"),
            defaultValue = "true",
            description = "Binds the L3 button to show the menu and on-screen keyboard",
            valueLabels = mapOf(
                "true" to "On (Default to Menu)",
                "keyboard" to "On (Default to On-Screen Keyboard)",
                "onlyosk" to "On (Only On-Screen Keyboard While in Game)",
                "false" to "Off"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_mouse_wheel",
            displayName = "Bind Mouse Wheel To Key",
            values = listOf(
                "67/68", "72/71", "79/82", "78/81", "80/82", "64/65", "69/70",
                "99/100", "97/98", "84/85", "83/86", "11/13", "none"
            ),
            defaultValue = "67/68",
            description = "Maps mouse wheel scroll up/down to the selected keyboard keys",
            valueLabels = mapOf(
                "67/68" to "Left-Bracket/Right-Bracket", "72/71" to "Comma/Period",
                "79/82" to "Page-Up/Page-Down", "78/81" to "Home/End",
                "80/82" to "Delete/Page-Down", "64/65" to "Minus/Equals",
                "69/70" to "Semicolon/Quote", "99/100" to "Numpad Minus/Plus",
                "97/98" to "Numpad Divide/Multiply", "84/85" to "Up/Down",
                "83/86" to "Left/Right", "11/13" to "Q/E", "none" to "Disable"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_mouse_speed_factor",
            displayName = "Mouse Sensitivity",
            values = listOf(
                "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0",
                "1.2", "1.4", "1.6", "1.8", "2.0", "2.4", "2.8", "3.2", "3.6",
                "4.0", "4.4", "4.8", "5.0"
            ),
            defaultValue = "1.0",
            valueLabels = mapOf(
                "0.2" to "20%", "0.3" to "30%", "0.4" to "40%", "0.5" to "50%",
                "0.6" to "60%", "0.7" to "70%", "0.8" to "80%", "0.9" to "90%",
                "1.0" to "100%", "1.2" to "120%", "1.4" to "140%", "1.6" to "160%",
                "1.8" to "180%", "2.0" to "200%", "2.4" to "240%", "2.8" to "280%",
                "3.2" to "320%", "3.6" to "360%", "4.0" to "400%", "4.4" to "440%",
                "4.8" to "480%", "5.0" to "500%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_mouse_speed_factor_x",
            displayName = "Horizontal Mouse Sensitivity",
            values = listOf(
                "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0",
                "1.2", "1.4", "1.6", "1.8", "2.0", "2.4", "2.8", "3.2", "3.6",
                "4.0", "4.4", "4.8", "5.0"
            ),
            defaultValue = "1.0",
            description = "Adjusts mouse sensitivity for horizontal movement only",
            valueLabels = mapOf(
                "0.2" to "20%", "0.3" to "30%", "0.4" to "40%", "0.5" to "50%",
                "0.6" to "60%", "0.7" to "70%", "0.8" to "80%", "0.9" to "90%",
                "1.0" to "100%", "1.2" to "120%", "1.4" to "140%", "1.6" to "160%",
                "1.8" to "180%", "2.0" to "200%", "2.4" to "240%", "2.8" to "280%",
                "3.2" to "320%", "3.6" to "360%", "4.0" to "400%", "4.4" to "440%",
                "4.8" to "480%", "5.0" to "500%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_mouse_input",
            displayName = "Mouse Input Mode",
            values = listOf("true", "virtual", "direct", "pad", "false"),
            defaultValue = "true",
            description = "Selects how mouse and touchscreen input is handled",
            valueLabels = mapOf(
                "true" to "Auto (default)",
                "virtual" to "Virtual mouse movement",
                "direct" to "Direct controlled mouse (not supported by all games)",
                "pad" to "Touchpad mode (best for touch screens)",
                "false" to "Off (ignore mouse inputs)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_auto_mapping",
            displayName = "Automatic Game Pad Mappings",
            values = listOf("true", "notify", "false"),
            defaultValue = "true",
            description = "Automatically maps gamepad buttons to match the detected game",
            valueLabels = mapOf(
                "true" to "On (default)",
                "notify" to "Enable with notification on game detection",
                "false" to "Off"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_keyboard_layout",
            displayName = "Keyboard Layout",
            values = listOf(
                "us", "uk", "be", "br", "hr", "cz243", "dk", "su", "fr", "gr",
                "gk", "hu", "is161", "it", "nl", "no", "pl", "po", "ru", "sk",
                "si", "sp", "sv", "sg", "sf", "tr"
            ),
            defaultValue = "us",
            valueLabels = mapOf(
                "us" to "US (default)", "uk" to "UK", "be" to "Belgium", "br" to "Brazil",
                "hr" to "Croatia", "cz243" to "Czech Republic", "dk" to "Denmark",
                "su" to "Finland", "fr" to "France", "gr" to "Germany", "gk" to "Greece",
                "hu" to "Hungary", "is161" to "Iceland", "it" to "Italy",
                "nl" to "Netherlands", "no" to "Norway", "pl" to "Poland",
                "po" to "Portugal", "ru" to "Russia", "sk" to "Slovakia",
                "si" to "Slovenia", "sp" to "Spain", "sv" to "Sweden",
                "sg" to "Switzerland (German)", "sf" to "Switzerland (French)", "tr" to "Turkey"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_joystick_analog_deadzone",
            displayName = "Joystick Analog Deadzone",
            values = listOf("0", "5", "10", "15", "20", "25", "30", "35", "40"),
            defaultValue = "15",
            valueLabels = mapOf(
                "0" to "0%", "5" to "5%", "10" to "10%", "15" to "15%", "20" to "20%",
                "25" to "25%", "30" to "30%", "35" to "35%", "40" to "40%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_joystick_timed",
            displayName = "Enable Joystick Timed Intervals",
            values = listOf("true", "false"),
            defaultValue = "true",
            description = "Uses timed joystick axis intervals as on real DOS hardware",
            valueLabels = mapOf("true" to "On (default)", "false" to "Off")
        ),
        CoreOptionDef(
            key = "dosbox_pure_cycles",
            displayName = "Emulated Performance",
            values = listOf(
                "auto", "max", "315", "1320", "2750", "4720", "7800", "13400",
                "26800", "77000", "200000", "500000", "1000000"
            ),
            defaultValue = "auto",
            description = "Sets the emulated CPU speed",
            valueLabels = mapOf(
                "auto" to "AUTO (detect performance needs)",
                "max" to "MAX (emulate as fast as possible)",
                "315" to "8086/8088, 4.77 MHz (1980)",
                "1320" to "286, 6 MHz (1982)",
                "2750" to "286, 12.5 MHz (1985)",
                "4720" to "386, 20 MHz (1987)",
                "7800" to "386DX, 33 MHz (1989)",
                "13400" to "486DX, 33 MHz (1990)",
                "26800" to "486DX2, 66 MHz (1992)",
                "77000" to "Pentium, 100 MHz (1995)",
                "200000" to "Pentium II, 300 MHz (1997)",
                "500000" to "Pentium III, 600 MHz (1999)",
                "1000000" to "AMD Athlon, 1.2 GHz (2000)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_cycles_scale",
            displayName = "Performance Scale",
            values = listOf(
                "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0",
                "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0"
            ),
            defaultValue = "1.0",
            description = "Scales the emulated CPU speed up or down from the base setting",
            valueLabels = mapOf(
                "0.2" to "20%", "0.3" to "30%", "0.4" to "40%", "0.5" to "50%",
                "0.6" to "60%", "0.7" to "70%", "0.8" to "80%", "0.9" to "90%",
                "1.0" to "100%", "1.1" to "110%", "1.2" to "120%", "1.3" to "130%",
                "1.4" to "140%", "1.5" to "150%", "1.6" to "160%", "1.7" to "170%",
                "1.8" to "180%", "1.9" to "190%", "2.0" to "200%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_cycle_limit",
            displayName = "Limit CPU Usage",
            values = listOf(
                "0.5", "0.55", "0.6", "0.65", "0.7", "0.75", "0.8", "0.85",
                "0.9", "0.95", "1.0"
            ),
            defaultValue = "1.0",
            description = "Caps the host CPU time the emulator is allowed to use",
            valueLabels = mapOf(
                "0.5" to "50%", "0.55" to "55%", "0.6" to "60%", "0.65" to "65%",
                "0.7" to "70%", "0.75" to "75%", "0.8" to "80%", "0.85" to "85%",
                "0.9" to "90%", "0.95" to "95%", "1.0" to "100%"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_machine",
            displayName = "Emulated Graphics Chip",
            values = listOf("svga", "vga", "ega", "cga", "tandy", "hercules", "pcjr"),
            defaultValue = "svga",
            description = "Selects the emulated graphics hardware for compatibility",
            valueLabels = mapOf(
                "svga" to "SVGA (default)", "vga" to "VGA", "ega" to "EGA",
                "cga" to "CGA", "tandy" to "Tandy", "hercules" to "Hercules", "pcjr" to "PCjr"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_cga",
            displayName = "CGA Mode",
            values = listOf(
                "early_auto", "early_on", "early_off", "late_auto", "late_on", "late_off"
            ),
            defaultValue = "early_auto",
            description = "Selects the CGA hardware revision and composite mode behavior",
            valueLabels = mapOf(
                "early_auto" to "Early model, composite mode auto (default)",
                "early_on" to "Early model, composite mode on",
                "early_off" to "Early model, composite mode off",
                "late_auto" to "Late model, composite mode auto",
                "late_on" to "Late model, composite mode on",
                "late_off" to "Late model, composite mode off"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_hercules",
            displayName = "Hercules Color Mode",
            values = listOf("white", "amber", "green"),
            defaultValue = "white",
            description = "Sets the monochrome tint for Hercules graphics emulation",
            valueLabels = mapOf(
                "white" to "Black & white (default)",
                "amber" to "Black & amber",
                "green" to "Black & green"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_svga",
            displayName = "SVGA Mode",
            values = listOf(
                "svga_s3", "vesa_nolfb", "vesa_oldvbe", "svga_et3000",
                "svga_et4000", "svga_paradise"
            ),
            defaultValue = "svga_s3",
            description = "Selects the SVGA chipset to emulate for high-resolution modes",
            valueLabels = mapOf(
                "svga_s3" to "S3 Trio64 (default)",
                "vesa_nolfb" to "S3 Trio64 no-line buffer hack",
                "vesa_oldvbe" to "S3 Trio64 VESA 1.3",
                "svga_et3000" to "Tseng Labs ET3000",
                "svga_et4000" to "Tseng Labs ET4000",
                "svga_paradise" to "Paradise PVGA1A"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_aspect_correction",
            displayName = "Aspect Ratio Correction",
            values = listOf("false", "true", "doublescan", "padded", "padded-doublescan"),
            defaultValue = "false",
            description = "Stretches the image to correct for non-square pixels",
            valueLabels = mapOf(
                "false" to "Off (default)",
                "true" to "On (single-scan)",
                "doublescan" to "On (double-scan when applicable)",
                "padded" to "Padded to 4:3 (single-scan)",
                "padded-doublescan" to "Padded to 4:3 (double-scan when applicable)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_memory_size",
            displayName = "Memory Size",
            values = listOf(
                "none", "4", "8", "16", "24", "32", "48", "64", "96", "128",
                "224", "256", "512", "1024"
            ),
            defaultValue = "16",
            description = "Sets the amount of emulated system memory available to DOS",
            valueLabels = mapOf(
                "none" to "Disable extended memory", "4" to "4 MB", "8" to "8 MB",
                "16" to "16 MB (default)", "24" to "24 MB", "32" to "32 MB",
                "48" to "48 MB", "64" to "64 MB", "96" to "96 MB", "128" to "128 MB",
                "224" to "224 MB", "256" to "256 MB", "512" to "512 MB", "1024" to "1024 MB"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_cpu_type",
            displayName = "CPU Type",
            values = listOf("auto", "386", "386_slow", "386_prefetch", "486_slow", "pentium_slow"),
            defaultValue = "auto",
            description = "Selects which CPU instruction set and behavior to emulate",
            valueLabels = mapOf(
                "auto" to "Auto", "386" to "386", "386_slow" to "386 (slow)",
                "386_prefetch" to "386 (prefetch)", "486_slow" to "486 (slow)",
                "pentium_slow" to "Pentium (slow)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_cpu_core",
            displayName = "CPU Core",
            values = listOf("auto", "dynamic", "normal", "simple"),
            defaultValue = "auto",
            description = "Selects the CPU emulation method balancing speed and compatibility",
            valueLabels = mapOf(
                "auto" to "Auto", "dynamic" to "Dynamic (recompiler)",
                "normal" to "Normal (interpreter)",
                "simple" to "Simple (interpreter)"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_audiorate",
            displayName = "Audio Sample Rate",
            values = listOf("48000", "44100", "32000", "22050", "16000", "11025", "8000", "49716"),
            defaultValue = "48000",
            valueLabels = mapOf(
                "8000" to "8 kHz", "11025" to "11 kHz", "16000" to "16 kHz",
                "22050" to "22 kHz", "32000" to "32 kHz",
                "44100" to "44.1 kHz", "48000" to "48 kHz", "49716" to "49.7 kHz"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_sblaster_type",
            displayName = "SoundBlaster Type",
            values = listOf("sb16", "sbpro2", "sbpro1", "sb2", "sb1", "gb", "none"),
            defaultValue = "sb16",
            description = "Selects the Sound Blaster model to emulate for audio compatibility",
            valueLabels = mapOf(
                "sb16" to "SoundBlaster 16 (default)", "sbpro2" to "SoundBlaster Pro 2",
                "sbpro1" to "SoundBlaster Pro", "sb2" to "SoundBlaster 2.0",
                "sb1" to "SoundBlaster 1.0", "gb" to "GameBlaster", "none" to "none"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_sblaster_adlib_mode",
            displayName = "SoundBlaster Adlib/FM Mode",
            values = listOf("auto", "cms", "opl2", "dualopl2", "opl3", "opl3gold", "none"),
            defaultValue = "auto",
            description = "Selects the FM synthesis chip to emulate for music output",
            valueLabels = mapOf(
                "auto" to "Auto (default)", "cms" to "CMS", "opl2" to "OPL-2",
                "dualopl2" to "Dual OPL-2", "opl3" to "OPL-3", "opl3gold" to "OPL-3 Gold",
                "none" to "Disabled"
            )
        ),
        CoreOptionDef(
            key = "dosbox_pure_sblaster_adlib_emu",
            displayName = "SoundBlaster Adlib Provider",
            values = listOf("default", "nuked"),
            defaultValue = "default",
            description = "Selects the OPL emulation library used for FM synthesis",
            valueLabels = mapOf("default" to "Default", "nuked" to "High quality Nuked OPL3")
        ),
        CoreOptionDef(
            key = "dosbox_pure_gus",
            displayName = "Enable Gravis Ultrasound",
            values = listOf("false", "true"),
            defaultValue = "false",
            description = "Emulates a Gravis Ultrasound card for games that support it",
            valueLabels = mapOf("false" to "Off (default)", "true" to "On")
        ),
    )
}

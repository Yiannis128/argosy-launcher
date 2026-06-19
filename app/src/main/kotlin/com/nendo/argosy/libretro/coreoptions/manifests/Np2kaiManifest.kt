package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object Np2kaiManifest : CoreOptionManifest {
    override val coreId = "np2kai"
    override val options = listOf(
        CoreOptionDef(
            key = "np2kai_drive",
            displayName = "Swap Disks on Drive",
            values = listOf("FDD1", "FDD2"),
            defaultValue = "FDD2"
        ),
        CoreOptionDef(
            key = "np2kai_keyboard",
            displayName = "Keyboard (Restart)",
            values = listOf("Ja", "Us"),
            defaultValue = "Ja",
            description = "Selects between Japanese or US keyboard layout"
        ),
        CoreOptionDef(
            key = "np2kai_keyrepeat",
            displayName = "Key-repeat",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Repeats a key while it is held down"
        ),
        CoreOptionDef(
            key = "np2kai_keyrepeat_delay",
            displayName = "Key-repeat Delay",
            values = listOf("250ms", "500ms", "1000ms"),
            defaultValue = "500ms",
            description = "Sets the delay before key-repeat starts"
        ),
        CoreOptionDef(
            key = "np2kai_keyrepeat_interval",
            displayName = "Key-repeat Interval",
            values = listOf("25ms", "50ms", "100ms"),
            defaultValue = "50ms",
            description = "Sets the interval between repeated key presses"
        ),
        CoreOptionDef(
            key = "np2kai_model",
            displayName = "PC Model",
            values = listOf("PC-286", "PC-9801VM", "PC-9801VX"),
            defaultValue = "PC-9801VX",
            description = "Selects which PC-98 hardware model to emulate"
        ),
        CoreOptionDef(
            key = "np2kai_clk_base",
            displayName = "CPU Base Clock",
            values = listOf("1.9968 MHz", "2.4576 MHz"),
            defaultValue = "2.4576 MHz",
            description = "Sets the base CPU clock frequency"
        ),
        CoreOptionDef(
            key = "np2kai_cpu_feature",
            displayName = "CPU Feature (Restart)",
            values = listOf(
                "(custom)", "Intel 80386", "Intel i486SX", "Intel i486DX",
                "Intel Pentium", "Intel MMX Pentium", "Intel Pentium Pro",
                "Intel Pentium II", "Intel Pentium III", "Intel Pentium M",
                "Intel Pentium 4", "AMD K6-2", "AMD K6-III", "AMD K7 Athlon",
                "AMD K7 Athlon XP", "Neko Processor II"
            ),
            defaultValue = "Intel 80386",
            description = "Selects the emulated CPU instruction set and feature level"
        ),
        CoreOptionDef(
            key = "np2kai_clk_mult",
            displayName = "CPU Clock Multiplier",
            values = listOf(
                "2", "4", "5", "6", "8", "10", "12", "16", "20", "24",
                "30", "36", "40", "42", "52", "64", "76", "88", "100"
            ),
            defaultValue = "4",
            description = "Multiplies the base clock to set overall CPU speed",
            valueLabels = mapOf(
                "2" to "2x", "4" to "4x", "5" to "5x", "6" to "6x", "8" to "8x",
                "10" to "10x", "12" to "12x", "16" to "16x", "20" to "20x",
                "24" to "24x", "30" to "30x", "36" to "36x", "40" to "40x",
                "42" to "42x", "52" to "52x", "64" to "64x", "76" to "76x",
                "88" to "88x", "100" to "100x"
            )
        ),
        CoreOptionDef(
            key = "np2kai_ExMemory",
            displayName = "RAM Size",
            values = listOf("1", "3", "7", "11", "13", "16", "32", "64", "120", "230", "512", "1024"),
            defaultValue = "3",
            description = "Sets the amount of extended memory in megabytes",
            valueLabels = mapOf(
                "1" to "1 MB", "3" to "3 MB", "7" to "7 MB", "11" to "11 MB",
                "13" to "13 MB", "16" to "16 MB", "32" to "32 MB", "64" to "64 MB",
                "120" to "120 MB", "230" to "230 MB", "512" to "512 MB", "1024" to "1024 MB"
            )
        ),
        CoreOptionDef(
            key = "np2kai_FastMC",
            displayName = "Fast Memcheck",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Performs a faster memory check at startup"
        ),
        CoreOptionDef(
            key = "np2kai_uselasthddmount",
            displayName = "Use Last HDD Mount",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Restores the previous HDD mount at core start"
        ),
        CoreOptionDef(
            key = "np2kai_gdc",
            displayName = "GDC",
            values = listOf("uPD7220", "uPD72020"),
            defaultValue = "uPD7220",
            description = "Selects the graphics display controller chip to emulate"
        ),
        CoreOptionDef(
            key = "np2kai_skipline",
            displayName = "Skipline Revisions",
            values = listOf("Full 255 lines", "OFF", "ON"),
            defaultValue = "Full 255 lines",
            description = "Controls scanline rendering mode for display output"
        ),
        CoreOptionDef(
            key = "np2kai_realpal",
            displayName = "Real Palettes",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Uses more accurate palette color emulation"
        ),
        CoreOptionDef(
            key = "np2kai_lcd",
            displayName = "LCD",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Simulates an LCD display with reduced color output"
        ),
        CoreOptionDef(
            key = "np2kai_SNDboard",
            displayName = "Sound Board",
            values = listOf(
                "PC9801-14", "PC9801-86", "PC9801-86 + 118(B460)",
                "PC9801-86 + Mate-X PCM(B460)", "PC9801-86 + Chibi-oto",
                "PC9801-86 + Speak Board", "PC9801-26K", "PC9801-26K + 86",
                "PC9801-118", "Mate-X PCM", "Chibi-oto", "Speak Board",
                "Spark Board", "Sound Orchestra", "Sound Orchestra-V",
                "Little Orchestra L", "Multimedia Orchestra", "Sound Blaster 16",
                "PC9801-86 + Sound Blaster 16", "Mate-X PCM + Sound Blaster 16",
                "PC9801-118 + Sound Blaster 16",
                "PC9801-86 + Mate-X PCM(B460) + Sound Blaster 16",
                "PC9801-86 + 118(B460) + Sound Blaster 16", "AMD-98", "WaveStar",
                "Otomi-chanx2", "Otomi-chanx2 + 86", "None"
            ),
            defaultValue = "PC9801-86",
            description = "Selects the sound board hardware to emulate"
        ),
        CoreOptionDef(
            key = "np2kai_118ROM",
            displayName = "Enable 118 ROM",
            values = listOf("OFF", "ON"),
            defaultValue = "ON",
            description = "Enables the PC-9801-118 sound ROM"
        ),
        CoreOptionDef(
            key = "np2kai_jast_snd",
            displayName = "JastSound",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Enables JastSound PCM audio expansion emulation"
        ),
        CoreOptionDef(
            key = "np2kai_xroll",
            displayName = "Swap PageUp/PageDown",
            values = listOf("OFF", "ON"),
            defaultValue = "ON",
            description = "Swaps the PageUp and PageDown key mappings"
        ),
        CoreOptionDef(
            key = "np2kai_usefmgen",
            displayName = "Sound Generator",
            values = listOf("Default", "fmgen"),
            defaultValue = "fmgen",
            description = "Selects the FM synthesis engine used for sound generation"
        ),
        CoreOptionDef(
            key = "np2kai_volume_M",
            displayName = "Volume Master",
            values = (0..100 step 5).map { it.toString() },
            defaultValue = "100"
        ),
        CoreOptionDef(
            key = "np2kai_volume_F",
            displayName = "Volume FM",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "64"
        ),
        CoreOptionDef(
            key = "np2kai_volume_S",
            displayName = "Volume SSG",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "28"
        ),
        CoreOptionDef(
            key = "np2kai_volume_A",
            displayName = "Volume ADPCM",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "64"
        ),
        CoreOptionDef(
            key = "np2kai_volume_P",
            displayName = "Volume PCM",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "92"
        ),
        CoreOptionDef(
            key = "np2kai_volume_R",
            displayName = "Volume RHYTHM",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "64"
        ),
        CoreOptionDef(
            key = "np2kai_volume_C",
            displayName = "Volume CD-DA",
            values = listOf(
                "0", "8", "16", "24", "32", "40", "48", "56", "64", "72", "80",
                "88", "96", "104", "112", "120", "128", "136", "144", "154",
                "160", "168", "196", "184", "192", "200", "208", "216", "224",
                "232", "240", "248", "255"
            ),
            defaultValue = "128"
        ),
        CoreOptionDef(
            key = "np2kai_Seek_Snd",
            displayName = "Floppy Seek Sound",
            values = listOf("OFF", "ON"),
            defaultValue = "OFF",
            description = "Plays floppy disk drive sound effects during disk access"
        ),
        CoreOptionDef(
            key = "np2kai_Seek_Vol",
            displayName = "Volume Floppy Seek",
            values = (0..128 step 4).map { it.toString() },
            defaultValue = "64"
        ),
        CoreOptionDef(
            key = "np2kai_BEEP_vol",
            displayName = "Volume Beep",
            values = listOf("0", "1", "2", "3"),
            defaultValue = "3",
            valueLabels = mapOf("0" to "Off", "1" to "Low", "2" to "Medium", "3" to "High")
        ),
        CoreOptionDef(
            key = "np2kai_usecdecc",
            displayName = "Use CD-ROM EDC/ECC Emulation",
            values = listOf("OFF", "ON"),
            defaultValue = "ON",
            description = "Emulates CD-ROM error detection and correction codes"
        ),
        CoreOptionDef(
            key = "np2kai_inputmouse",
            displayName = "Use Mouse Input",
            values = listOf("OFF", "ON"),
            defaultValue = "ON",
            description = "Uses physical mouse or touchpanel for mouse input"
        ),
        CoreOptionDef(
            key = "np2kai_stick2mouse",
            displayName = "Analog Stick to Mouse Mapping",
            values = listOf("OFF", "L-stick", "R-stick"),
            defaultValue = "R-stick",
            description = "Maps a gamepad analog stick to mouse cursor movement"
        ),
        CoreOptionDef(
            key = "np2kai_stick2mouse_shift",
            displayName = "Stick-to-Mouse Click Shift Button",
            values = listOf("OFF", "L1", "L2", "R1", "R2"),
            defaultValue = "R1",
            description = "Shoulder button that shifts the stick click from left to right"
        ),
        CoreOptionDef(
            key = "np2kai_joymode",
            displayName = "D-pad Mapping Mode",
            values = listOf(
                "OFF", "Mouse", "Arrows", "Arrows 3button", "Keypad",
                "Keypad 3button", "Manual Keyboard", "Atari Joypad", "Keypad Fighting"
            ),
            defaultValue = "OFF",
            description = "Maps the gamepad d-pad to mouse, keyboard, or joypad input"
        ),
        CoreOptionDef(
            key = "np2kai_joynp2menu",
            displayName = "Open NP2 Menu Button",
            values = listOf(
                "OFF", "L1", "L2", "L3", "R1", "R2", "R3",
                "A", "B", "X", "Y", "Start", "Select"
            ),
            defaultValue = "L2",
            description = "Selects the gamepad button that opens the NP2 menu"
        ),
    )
}

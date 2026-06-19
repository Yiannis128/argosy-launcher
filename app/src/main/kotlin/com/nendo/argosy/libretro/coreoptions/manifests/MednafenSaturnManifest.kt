package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object MednafenSaturnManifest : CoreOptionManifest {
    override val coreId = "mednafen_saturn"
    override val options = listOf(
        CoreOptionDef(
            key = "beetle_saturn_region",
            displayName = "System Region",
            values = listOf(
                "Auto Detect", "Japan", "North America", "Europe",
                "South Korea", "Asia (NTSC)", "Asia (PAL)", "Brazil", "Latin America"
            ),
            defaultValue = "Auto Detect"
        ),
        CoreOptionDef(
            key = "beetle_saturn_cart",
            displayName = "Cartridge",
            values = listOf(
                "Auto Detect", "None", "Backup Memory", "Extended RAM (1MB)",
                "Extended RAM (4MB)", "The King of Fighters '95",
                "Ultraman: Hikari no Kyojin Densetsu"
            ),
            defaultValue = "Auto Detect",
            description = "Selects the expansion cartridge inserted in the Saturn's cartridge slot"
        ),
        CoreOptionDef(
            key = "beetle_saturn_shared_int",
            displayName = "Shared Internal Memory (Restart)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Shares internal backup memory across all games instead of per-game"
        ),
        CoreOptionDef(
            key = "beetle_saturn_shared_ext",
            displayName = "Shared Backup Memory (Restart)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Shares cartridge backup memory across all games instead of per-game"
        ),
        CoreOptionDef(
            key = "beetle_saturn_save_method",
            displayName = "Save Method (Restart)",
            values = listOf("libretro", "mednafen"),
            defaultValue = "libretro",
            description = "Format used to persist internal backup RAM",
            valueLabels = mapOf("libretro" to "Libretro", "mednafen" to "Mednafen")
        ),
        CoreOptionDef(
            key = "beetle_saturn_multitap_port1",
            displayName = "6Player Adaptor on Port 1",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables the 6-player multitap adapter on controller port 1"
        ),
        CoreOptionDef(
            key = "beetle_saturn_multitap_port2",
            displayName = "6Player Adaptor on Port 2",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Enables the 6-player multitap adapter on controller port 2"
        ),
        CoreOptionDef(
            key = "beetle_saturn_opposite_directions",
            displayName = "Allow Opposite Directions",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Allows pressing up and down or left and right at the same time"
        ),
        CoreOptionDef(
            key = "beetle_saturn_analog_stick_deadzone",
            displayName = "Analog Stick Deadzone",
            values = listOf("0%", "5%", "10%", "15%", "20%", "25%", "30%"),
            defaultValue = "0%"
        ),
        CoreOptionDef(
            key = "beetle_saturn_trigger_deadzone",
            displayName = "Trigger Deadzone",
            values = listOf("0%", "5%", "10%", "15%", "20%", "25%", "30%"),
            defaultValue = "0%"
        ),
        CoreOptionDef(
            key = "beetle_saturn_mouse_sensitivity",
            displayName = "Mouse Sensitivity",
            values = listOf(
                "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%",
                "105%", "110%", "115%", "120%", "125%", "130%", "135%", "140%", "145%", "150%",
                "155%", "160%", "165%", "170%", "175%", "180%", "185%", "190%", "195%", "200%"
            ),
            defaultValue = "100%"
        ),
        CoreOptionDef(
            key = "beetle_saturn_virtuagun_input",
            displayName = "Gun Input Mode",
            values = listOf("Lightgun", "Touchscreen"),
            defaultValue = "Lightgun",
            description = "Selects the input source used for the light gun"
        ),
        CoreOptionDef(
            key = "beetle_saturn_virtuagun_crosshair",
            displayName = "Gun Crosshair",
            values = listOf("Off", "Cross", "Dot"),
            defaultValue = "Cross",
            description = "Selects the crosshair style shown when using the Virtua Gun",
            valueLabels = mapOf("Off" to "Disabled")
        ),
        CoreOptionDef(
            key = "beetle_saturn_crosshair_color_p1",
            displayName = "Gun Crosshair Color: Port 1",
            values = listOf(
                "red", "blue", "green", "orange", "yellow",
                "cyan", "pink", "purple", "black", "white"
            ),
            defaultValue = "red",
            valueLabels = mapOf(
                "red" to "Red", "blue" to "Blue", "green" to "Green", "orange" to "Orange",
                "yellow" to "Yellow", "cyan" to "Cyan", "pink" to "Pink", "purple" to "Purple",
                "black" to "Black", "white" to "White"
            )
        ),
        CoreOptionDef(
            key = "beetle_saturn_crosshair_color_p2",
            displayName = "Gun Crosshair Color: Port 2",
            values = listOf(
                "blue", "red", "green", "orange", "yellow",
                "cyan", "pink", "purple", "black", "white"
            ),
            defaultValue = "blue",
            valueLabels = mapOf(
                "red" to "Red", "blue" to "Blue", "green" to "Green", "orange" to "Orange",
                "yellow" to "Yellow", "cyan" to "Cyan", "pink" to "Pink", "purple" to "Purple",
                "black" to "Black", "white" to "White"
            )
        ),
        CoreOptionDef(
            key = "beetle_saturn_cdimagecache",
            displayName = "CD Image Cache (Restart)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Loads the entire disc image into memory for faster access"
        ),
        CoreOptionDef(
            key = "beetle_saturn_midsync",
            displayName = "Mid-frame Input Synchronization",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Synchronizes input mid-frame for reduced latency at a performance cost"
        ),
        CoreOptionDef(
            key = "beetle_saturn_autortc",
            displayName = "Automatically Set RTC on Game Load",
            values = listOf("disabled", "enabled"),
            defaultValue = "enabled",
            description = "Syncs the emulated real-time clock to the host system time on load"
        ),
        CoreOptionDef(
            key = "beetle_saturn_autortc_lang",
            displayName = "BIOS Language",
            values = listOf("english", "german", "french", "spanish", "italian", "japanese"),
            defaultValue = "english",
            description = "Also affects the language used by some games",
            valueLabels = mapOf(
                "english" to "English", "german" to "German", "french" to "French",
                "spanish" to "Spanish", "italian" to "Italian", "japanese" to "Japanese"
            )
        ),
        CoreOptionDef(
            key = "beetle_saturn_horizontal_overscan",
            displayName = "Horizontal Overscan Mask",
            values = (0..30).map { (it * 2).toString() },
            defaultValue = "0",
            description = "Sets how many pixels to crop from the left and right screen edges",
            valueLabels = mapOf("0" to "Disabled")
        ),
        CoreOptionDef(
            key = "beetle_saturn_initial_scanline",
            displayName = "Initial Scanline",
            values = (0..40).map { it.toString() },
            defaultValue = "8",
            description = "Sets the first visible scanline for NTSC output"
        ),
        CoreOptionDef(
            key = "beetle_saturn_last_scanline",
            displayName = "Last Scanline",
            values = (210..239).map { it.toString() },
            defaultValue = "231",
            description = "Sets the last visible scanline for NTSC output"
        ),
        CoreOptionDef(
            key = "beetle_saturn_initial_scanline_pal",
            displayName = "Initial Scanline PAL",
            values = (0..60).map { it.toString() },
            defaultValue = "16",
            description = "Sets the first visible scanline for PAL output"
        ),
        CoreOptionDef(
            key = "beetle_saturn_last_scanline_pal",
            displayName = "Last Scanline PAL",
            values = (230..287).map { it.toString() },
            defaultValue = "271",
            description = "Sets the last visible scanline for PAL output"
        ),
        CoreOptionDef(
            key = "beetle_saturn_horizontal_blend",
            displayName = "Enable Horizontal Blend (Blur)",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Blends adjacent horizontal pixels to simulate composite video blur"
        ),
        CoreOptionDef(
            key = "beetle_saturn_deinterlacer",
            displayName = "Deinterlace Method",
            values = listOf("weave", "bob", "bob_offset", "fastmad", "off"),
            defaultValue = "weave",
            description = "Selects how the two fields of an interlaced signal are combined",
            valueLabels = mapOf(
                "weave" to "Weave", "bob" to "Bob", "bob_offset" to "Bob (Offset)",
                "fastmad" to "FastMAD (Motion Adaptive)", "off" to "Off"
            )
        ),
        CoreOptionDef(
            key = "beetle_saturn_mesh_transparency",
            displayName = "Improved Mesh Transparencies",
            values = listOf("disabled", "enabled"),
            defaultValue = "disabled",
            description = "Replaces VDP1 mesh stipple with a 50% blend for smoother transparencies"
        ),
    )
}

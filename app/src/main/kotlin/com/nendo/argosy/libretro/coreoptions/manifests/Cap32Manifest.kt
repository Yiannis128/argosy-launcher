package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object Cap32Manifest : CoreOptionManifest {
    override val coreId = "cap32"
    override val options = listOf(
        CoreOptionDef(
            key = "cap32_retrojoy0",
            displayName = "User 1 Controller Config",
            values = listOf("auto", "qaop", "incentive", "joystick_port1"),
            defaultValue = "auto",
            description = "Selects the keyboard-to-joystick mapping scheme for player 1"
        ),
        CoreOptionDef(
            key = "cap32_retrojoy1",
            displayName = "User 2 Controller Config",
            values = listOf("auto", "qaop", "incentive", "joystick_port1", "joystick_port2"),
            defaultValue = "auto",
            description = "Selects the keyboard-to-joystick mapping scheme for player 2"
        ),
        CoreOptionDef(
            key = "cap32_combokey",
            displayName = "Combo Key",
            values = listOf("select", "y", "b", "disabled"),
            defaultValue = "select",
            description = "Sets which gamepad button opens the virtual keyboard"
        ),
        CoreOptionDef(
            key = "cap32_db_mapkeys",
            displayName = "Use Internal Remap DB",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Applies the bundled per-game key remapping database"
        ),
        CoreOptionDef(
            key = "cap32_lightgun_input",
            displayName = "Light Gun Input",
            values = listOf("disabled", "phaser", "gunstick"),
            defaultValue = "disabled",
            description = "Selects the emulated light gun peripheral"
        ),
        CoreOptionDef(
            key = "cap32_lightgun_show",
            displayName = "Light Gun Crosshair",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Shows a crosshair at the light gun aim position"
        ),
        CoreOptionDef(
            key = "cap32_model",
            displayName = "Model",
            values = listOf("464", "664", "6128", "6128+ (experimental)"),
            defaultValue = "6128",
            description = "Selects which Amstrad CPC model to emulate"
        ),
        CoreOptionDef(
            key = "cap32_autorun",
            displayName = "Autorun",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Automatically load and run the first program on the inserted disk"
        ),
        CoreOptionDef(
            key = "cap32_ram",
            displayName = "RAM Size",
            values = listOf("64", "128", "192", "576"),
            defaultValue = "128",
            description = "Sets the amount of emulated RAM in kilobytes"
        ),
        CoreOptionDef(
            key = "cap32_advanced_green_phosphor",
            displayName = "Green Phosphor Blueish",
            values = listOf("5", "10", "15", "20", "30"),
            defaultValue = "15",
            description = "Adjusts the blue tint of the green phosphor monitor"
        ),
        CoreOptionDef(
            key = "cap32_scr_intensity",
            displayName = "Monitor Intensity",
            values = listOf("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"),
            defaultValue = "8",
            description = "Adjusts the brightness level of the emulated monitor"
        ),
        CoreOptionDef(
            key = "cap32_gfx_colors",
            displayName = "Color Depth",
            values = listOf("8bit", "16bit", "24bit"),
            defaultValue = "16bit",
            valueLabels = mapOf("8bit" to "8-bit", "16bit" to "16-bit", "24bit" to "24-bit")
        ),
        CoreOptionDef(
            key = "cap32_scr_crop",
            displayName = "Crop Screen Borders",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Removes the border area around the active display"
        ),
        CoreOptionDef(
            key = "cap32_statusbar",
            displayName = "Status Bar",
            values = listOf("onloading", "enabled", "disabled"),
            defaultValue = "onloading",
            description = "Controls when the disk activity status bar is shown"
        ),
        CoreOptionDef(
            key = "cap32_keyboard_transparency",
            displayName = "Keyboard Transparency",
            values = listOf("enabled", "disabled"),
            defaultValue = "disabled",
            description = "Makes the on-screen keyboard overlay semi-transparent"
        ),
        CoreOptionDef(
            key = "cap32_floppy_sound",
            displayName = "Floppy Sound",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Plays floppy disk drive sound effects during disk access"
        ),
        CoreOptionDef(
            key = "cap32_scr_tube",
            displayName = "Monitor Type",
            values = listOf("color", "green", "white"),
            defaultValue = "color",
            description = "Simulates a color, green phosphor, or white phosphor monitor"
        ),
        CoreOptionDef(
            key = "cap32_lang_layout",
            displayName = "CPC Language",
            values = listOf("english", "french", "spanish"),
            defaultValue = "english",
            description = "Sets the keyboard layout language for the emulated CPC"
        ),
    )
}

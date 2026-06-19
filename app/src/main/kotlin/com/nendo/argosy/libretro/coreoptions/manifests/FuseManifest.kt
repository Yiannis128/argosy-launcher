package com.nendo.argosy.libretro.coreoptions.manifests

import com.nendo.argosy.libretro.coreoptions.CoreOptionDef
import com.nendo.argosy.libretro.coreoptions.CoreOptionManifest

object FuseManifest : CoreOptionManifest {
    override val coreId = "fuse"
    override val options = listOf(
        CoreOptionDef(
            key = "fuse_machine",
            displayName = "Model",
            values = listOf(
                "Spectrum 48K", "Spectrum 48K (NTSC)", "Spectrum 128K",
                "Spectrum +2", "Spectrum +2A", "Spectrum +3", "Spectrum +3e",
                "Spectrum SE", "Timex TC2048", "Timex TC2068", "Timex TS2068",
                "Spectrum 16K", "Pentagon 128K", "Pentagon 512K", "Pentagon 1024",
                "Scorpion 256K"
            ),
            defaultValue = "Spectrum 48K",
            description = "Selects which ZX Spectrum hardware model to emulate"
        ),
        CoreOptionDef(
            key = "fuse_emulation_speed",
            displayName = "Emulation Speed",
            values = listOf("50", "100", "150", "200", "300"),
            defaultValue = "100",
            description = "Runs emulation at the selected percentage of normal speed",
            valueLabels = mapOf(
                "50" to "50%", "100" to "100%", "150" to "150%",
                "200" to "200%", "300" to "300%"
            )
        ),
        CoreOptionDef(
            key = "fuse_size_border",
            displayName = "Size Video Border",
            values = listOf("full", "medium", "small", "minimum", "none"),
            defaultValue = "full",
            description = "Selects how much of the colored screen border to show"
        ),
        CoreOptionDef(
            key = "fuse_palette",
            displayName = "Colour Palette",
            values = listOf(
                "Fuse Standard", "ZX Standard", "B&W TV", "Green Monochrome",
                "Ambar Monochrome", "C64", "CGA 4 colours", "CGA 8 colours",
                "CGA 16 colours", "Inverted colours"
            ),
            defaultValue = "Fuse Standard",
            description = "Selects the color palette used for the display"
        ),
        CoreOptionDef(
            key = "fuse_auto_load",
            displayName = "Tape Auto Load",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Automatically starts loading tape content"
        ),
        CoreOptionDef(
            key = "fuse_fast_load",
            displayName = "Tape Fast Load",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Accelerates tape loading to skip real-time wait"
        ),
        CoreOptionDef(
            key = "fuse_load_sound",
            displayName = "Tape Load Sound",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Plays the cassette loading sounds during tape operations"
        ),
        CoreOptionDef(
            key = "fuse_speaker_type",
            displayName = "Speaker Type",
            values = listOf("tv speaker", "beeper", "unfiltered"),
            defaultValue = "tv speaker",
            description = "Selects the audio output filter to simulate different speakers"
        ),
        CoreOptionDef(
            key = "fuse_ay_stereo_separation",
            displayName = "AY Stereo Separation",
            values = listOf("none", "acb", "abc"),
            defaultValue = "none",
            description = "Sets the stereo channel layout for the AY sound chip"
        ),
        CoreOptionDef(
            key = "fuse_key_ovrlay_transp",
            displayName = "Transparent Keyboard Overlay",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Makes the on-screen keyboard overlay semi-transparent"
        ),
        CoreOptionDef(
            key = "fuse_key_hold_time",
            displayName = "Time to Release Key in ms",
            values = listOf("100", "300", "500", "1000"),
            defaultValue = "500",
            description = "Sets how long a virtual key press is held before releasing"
        ),
        CoreOptionDef(
            key = "fuse_display_joystick_type",
            displayName = "Show Joystick Type at Startup",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Displays joystick type and emulation speed when content loads"
        ),
        CoreOptionDef(
            key = "fuse_auto_size_savestate",
            displayName = "Auto Size Savestates",
            values = listOf("enabled", "disabled"),
            defaultValue = "enabled",
            description = "Sizes save states automatically; disable for netplay"
        ),
    )
}

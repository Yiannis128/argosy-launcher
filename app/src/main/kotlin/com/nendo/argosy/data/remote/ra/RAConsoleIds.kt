package com.nendo.argosy.data.remote.ra

import com.nendo.argosy.data.platform.PlatformDefinitions

object RAConsoleIds {

    private val slugToConsoleId: Map<String, Int> = mapOf(
        // Nintendo consoles
        "nes" to 7,
        "fds" to 81,
        "snes" to 3,
        "n64" to 2,
        "gc" to 16,
        "wii" to 19,
        "wiiu" to 20,
        // Nintendo handhelds
        "gameandwatch" to 60,
        "gb" to 4,
        "gbc" to 6,
        "vb" to 28,
        "gba" to 5,
        "pokemini" to 24,
        "nds" to 18,
        "dsi" to 78,
        "3ds" to 62,
        // Sony
        "psx" to 12,
        "ps2" to 21,
        "psp" to 41,
        // Sega consoles
        "sg1000" to 33,
        "sms" to 11,
        "genesis" to 1,
        "scd" to 9,
        "32x" to 10,
        "pico" to 68,
        "saturn" to 39,
        "dreamcast" to 40,
        // Sega handhelds
        "gg" to 15,
        // Microsoft
        "xbox" to 22,
        // Atari
        "atari2600" to 25,
        "atari5200" to 50,
        "atari7800" to 51,
        "jaguar" to 17,
        "jaguarcd" to 77,
        "lynx" to 13,
        "atarist" to 36,
        // NEC
        "tg16" to 8,
        "tgcd" to 76,
        "pcfx" to 49,
        // SNK
        "neogeo" to 27,
        "neogeocd" to 56,
        "ngp" to 14,
        "ngpc" to 14,
        // Commodore
        "vic20" to 34,
        "c64" to 30,
        "amiga" to 35,
        // Arcade
        "arcade" to 27,
        "fbneo" to 27,
        "mame" to 27,
        "cps1" to 27,
        "cps2" to 27,
        "cps3" to 27,
        // Computers
        "amstradcpc" to 37,
        "dos" to 26,
        "msx" to 29,
        "msx2" to 29,
        "pc8800" to 47,
        "pc9800" to 48,
        "sharpx1" to 64,
        "x68000" to 52,
        "zx" to 59,
        "zx81" to 31,
        "fmtowns" to 58,
        // Other
        "3do" to 43,
        "cdi" to 42,
        "channelf" to 57,
        "coleco" to 44,
        "intellivision" to 45,
        "vectrex" to 46,
        "odyssey2" to 23,
        "wonderswan" to 53,
        "wsc" to 53,
        "supervision" to 63,
        "megaduck" to 69,
        "arduboy" to 71,
        "uzebox" to 80,
        "tic80" to 65,
        "cassettevision" to 54,
        "supercassettevision" to 55
    )

    fun getConsoleId(platformSlug: String): Int? {
        val canonical = PlatformDefinitions.getCanonicalSlug(platformSlug)
        return slugToConsoleId[canonical]
    }

    fun isSupported(platformSlug: String): Boolean = getConsoleId(platformSlug) != null
}

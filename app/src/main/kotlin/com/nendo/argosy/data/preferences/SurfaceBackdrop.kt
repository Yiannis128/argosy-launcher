package com.nendo.argosy.data.preferences

enum class BackdropPreset(val displayName: String) {
    DOTS("Dots"),
    SCANLINES("Scanlines"),
    HEX("Hex"),
    ICON_GRID("Icon Grid"),
    PLATFORMS("Platforms");

    companion object {
        fun fromString(value: String?): BackdropPreset =
            entries.find { it.name == value } ?: DOTS
    }
}

enum class BackdropEdgeStyle(val displayName: String) {
    NONE("None"),
    SOLID("Solid"),
    DASHED("Dashed"),
    FADED("Faded"),
    CONNECTIONS("Connections");

    companion object {
        fun fromString(value: String?): BackdropEdgeStyle =
            entries.find { it.name == value } ?: NONE
    }
}

enum class BackdropVertexIcon(val displayName: String) {
    NONE("None"),
    DOTS("Dots"),
    PLUS("Plus"),
    DIAMOND("Diamond");

    companion object {
        fun fromString(value: String?): BackdropVertexIcon =
            entries.find { it.name == value } ?: NONE
    }
}

enum class BackdropMotion(val displayName: String) {
    OFF("Off"),
    DRIFT("Drift"),
    SWAY("Sway");

    companion object {
        fun fromString(value: String?): BackdropMotion =
            entries.find { it.name == value } ?: OFF
    }
}

enum class HomeBackgroundMode(val displayName: String) {
    GAME_ART("Game Art"),
    PATTERN("Pattern");

    companion object {
        fun fromString(value: String?): HomeBackgroundMode =
            entries.find { it.name == value } ?: GAME_ART
    }
}

package com.nendo.argosy.data.preferences

enum class MenuWrapMode {
    OFF, HARD_STOP, AUTO;

    val displayName: String get() = when (this) {
        OFF -> "Off"
        HARD_STOP -> "Hard Stop"
        AUTO -> "Auto"
    }

    companion object {
        fun fromString(value: String?): MenuWrapMode =
            entries.find { it.name == value } ?: HARD_STOP
    }
}

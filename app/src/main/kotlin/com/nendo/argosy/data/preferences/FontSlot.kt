package com.nendo.argosy.data.preferences

enum class FontSlot(val key: String) {
    DISPLAY("display"),
    BODY("body");

    companion object {
        fun fromString(value: String?): FontSlot? =
            entries.find { it.name == value }
    }
}

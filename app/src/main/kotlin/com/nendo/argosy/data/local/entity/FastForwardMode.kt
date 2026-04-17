package com.nendo.argosy.data.local.entity

enum class FastForwardMode {
    HOLD,
    TOGGLE;

    companion object {
        fun fromString(value: String?): FastForwardMode = when (value) {
            TOGGLE.name -> TOGGLE
            else -> HOLD
        }
    }
}

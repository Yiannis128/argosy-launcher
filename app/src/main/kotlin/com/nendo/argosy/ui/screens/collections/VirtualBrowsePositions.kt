package com.nendo.argosy.ui.screens.collections

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualBrowsePositions @Inject constructor() {
    private val byType = mutableMapOf<String, Int>()

    fun get(type: String): Int = byType[type] ?: 0

    fun set(type: String, index: Int) {
        byType[type] = index
    }
}

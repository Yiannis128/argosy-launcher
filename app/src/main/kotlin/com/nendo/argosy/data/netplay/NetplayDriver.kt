package com.nendo.argosy.data.netplay

sealed interface NetplayDriver {
    fun tick()
    fun stop()
}

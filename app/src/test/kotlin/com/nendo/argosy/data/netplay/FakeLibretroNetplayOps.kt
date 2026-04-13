package com.nendo.argosy.data.netplay

import com.swordfish.libretrodroid.GLRetroView

internal class FakeLibretroNetplayOps : LibretroNetplayOps {
    val setCalls = mutableListOf<Pair<Int, Int>>()
    var stepCount: Int = 0
    private val portBitmasks = IntArray(4)

    fun setFakeInputBitmask(port: Int, bitmask: Int) {
        if (port in portBitmasks.indices) portBitmasks[port] = bitmask
    }

    override fun setInputPortState(port: Int, bitmask: Int) {
        setCalls += (port to bitmask)
    }

    override fun getInputPortBitmask(port: Int): Int {
        return if (port in portBitmasks.indices) portBitmasks[port] else 0
    }

    override fun stepForNetplay(retroView: GLRetroView) {
        stepCount += 1
    }
}

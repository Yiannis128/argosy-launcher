package com.nendo.argosy.data.netplay

import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.libretrodroid.LibretroDroid

interface LibretroNetplayOps {
    fun setInputPortState(port: Int, bitmask: Int)
    fun getInputPortBitmask(port: Int): Int
    fun stepForNetplay(retroView: GLRetroView)
    fun renderFrameOnly()
}

object RealLibretroNetplayOps : LibretroNetplayOps {
    override fun setInputPortState(port: Int, bitmask: Int) {
        LibretroDroid.setInputPortState(port, bitmask)
    }

    override fun getInputPortBitmask(port: Int): Int {
        return LibretroDroid.getInputPortBitmask(port)
    }

    override fun stepForNetplay(retroView: GLRetroView) {
        LibretroDroid.stepForNetplay(retroView)
    }

    override fun renderFrameOnly() {
        LibretroDroid.renderFrameOnly()
    }
}

package com.nendo.argosy.libretro.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class NetplayQualityInfoTest {

    @Test
    fun `null ping is bad`() {
        assertEquals(NetplayQualityLabel.Bad, NetplayQualityInfo.labelForRttMs(null))
    }

    @Test
    fun `under 40ms is excellent`() {
        assertEquals(NetplayQualityLabel.Excellent, NetplayQualityInfo.labelForRttMs(12))
        assertEquals(NetplayQualityLabel.Excellent, NetplayQualityInfo.labelForRttMs(39))
    }

    @Test
    fun `40 to 80ms is good`() {
        assertEquals(NetplayQualityLabel.Good, NetplayQualityInfo.labelForRttMs(40))
        assertEquals(NetplayQualityLabel.Good, NetplayQualityInfo.labelForRttMs(79))
    }

    @Test
    fun `80 to 150ms is fair`() {
        assertEquals(NetplayQualityLabel.Fair, NetplayQualityInfo.labelForRttMs(80))
        assertEquals(NetplayQualityLabel.Fair, NetplayQualityInfo.labelForRttMs(149))
    }

    @Test
    fun `150 to 200ms is poor`() {
        assertEquals(NetplayQualityLabel.Poor, NetplayQualityInfo.labelForRttMs(150))
        assertEquals(NetplayQualityLabel.Poor, NetplayQualityInfo.labelForRttMs(199))
    }

    @Test
    fun `200ms and above is bad`() {
        assertEquals(NetplayQualityLabel.Bad, NetplayQualityInfo.labelForRttMs(200))
        assertEquals(NetplayQualityLabel.Bad, NetplayQualityInfo.labelForRttMs(400))
    }
}

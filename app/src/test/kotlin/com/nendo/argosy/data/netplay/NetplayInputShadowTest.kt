package com.nendo.argosy.data.netplay

import android.view.KeyEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NetplayInputShadowTest {

    @Test
    fun setAndClearIndividualBits() {
        val shadow = NetplayInputShadow()
        shadow.onKeyDown(0, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_A)
        shadow.onKeyDown(0, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_B)

        val expected = (1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_A) or
            (1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_B)
        assertEquals(expected, shadow.sample(0))

        shadow.onKeyUp(0, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_A)
        assertEquals(1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_B, shadow.sample(0))
    }

    @Test
    fun separatePortsAreIsolated() {
        val shadow = NetplayInputShadow()
        shadow.onKeyDown(0, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_START)
        shadow.onKeyDown(1, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_SELECT)

        assertEquals(1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_START, shadow.sample(0))
        assertEquals(1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_SELECT, shadow.sample(1))
    }

    @Test
    fun androidKeyEventMapsToRetroId() {
        val shadow = NetplayInputShadow()
        shadow.onAndroidKeyEvent(0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)
        shadow.onAndroidKeyEvent(0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)

        val expected = (1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_A) or
            (1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_UP)
        assertEquals(expected, shadow.sample(0))

        shadow.onAndroidKeyEvent(0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)
        assertEquals(1 shl NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_UP, shadow.sample(0))
    }

    @Test
    fun unknownKeyCodeIsIgnored() {
        val shadow = NetplayInputShadow()
        shadow.onAndroidKeyEvent(0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP)
        assertEquals(0, shadow.sample(0))
    }

    @Test
    fun outOfRangePortIsIgnored() {
        val shadow = NetplayInputShadow()
        shadow.onKeyDown(99, NetplayInputShadow.RETRO_DEVICE_ID_JOYPAD_A)
        assertEquals(0, shadow.sample(99))
    }

    @Test
    fun concurrentWritesAreConsistent() {
        val shadow = NetplayInputShadow()
        val workers = 16
        val iterations = 10_000
        val latch = CountDownLatch(workers)
        val pool = Executors.newFixedThreadPool(workers)

        try {
            repeat(workers) { w ->
                pool.submit {
                    try {
                        val key = w % 16
                        repeat(iterations) {
                            shadow.onKeyDown(0, key)
                            shadow.sample(0)
                            shadow.onKeyUp(0, key)
                        }
                    } finally {
                        latch.countDown()
                    }
                }
            }
            latch.await(30, TimeUnit.SECONDS)
        } finally {
            pool.shutdownNow()
        }

        assertEquals(0, shadow.sample(0))
    }
}

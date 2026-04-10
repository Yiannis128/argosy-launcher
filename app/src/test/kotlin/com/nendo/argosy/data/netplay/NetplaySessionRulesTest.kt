package com.nendo.argosy.data.netplay

import com.nendo.argosy.libretro.CheatSessionManager
import com.nendo.argosy.libretro.RetroAchievementsSessionManager
import com.swordfish.libretrodroid.GLRetroView
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetplaySessionRulesTest {

    private fun buildRules(
        retroView: GLRetroView,
        cheat: CheatSessionManager? = null,
        ra: RetroAchievementsSessionManager? = null,
        onFastForward: (() -> Unit)? = null
    ): NetplaySessionRules {
        val rules = NetplaySessionRules(
            retroView = retroView,
            raSessionManager = { ra },
            onFastForwardRelease = onFastForward
        )
        rules.cheatSessionManager = cheat
        return rules
    }

    @Test
    fun applyAsHostDisablesCheatsPausesRaAndKillsBFI() = runTest {
        val view = mockk<GLRetroView>(relaxed = true)
        every { view.blackFrameInsertion } returns true
        val cheat = mockk<CheatSessionManager>(relaxed = true)
        coEvery { cheat.disableAllAndCycleForNetplay() } returns Unit
        val ra = mockk<RetroAchievementsSessionManager>(relaxed = true)

        val rules = buildRules(view, cheat = cheat, ra = ra)
        assertFalse(rules.isInNetplaySession.value)

        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Host))

        coVerify(exactly = 1) { cheat.disableAllAndCycleForNetplay() }
        verify(exactly = 1) { ra.pause() }
        verify { view.blackFrameInsertion = false }
        verify { view.frameSpeed = 1 }
        assertTrue(rules.isInNetplaySession.value)
    }

    @Test
    fun applyAsGuestDoesNotTouchCheats() = runTest {
        val view = mockk<GLRetroView>(relaxed = true)
        every { view.blackFrameInsertion } returns false
        val cheat = mockk<CheatSessionManager>(relaxed = true)
        val ra = mockk<RetroAchievementsSessionManager>(relaxed = true)

        val rules = buildRules(view, cheat = cheat, ra = ra)
        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Guest))

        coVerify(exactly = 0) { cheat.disableAllAndCycleForNetplay() }
        verify(exactly = 1) { ra.pause() }
        assertTrue(rules.isInNetplaySession.value)
    }

    @Test
    fun releaseResumesRaAndRestoresBFI() = runTest {
        val view = mockk<GLRetroView>(relaxed = true)
        every { view.blackFrameInsertion } returns true
        val ra = mockk<RetroAchievementsSessionManager>(relaxed = true)

        val rules = buildRules(view, ra = ra)
        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Guest))

        rules.release()

        verify(exactly = 1) { ra.resume() }
        verify { view.blackFrameInsertion = true }
        assertFalse(rules.isInNetplaySession.value)
    }

    @Test
    fun releaseBeforeApplyIsNoOp() {
        val view = mockk<GLRetroView>(relaxed = true)
        val ra = mockk<RetroAchievementsSessionManager>(relaxed = true)

        val rules = buildRules(view, ra = ra)
        rules.release()

        verify(exactly = 0) { ra.resume() }
        assertFalse(rules.isInNetplaySession.value)
    }

    @Test
    fun applyTwiceIsIdempotent() = runTest {
        val view = mockk<GLRetroView>(relaxed = true)
        every { view.blackFrameInsertion } returns false
        val ra = mockk<RetroAchievementsSessionManager>(relaxed = true)

        val rules = buildRules(view, ra = ra)
        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Guest))
        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Guest))

        verify(exactly = 1) { ra.pause() }
    }

    @Test
    fun applyInvokesFastForwardRelease() = runTest {
        val view = mockk<GLRetroView>(relaxed = true)
        every { view.blackFrameInsertion } returns false
        var fired = 0
        val rules = buildRules(view, onFastForward = { fired++ })
        rules.apply(NetplaySessionRules.ApplyContext(NetplaySessionRules.Role.Host))
        assertEquals(1, fired)
    }
}

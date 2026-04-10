package com.nendo.argosy.data.netplay

import com.nendo.argosy.data.social.ArgosSocialService
import com.nendo.argosy.data.social.NetplayOpenPayload
import com.nendo.argosy.data.social.NetplayReadyPayload
import com.nendo.argosy.data.social.NetplaySessionState
import com.swordfish.libretrodroid.GLRetroView
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

@OptIn(ExperimentalCoroutinesApi::class)
class NetplaySessionManagerTest {

    private fun fakeService(
        incoming: MutableSharedFlow<ArgosSocialService.IncomingMessage>,
        openResult: Boolean = true
    ): ArgosSocialService {
        val svc = mockk<ArgosSocialService>(relaxed = true)
        every { svc.incomingMessages } returns incoming
        every { svc.sendNetplayOpen(any()) } returns openResult
        every { svc.sendNetplayJoinRequest(any()) } returns true
        every { svc.sendNetplayJoinResponse(any()) } returns true
        every { svc.sendNetplayClose(any()) } returns true
        every { svc.sendNetplayLeave(any()) } returns true
        return svc
    }

    private fun fakeHandshake(): NetplayHandshake {
        return NetplayHandshake(
            candidateGatherer = mockk(relaxed = true),
            socialService = mockk(relaxed = true)
        )
    }

    @Test
    fun openServerFailsOnSendFailure() = runTest {
        val incoming = MutableSharedFlow<ArgosSocialService.IncomingMessage>(replay = 0, extraBufferCapacity = 16)
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + Job())
        val manager = NetplaySessionManager(
            socialService = fakeService(incoming, openResult = false),
            handshake = fakeHandshake(),
            retroView = mockk(relaxed = true),
            scope = scope
        )

        val result = manager.openServer(
            NetplayOpenPayload(
                gameIgdbId = null,
                gameTitle = "Test",
                coreId = "fceumm",
                romHashPrefix = "abc",
                coreHash = "def"
            )
        )
        assertTrue(result.isFailure)
        assertEquals(NetplaySessionState.Error("send_failed"), manager.sessionState.value)
        manager.shutdown()
    }

    @Test
    fun openServerTransitionsToWaitingOnReady() = runTest(StandardTestDispatcher()) {
        val incoming = MutableSharedFlow<ArgosSocialService.IncomingMessage>(replay = 0, extraBufferCapacity = 16)
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + Job())
        val manager = NetplaySessionManager(
            socialService = fakeService(incoming),
            handshake = fakeHandshake(),
            retroView = mockk(relaxed = true),
            scope = scope
        )

        val openJob = launch {
            val result = manager.openServer(
                NetplayOpenPayload(
                    gameIgdbId = null,
                    gameTitle = "Super Mario",
                    coreId = "fceumm",
                    romHashPrefix = "abc",
                    coreHash = "def"
                )
            )
            assertTrue(result.isSuccess)
        }

        // let the manager start collecting and the open call begin awaiting ready
        delay(50)

        val key = ByteArray(32) { it.toByte() }
        incoming.emit(
            ArgosSocialService.IncomingMessage.NetplayReady(
                NetplayReadyPayload(
                    sessionId = "sess-1",
                    sessionKey = Base64.getEncoder().encodeToString(key),
                    protocolVersion = 1
                )
            )
        )
        openJob.join()
        val state = manager.sessionState.value
        assertTrue("expected Waiting, got $state", state is NetplaySessionState.Waiting)
        assertEquals("sess-1", (state as NetplaySessionState.Waiting).sessionId)
        manager.shutdown()
    }

    @Test
    fun joinSessionFailsOnSendFailure() = runTest {
        val incoming = MutableSharedFlow<ArgosSocialService.IncomingMessage>(replay = 0, extraBufferCapacity = 16)
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + Job())
        val svc = mockk<ArgosSocialService>(relaxed = true) {
            every { incomingMessages } returns incoming
            every { sendNetplayJoinRequest(any()) } returns false
        }
        val manager = NetplaySessionManager(
            socialService = svc,
            handshake = fakeHandshake(),
            retroView = mockk(relaxed = true),
            scope = scope
        )
        manager.joinSession("sess-x", "host-user")
        assertEquals(NetplaySessionState.Error("send_failed"), manager.sessionState.value)
        manager.shutdown()
    }
}

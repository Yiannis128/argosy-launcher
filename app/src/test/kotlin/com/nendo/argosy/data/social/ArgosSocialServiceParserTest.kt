package com.nendo.argosy.data.social

import android.app.Application
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArgosSocialServiceParserTest {

    private fun newService(): ArgosSocialService {
        val application = mockk<Application>(relaxed = true)
        return ArgosSocialService(application)
    }

    @Test
    fun parsesNetplayInviteWithIgdbId() = runBlocking {
        val service = newService()
        val envelope = """
            {
              "type": "netplay_invite",
              "payload": {
                "session_id": "sess-42",
                "host_user_id": "user-1",
                "host_username": "alice",
                "game_title": "Super Mario",
                "game_igdb_id": 1234,
                "core_id": "fceumm",
                "rom_hash_prefix": "abc123",
                "core_hash": "def456",
                "protocol_version": 1
              }
            }
        """.trimIndent()

        service.handleMessageForTest(envelope)
        val message = withTimeout(2000) { service.incomingMessages.first() }
        assertTrue(
            "expected NetplayInvite but got ${message::class.simpleName}",
            message is ArgosSocialService.IncomingMessage.NetplayInvite
        )
        val payload = (message as ArgosSocialService.IncomingMessage.NetplayInvite).payload
        assertEquals("sess-42", payload.sessionId)
        assertEquals("user-1", payload.hostUserId)
        assertEquals("alice", payload.hostUsername)
        assertEquals("Super Mario", payload.gameTitle)
        assertEquals(1234, payload.gameIgdbId)
        assertEquals("fceumm", payload.coreId)
        assertEquals("abc123", payload.romHashPrefix)
        assertEquals("def456", payload.coreHash)
        assertEquals(1, payload.protocolVersion)
    }

    @Test
    fun parsesNetplayInviteWithoutIgdbId() = runBlocking {
        val service = newService()
        val envelope = """
            {
              "type": "netplay_invite",
              "payload": {
                "session_id": "sess-1",
                "host_user_id": "user-x",
                "host_username": "bob",
                "game_title": "Unknown ROM",
                "core_id": "snes9x",
                "rom_hash_prefix": "aa",
                "core_hash": "bb",
                "protocol_version": 1
              }
            }
        """.trimIndent()

        service.handleMessageForTest(envelope)
        val message = withTimeout(2000) { service.incomingMessages.first() }
        val payload = (message as ArgosSocialService.IncomingMessage.NetplayInvite).payload
        assertNull(payload.gameIgdbId)
        assertEquals("bob", payload.hostUsername)
    }
}

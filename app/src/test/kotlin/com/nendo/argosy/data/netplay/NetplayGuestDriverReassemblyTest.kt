package com.nendo.argosy.data.netplay

import com.swordfish.libretrodroid.GLRetroView
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.InetSocketAddress

class NetplayGuestDriverReassemblyTest {

    @Test
    fun reassemblesChunksInOrder() {
        val total = 4
        val chunks = List(total) { idx ->
            NetplayPacket.SnapshotChunk(
                snapshotId = 7,
                chunkIndex = idx,
                chunkTotal = total,
                payload = byteArrayOf((idx * 4).toByte(), (idx * 4 + 1).toByte(), (idx * 4 + 2).toByte(), (idx * 4 + 3).toByte())
            )
        }
        val buffer = NetplayGuestDriver.ReassemblyBuffer(7, total)
        chunks.forEach(buffer::addChunk)
        assertTrue(buffer.isComplete())
        val assembled = buffer.assemble()
        assertNotNull(assembled)
        val expected = ByteArray(16) { it.toByte() }
        assertArrayEquals(expected, assembled)
    }

    @Test
    fun reassemblesChunksOutOfOrder() {
        val total = 3
        val chunks = listOf(
            NetplayPacket.SnapshotChunk(1, 2, total, byteArrayOf(6, 7, 8)),
            NetplayPacket.SnapshotChunk(1, 0, total, byteArrayOf(0, 1, 2)),
            NetplayPacket.SnapshotChunk(1, 1, total, byteArrayOf(3, 4, 5))
        )
        val buffer = NetplayGuestDriver.ReassemblyBuffer(1, total)
        chunks.forEach(buffer::addChunk)
        assertTrue(buffer.isComplete())
        val assembled = buffer.assemble()
        assertNotNull(assembled)
        val expected = ByteArray(9) { it.toByte() }
        assertArrayEquals(expected, assembled)
    }

    @Test
    fun incompleteBufferReturnsNull() {
        val buffer = NetplayGuestDriver.ReassemblyBuffer(3, 4)
        buffer.addChunk(NetplayPacket.SnapshotChunk(3, 0, 4, byteArrayOf(1)))
        buffer.addChunk(NetplayPacket.SnapshotChunk(3, 2, 4, byteArrayOf(3)))
        assertFalse(buffer.isComplete())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildGuestDriver(scope: CoroutineScope): NetplayGuestDriver {
        val retroView = mockk<GLRetroView>(relaxed = true)
        every { retroView.unserializeState(any()) } returns true
        val transport = mockk<NetplayTransport>(relaxed = true)
        val incomingFlow = MutableSharedFlow<NetplayTransport.Incoming>(extraBufferCapacity = 256)
        every { transport.incomingPackets } returns incomingFlow.asSharedFlow() as SharedFlow<NetplayTransport.Incoming>
        coEvery { transport.send(any(), any()) } just Runs
        coEvery { transport.close() } just Runs
        return NetplayGuestDriver(
            retroView = retroView,
            transport = transport,
            initialPeerAddress = InetSocketAddress("127.0.0.1", 50002),
            peerUserId = "host",
            localPort = 1,
            hostPort = 0,
            scope = scope,
            onSessionEnd = {},
            libretroOps = FakeLibretroNetplayOps(),
            framePeriodNanos = 0L
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkDropsChunkTotalZero() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        val chunk = NetplayPacket.SnapshotChunk(1, 0, 0, byteArrayOf(1))
        driver.handleSnapshotChunkAt(0L, chunk)
        assertEquals(0, driver.reassemblySizeForTest())
        driver.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkDropsChunkTotalAboveMax() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        val chunk = NetplayPacket.SnapshotChunk(
            snapshotId = 2,
            chunkIndex = 0,
            chunkTotal = NetplaySecurityBounds.MAX_CHUNKS_PER_SNAPSHOT + 1,
            payload = byteArrayOf(1)
        )
        driver.handleSnapshotChunkAt(0L, chunk)
        assertEquals(0, driver.reassemblySizeForTest())
        driver.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkDropsChunkIndexAtOrAboveTotal() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        val chunk = NetplayPacket.SnapshotChunk(3, 4, 4, byteArrayOf(1))
        driver.handleSnapshotChunkAt(0L, chunk)
        assertEquals(0, driver.reassemblySizeForTest())
        driver.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkDropsNegativeChunkIndex() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        val chunk = NetplayPacket.SnapshotChunk(4, -1, 4, byteArrayOf(1))
        driver.handleSnapshotChunkAt(0L, chunk)
        assertEquals(0, driver.reassemblySizeForTest())
        driver.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkDropsThirdConcurrentSnapshot() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        val now = 1_000_000_000L
        driver.injectReassemblyBufferForTest(10, 4, now)
        driver.injectReassemblyBufferForTest(11, 4, now)
        assertEquals(2, driver.reassemblySizeForTest())
        val chunk = NetplayPacket.SnapshotChunk(12, 0, 4, byteArrayOf(1))
        driver.handleSnapshotChunkAt(now, chunk)
        assertEquals(2, driver.reassemblySizeForTest())
        assertFalse(driver.reassemblyContainsForTest(12))
        driver.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun handleSnapshotChunkEvictsStaleEntryBeforeProcessing() = runTest(UnconfinedTestDispatcher()) {
        val driver = buildGuestDriver(CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job()))
        driver.injectReassemblyBufferForTest(20, 4, createdNanos = 0L)
        val now = NetplaySecurityBounds.REASSEMBLY_TTL_NANOS + 1L
        val chunk = NetplayPacket.SnapshotChunk(21, 0, 4, byteArrayOf(1))
        driver.handleSnapshotChunkAt(now, chunk)
        assertFalse("stale snapshot 20 should be evicted", driver.reassemblyContainsForTest(20))
        assertTrue("new snapshot 21 should be accepted", driver.reassemblyContainsForTest(21))
        driver.stop()
    }

    @Test
    fun duplicateChunksAreIgnored() {
        val buffer = NetplayGuestDriver.ReassemblyBuffer(5, 2)
        val chunk = NetplayPacket.SnapshotChunk(5, 0, 2, byteArrayOf(9))
        buffer.addChunk(chunk)
        val firstProgress = buffer.lastProgressNanos
        Thread.sleep(2)
        buffer.addChunk(NetplayPacket.SnapshotChunk(5, 0, 2, byteArrayOf(42)))
        // duplicate index ignored so lastProgressNanos should not advance
        assertTrue(buffer.lastProgressNanos == firstProgress)
        buffer.addChunk(NetplayPacket.SnapshotChunk(5, 1, 2, byteArrayOf(11)))
        assertTrue(buffer.isComplete())
        val assembled = buffer.assemble()
        assertArrayEquals(byteArrayOf(9, 11), assembled)
    }
}

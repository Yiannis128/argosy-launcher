package com.nendo.argosy.data.sync.platform

import android.content.Context
import com.nendo.argosy.data.emulator.RetroArchConfigParser
import com.nendo.argosy.data.emulator.RetroArchSaveConfig
import com.nendo.argosy.data.emulator.SavePathConfig
import com.nendo.argosy.data.emulator.SavePathRegistry
import com.nendo.argosy.data.storage.FileAccessLayer
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RetroArchSaveHandlerTest {

    private lateinit var context: Context
    private lateinit var fal: FileAccessLayer
    private lateinit var parser: RetroArchConfigParser
    private lateinit var handler: RetroArchSaveHandler

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        fal = mockk(relaxed = true)
        parser = mockk(relaxed = true)
        handler = RetroArchSaveHandler(context, fal, parser)
    }

    private fun saveContext(
        emulatorId: String = "retroarch",
        platformSlug: String = "snes",
        romPath: String? = "/storage/emulated/0/Roms/SNES/Mario.smc",
        gameTitle: String = "Super Mario World",
        localSavePath: String? = null,
    ): SaveContext = SaveContext(
        config = mockk(relaxed = true) { every { saveExtensions } returns listOf("srm") },
        romPath = romPath,
        titleId = null,
        emulatorPackage = null,
        gameId = 1L,
        gameTitle = gameTitle,
        platformSlug = platformSlug,
        emulatorId = emulatorId,
        localSavePath = localSavePath,
    )

    @Test
    fun `constructSavePath omits core subdir when sort_savefiles_enable is false`() {
        every { parser.parse(any()) } returns RetroArchSaveConfig(
            savefileDirectory = "/storage/emulated/0/RetroArch/saves",
            savefilesInContentDir = false,
            sortByContentDirectory = false,
            sortByCore = false,
        )

        val ctx = saveContext()
        val path = handler.constructSavePath(ctx)

        assertNotNull("constructSavePath should not return null when core is mapped", path)
        assertEquals(
            "When sort=false, save path must NOT contain the core subdirectory",
            "/storage/emulated/0/RetroArch/saves/Mario.srm",
            path,
        )
    }

    @Test
    fun `constructSavePath uses core subdir when sort_savefiles_enable is true`() {
        every { parser.parse(any()) } returns RetroArchSaveConfig(
            savefileDirectory = "/storage/emulated/0/RetroArch/saves",
            savefilesInContentDir = false,
            sortByContentDirectory = false,
            sortByCore = true,
        )

        val ctx = saveContext()
        val path = handler.constructSavePath(ctx)

        assertNotNull(path)
        assertTrue(
            "When sort=true, path must contain core subdirectory; got: $path",
            path!!.contains("/Snes9x/") || path.contains("/snes9x/"),
        )
    }

    @Test
    fun `constructSavePath honors content-dir mode`() {
        every { parser.parse(any()) } returns RetroArchSaveConfig(
            savefileDirectory = "/storage/emulated/0/RetroArch/saves",
            savefilesInContentDir = true,
            sortByContentDirectory = false,
            sortByCore = false,
        )

        val ctx = saveContext(romPath = "/storage/emulated/0/Roms/SNES/Mario.smc")
        val path = handler.constructSavePath(ctx)

        assertNotNull(path)
        assertTrue(
            "When savefiles_in_content_dir=true, path should land next to ROM; got: $path",
            path!!.startsWith("/storage/emulated/0/Roms/SNES"),
        )
    }

    @Test
    fun `constructSavePath honors PSX mcd extension`() {
        every { parser.parse(any()) } returns RetroArchSaveConfig(
            savefileDirectory = "/storage/emulated/0/RetroArch/saves",
            savefilesInContentDir = false,
            sortByContentDirectory = false,
            sortByCore = false,
        )

        val ctx = saveContext(
            platformSlug = "psx",
            romPath = "/storage/emulated/0/Roms/PSX/FFVII.cue",
        )
        val path = handler.constructSavePath(ctx)

        assertNotNull(path)
        assertFalse(
            "PSX should not get .srm extension; got: $path",
            path!!.endsWith(".srm"),
        )
    }
}

package com.nendo.argosy.data.emulator

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ArchiveRomNamingTest {

    @Rule
    @JvmField
    val temp = TemporaryFolder()

    private fun zipWith(archiveName: String, vararg entryNames: String): File {
        val archive = temp.newFile(archiveName)
        ZipOutputStream(archive.outputStream()).use { out ->
            entryNames.forEach { name ->
                out.putNextEntry(ZipEntry(name))
                out.write(byteArrayOf(1, 2, 3))
                out.closeEntry()
            }
        }
        return archive
    }

    @Test
    fun `launch base name comes from the entry not the archive`() {
        val archive = zipWith("Pokemon Emerald (USA).zip", "Pokemon - Emerald Version (U).gba")
        assertEquals("Pokemon - Emerald Version (U)", ArchiveRomNaming.launchBaseName(archive))
    }

    @Test
    fun `nested entries resolve to the bare file name`() {
        val archive = zipWith("Sonic.zip", "roms/genesis/Sonic The Hedgehog (W) (REV01).md")
        assertEquals("Sonic The Hedgehog (W) (REV01)", ArchiveRomNaming.launchBaseName(archive))
    }

    @Test
    fun `macOS resource forks are skipped`() {
        val archive = zipWith("Zelda.zip", "._Zelda.gba", "Zelda - The Minish Cap.gba")
        assertEquals("Zelda - The Minish Cap", ArchiveRomNaming.launchBaseName(archive))
    }

    @Test
    fun `non-archives and unreadable files yield no name`() {
        val plain = temp.newFile("Metroid.gba")
        assertNull(ArchiveRomNaming.launchBaseName(plain))
        assertNull(ArchiveRomNaming.launchBaseName(File(temp.root, "missing.zip")))
    }

    @Test
    fun `an empty archive yields no name rather than throwing`() {
        val archive = zipWith("Empty.zip")
        assertNull(ArchiveRomNaming.launchBaseName(archive))
    }

    @Test
    fun `an archive named the same as its entry still resolves`() {
        val archive = zipWith("Tetris.zip", "Tetris.gb")
        assertEquals("Tetris", ArchiveRomNaming.launchBaseName(archive))
    }
}

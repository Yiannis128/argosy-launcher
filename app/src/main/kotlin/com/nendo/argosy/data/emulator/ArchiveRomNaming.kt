package com.nendo.argosy.data.emulator

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Names the built-in emulator ends up launching when a single-file archive is extracted.
 *
 * Extraction launches the archive's first usable entry, so saves are named after that entry
 * rather than the archive. Save detection has to agree with extraction on which entry wins,
 * so both sides resolve it here.
 */
object ArchiveRomNaming {
    fun isUsableEntry(entry: ZipEntry): Boolean =
        !entry.isDirectory && !entry.name.startsWith("._")

    fun primaryEntry(zip: ZipFile): ZipEntry? =
        zip.entries().toList().firstOrNull { isUsableEntry(it) }

    fun launchFileName(entry: ZipEntry): String = File(entry.name).name

    fun launchBaseName(archive: File): String? {
        if (!archive.isFile || !archive.extension.equals("zip", ignoreCase = true)) return null

        return try {
            ZipFile(archive).use { zip ->
                primaryEntry(zip)?.let { File(launchFileName(it)).nameWithoutExtension }
            }
        } catch (_: Exception) {
            null
        }
    }
}

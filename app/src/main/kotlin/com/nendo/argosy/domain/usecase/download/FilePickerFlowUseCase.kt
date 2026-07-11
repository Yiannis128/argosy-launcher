package com.nendo.argosy.domain.usecase.download

import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.local.dao.GameFileDao
import com.nendo.argosy.data.model.VariantCategory
import com.nendo.argosy.data.preferences.DownloadDefaults
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import com.nendo.argosy.data.remote.romm.RomMRepository
import com.nendo.argosy.data.remote.romm.RomMResult
import com.nendo.argosy.data.model.FilePickerRow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private fun pathOnDisk(path: String?): Boolean = path != null && File(path).exists()

data class FilePickerSetup(
    val rows: List<FilePickerRow>,
    val preselectedFileIds: Set<Long>,
    val preselectedVersionIds: Set<Long>
)

/**
 * Shared core of the cherry-pick flow, usable from Hilt delegates and the
 * manually-constructed DualScreenManager alike.
 */
@Singleton
class FilePickerFlowUseCase @Inject constructor(
    private val gameDao: GameDao,
    private val gameFileDao: GameFileDao,
    private val romMRepository: RomMRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val downloadGameUseCase: DownloadGameUseCase
) {

    /** Null when there is nothing to choose: callers fall back to a plain download. */
    suspend fun buildRows(gameId: Long): FilePickerSetup? {
        val game = gameDao.getById(gameId) ?: return null
        val rommId = game.rommId ?: return null
        val dbRows = gameFileDao.getFilesForGame(gameId)
        val defaults = preferencesRepository.getEffectiveDownloadDefaults(game.platformSlug)

        val rows = mutableListOf<FilePickerRow>()
        val preselectedFiles = mutableSetOf<Long>()
        val preselectedVersions = mutableSetOf<Long>()

        val versionGroups = dbRows.filter { it.versionGroup != null }.groupBy { it.versionGroup!! }
        if (versionGroups.size > 1) {
            rows += FilePickerRow(isHeader = true, groupKey = "versions", label = "Version")
            val defaultLaunch = game.activeVariantFileId
            versionGroups.forEach { (key, files) ->
                val memberRommId = key.removePrefix("romm:").toLongOrNull() ?: return@forEach
                val label = files.firstOrNull { it.regions != null }?.regions
                    ?: files.first().fileName
                val isDefault = defaultLaunch != null && files.any { it.id == defaultLaunch } ||
                    (defaultLaunch == null && memberRommId == rommId)
                rows += FilePickerRow(
                    isHeader = false,
                    groupKey = "versions",
                    label = label,
                    versionRommId = memberRommId,
                    sizeBytes = files.sumOf { it.fileSize },
                    isDownloaded = files.any { pathOnDisk(it.localPath) },
                    isDefaultVersion = isDefault
                )
                if (isDefault) preselectedVersions += memberRommId
            }
            if (preselectedVersions.isEmpty()) preselectedVersions += rommId
        }

        val romFiles = when (val result = romMRepository.getRom(rommId)) {
            is RomMResult.Success -> result.data.files
                ?.filter { !it.fileName.startsWith(".") } ?: emptyList()
            is RomMResult.Error -> emptyList()
        }
        if (romFiles.size > 1) {
            val rootLen = romFiles.minOf { it.filePath.length }
            val grouped = romFiles.groupBy { f ->
                val cat = f.category
                when {
                    cat == VariantCategory.GAME.key || (cat == null && f.filePath.length == rootLen) -> "game"
                    cat != null -> cat
                    else -> "folder:${f.filePath.substringAfterLast('/')}"
                }
            }
            grouped.entries
                .sortedBy { (key, _) ->
                    if (key == "game") -1 else VariantCategory.fromKey(key).sortOrder
                }
                .forEach { (key, files) ->
                    val isGame = key == "game"
                    val label = when {
                        isGame -> "Game"
                        key.startsWith("folder:") -> key.removePrefix("folder:")
                        else -> VariantCategory.fromKey(key).displayLabel
                    }
                    rows += FilePickerRow(isHeader = true, groupKey = key, label = label)
                    val includeDefault = when {
                        isGame -> true
                        key.startsWith("folder:") -> defaults[DownloadDefaults.OTHER_KEY] ?: false
                        else -> defaults[key] ?: false
                    }
                    files.sortedBy { it.fileName }.forEach { f ->
                        val downloaded = pathOnDisk(dbRows.firstOrNull { it.rommFileId == f.id }?.localPath)
                        rows += FilePickerRow(
                            isHeader = false,
                            groupKey = key,
                            label = f.fileName,
                            rommFileId = f.id,
                            sizeBytes = f.fileSizeBytes,
                            isDownloaded = downloaded,
                            isLocked = isGame && files.size == 1
                        )
                        if (includeDefault) preselectedFiles += f.id
                    }
                }
        }

        if (rows.none { !it.isHeader }) return null
        if (versionGroups.size <= 1 && romFiles.size <= 1) return null
        return FilePickerSetup(rows, preselectedFiles, preselectedVersions)
    }

    /** Returns queued count; errors surface through the returned messages. */
    suspend fun downloadSelection(
        gameId: Long,
        selectedFileIds: Set<Long>,
        selectedVersionIds: Set<Long>
    ): Pair<Int, List<String>> {
        val primaryRommId = gameDao.getById(gameId)?.rommId
        var queued = 0
        val errors = mutableListOf<String>()
        if (primaryRommId == null || primaryRommId in selectedVersionIds || selectedVersionIds.isEmpty()) {
            val explicit = selectedFileIds.toList().takeIf { it.isNotEmpty() }
            when (val r = downloadGameUseCase(gameId, selectedFileIds = explicit)) {
                is DownloadResult.Queued -> queued++
                is DownloadResult.AlreadyDownloaded -> errors += "Game already downloaded"
                is DownloadResult.Error -> errors += r.message
                else -> {}
            }
        }
        selectedVersionIds.filter { it != primaryRommId }.forEach { versionId ->
            when (val r = downloadGameUseCase(gameId, versionRommId = versionId)) {
                is DownloadResult.Queued -> queued++
                is DownloadResult.Error -> errors += r.message
                else -> {}
            }
        }
        return queued to errors
    }
}

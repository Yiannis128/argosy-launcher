package com.nendo.argosy.data.emulator

import com.nendo.argosy.data.local.dao.GameFileDao
import com.nendo.argosy.data.local.entity.GameEntity
import com.nendo.argosy.data.local.entity.GameFileEntity
import com.nendo.argosy.data.model.VariantCategory
import javax.inject.Inject
import javax.inject.Singleton

data class VariantOption(
    val fileId: Long?,
    val fileName: String,
    val category: String,
    val isDownloaded: Boolean,
    val isMultiDisc: Boolean,
    val fileSizeBytes: Long
)

@Singleton
class VariantResolver @Inject constructor(
    private val gameFileDao: GameFileDao
) {
    suspend fun resolveVariant(game: GameEntity): GameFileEntity? {
        if (game.platformSlug in VariantCategory.VARIANT_EXCLUDED_PLATFORMS) return null

        game.activeVariantFileId?.let { fileId ->
            val file = gameFileDao.getById(fileId)
            if (file != null && file.localPath != null) return file
        }

        game.lastPlayedFileId?.let { fileId ->
            val file = gameFileDao.getById(fileId)
            if (file != null && file.localPath != null) return file
        }

        return null
    }

    suspend fun getVariantOptions(game: GameEntity): List<VariantOption>? {
        if (game.platformSlug in VariantCategory.VARIANT_EXCLUDED_PLATFORMS) return null
        val variants = gameFileDao.getVariantsForGame(game.id)
        if (variants.isEmpty()) return null

        val primary = VariantOption(
            fileId = null,
            fileName = game.rommFileName ?: game.localPath?.substringAfterLast('/') ?: game.title,
            category = VariantCategory.GAME.key,
            isDownloaded = game.localPath != null,
            isMultiDisc = game.isMultiDisc,
            fileSizeBytes = game.fileSizeBytes ?: 0
        )

        val variantOptions = variants.map { file ->
            VariantOption(
                fileId = file.id,
                fileName = file.fileName,
                category = file.category,
                isDownloaded = file.localPath != null,
                isMultiDisc = file.isMultiDisc,
                fileSizeBytes = file.fileSize
            )
        }

        return listOf(primary) + variantOptions
    }
}

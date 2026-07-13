package com.nendo.argosy.domain.usecase.music

import com.nendo.argosy.data.local.dao.GameFileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class LocalMusicTrackState(
    val gameFileId: Long?,
    val localPath: String
)

/** Resolves which RomM music tracks are already on disk, keyed by romm file id. */
class GetLocalMusicTrackStateUseCase @Inject constructor(
    private val gameFileDao: GameFileDao
) {
    suspend operator fun invoke(rommFileIds: List<Long>): Map<Long, LocalMusicTrackState> =
        withContext(Dispatchers.IO) {
            buildMap {
                for (id in rommFileIds) {
                    val row = gameFileDao.getByRommFileId(id) ?: continue
                    val path = row.localPath ?: continue
                    if (!File(path).exists()) continue
                    put(id, LocalMusicTrackState(row.id, path))
                }
            }
        }
}

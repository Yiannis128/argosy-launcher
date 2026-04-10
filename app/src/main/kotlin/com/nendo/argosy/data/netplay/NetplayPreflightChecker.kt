package com.nendo.argosy.data.netplay

import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.local.dao.GameFileDao
import com.nendo.argosy.data.local.entity.GameFileEntity
import com.nendo.argosy.data.social.NetplaySession
import com.nendo.argosy.libretro.LibretroCoreManager
import com.nendo.argosy.libretro.LibretroCoreRegistry
import com.nendo.argosy.libretro.NetplaySupportLevel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed class NetplayPreflightResult {
    data class Joinable(val localFilePath: String) : NetplayPreflightResult()
    data object RomNotFound : NetplayPreflightResult()
    data object RomVersionMismatch : NetplayPreflightResult()
    data object CoreVersionMismatch : NetplayPreflightResult()
    data object CoreNotSupported : NetplayPreflightResult()
}

interface RomHashProvider {
    fun computeRomHashPrefix(file: File): String?
}

interface CoreHashLookup {
    fun hashForCoreId(coreId: String): String?
}

@Singleton
class NetplayPreflightChecker(
    private val gameDao: GameDao,
    private val gameFileDao: GameFileDao,
    private val coreHashLookup: CoreHashLookup,
    private val romHashProvider: RomHashProvider,
    private val coreRegistry: CoreRegistryAdapter
) {
    @Inject constructor(
        gameDao: GameDao,
        gameFileDao: GameFileDao,
        netplayCoreHashLookup: NetplayCoreHashLookup
    ) : this(
        gameDao = gameDao,
        gameFileDao = gameFileDao,
        coreHashLookup = netplayCoreHashLookup,
        romHashProvider = DefaultRomHashProvider,
        coreRegistry = DefaultCoreRegistryAdapter
    )

    suspend fun check(session: NetplaySession): NetplayPreflightResult {
        val coreInfo = coreRegistry.findCore(session.coreId)
            ?: return NetplayPreflightResult.CoreNotSupported
        if (coreInfo.netplaySupport != NetplaySupportLevel.SUPPORTED) {
            return NetplayPreflightResult.CoreNotSupported
        }

        val igdbId = session.gameIgdbId?.toLong()
            ?: return NetplayPreflightResult.RomNotFound
        val game = gameDao.getByIgdbId(igdbId)
            ?: return NetplayPreflightResult.RomNotFound

        if (game.platformSlug.isNotEmpty() && !coreInfo.supportsPlatform(game.platformSlug)) {
            return NetplayPreflightResult.CoreNotSupported
        }

        val files = gameFileDao.getFilesForGame(game.id)
            .filter { !it.localPath.isNullOrEmpty() }
        if (files.isEmpty()) return NetplayPreflightResult.RomNotFound

        var sawFileWithHash = false
        var matchedFile: GameFileEntity? = null
        for (file in files) {
            val localPath = file.localPath ?: continue
            val hashPrefix = file.romHashPrefix ?: run {
                val computed = romHashProvider.computeRomHashPrefix(File(localPath))
                if (computed != null) {
                    gameFileDao.updateRomHashPrefix(file.id, computed)
                }
                computed
            } ?: continue
            sawFileWithHash = true
            if (hashPrefix.equals(session.romHashPrefix, ignoreCase = true)) {
                matchedFile = file
                break
            }
        }

        val resolved = matchedFile ?: return if (sawFileWithHash) {
            NetplayPreflightResult.RomVersionMismatch
        } else {
            NetplayPreflightResult.RomNotFound
        }

        val localCoreHash = coreHashLookup.hashForCoreId(session.coreId)
            ?: return NetplayPreflightResult.CoreVersionMismatch
        if (!localCoreHash.equals(session.coreHash, ignoreCase = true)) {
            return NetplayPreflightResult.CoreVersionMismatch
        }

        return NetplayPreflightResult.Joinable(resolved.localPath!!)
    }
}

interface CoreRegistryAdapter {
    fun findCore(coreId: String): CoreDescriptor?
}

data class CoreDescriptor(
    val coreId: String,
    val netplaySupport: NetplaySupportLevel,
    val supportedPlatforms: Set<String>
) {
    fun supportsPlatform(platformSlug: String): Boolean =
        supportedPlatforms.any { it.equals(platformSlug, ignoreCase = true) }
}

object DefaultCoreRegistryAdapter : CoreRegistryAdapter {
    override fun findCore(coreId: String): CoreDescriptor? {
        val info = LibretroCoreRegistry.getCoreById(coreId) ?: return null
        return CoreDescriptor(
            coreId = info.coreId,
            netplaySupport = info.netplaySupport,
            supportedPlatforms = info.platforms
        )
    }
}

object DefaultRomHashProvider : RomHashProvider {
    override fun computeRomHashPrefix(file: File): String? =
        RomHashComputer.computeRomHashPrefix(file)
}

@Singleton
class NetplayCoreHashLookup @Inject constructor(
    private val libretroCoreManager: LibretroCoreManager,
    private val coreHashCache: CoreHashCache
) : CoreHashLookup {
    override fun hashForCoreId(coreId: String): String? {
        val path = libretroCoreManager.getCorePathForCoreId(coreId) ?: return null
        return coreHashCache.getHashForCore(path)
    }
}

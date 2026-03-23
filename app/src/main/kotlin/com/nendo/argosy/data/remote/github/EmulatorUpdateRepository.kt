package com.nendo.argosy.data.remote.github

import android.util.Log
import com.nendo.argosy.data.emulator.ApkAssetMatcher
import com.nendo.argosy.data.emulator.ApkMatchResult
import com.nendo.argosy.data.emulator.EmulatorDef
import com.nendo.argosy.data.emulator.ReleaseSource
import com.nendo.argosy.data.local.dao.EmulatorUpdateDao
import com.nendo.argosy.data.local.entity.EmulatorUpdateEntity
import com.nendo.argosy.data.remote.gitea.GiteaApi
import com.nendo.argosy.data.remote.gitlab.GitLabApi
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed class EmulatorUpdateCheckResult {
    data class UpdateAvailable(
        val emulatorId: String,
        val currentVersion: String?,
        val latestVersion: String,
        val release: GitHubRelease,
        val matchResult: ApkMatchResult
    ) : EmulatorUpdateCheckResult()

    data class UpToDate(
        val emulatorId: String,
        val version: String
    ) : EmulatorUpdateCheckResult()

    data class Error(
        val emulatorId: String,
        val message: String
    ) : EmulatorUpdateCheckResult()
}

@Singleton
class EmulatorUpdateRepository @Inject constructor(
    private val emulatorUpdateDao: EmulatorUpdateDao
) {
    companion object {
        private const val TAG = "EmulatorUpdateRepo"
        private const val GITHUB_API_BASE = "https://api.github.com/"

        private fun normalizeVersion(version: String): String {
            return version
                .lowercase()
                .trim()
                .removePrefix("v")
                .replace(Regex("[^a-z0-9.-]"), "")
        }

        private val VERSION_PATTERN = Regex("""v?(\d+\.\d+(?:\.\d+)?(?:-[\w.]+)?)""")
        private val HEX_PATTERN = Regex("""^[0-9a-fA-F]{7,40}$""")

        fun extractVersionFromRelease(release: GitHubRelease): String? {
            // First try to find a semver-like version in the release name
            VERSION_PATTERN.find(release.name)?.let { match ->
                return match.groupValues[1]
            }

            // Then try the tag name
            VERSION_PATTERN.find(release.tagName)?.let { match ->
                return match.groupValues[1]
            }

            // Try APK filenames - often contain version like "app-1.2.3-arm64.apk"
            release.assets
                .filter { it.name.endsWith(".apk", ignoreCase = true) }
                .firstNotNullOfOrNull { asset ->
                    VERSION_PATTERN.find(asset.name)?.groupValues?.get(1)
                }?.let { return it }

            // No semver found
            return null
        }

        fun findVersionFromTags(tags: List<GitHubTag>, releaseTagName: String): String? {
            // If the release tag is a commit hash, find a semver tag pointing to same commit
            val releaseTagHash = releaseTagName.removePrefix("v")

            // First, find the commit SHA for the release tag (it might be in the tags list)
            val releaseTag = tags.find { it.name == releaseTagName }
            val targetSha = releaseTag?.commit?.sha ?: releaseTagHash

            // Look for semver tags pointing to the same commit
            return tags
                .filter { VERSION_PATTERN.containsMatchIn(it.name) }
                .find { it.commit.sha.startsWith(targetSha) || targetSha.startsWith(it.commit.sha) }
                ?.let { VERSION_PATTERN.find(it.name)?.groupValues?.get(1) }
        }

        fun isCommitHash(version: String): Boolean {
            return HEX_PATTERN.matches(version.removePrefix("v"))
        }
    }

    private val moshi = Moshi.Builder().build()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gitHubApi: GitHubApi by lazy {
        Retrofit.Builder()
            .baseUrl(GITHUB_API_BASE)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GitHubApi::class.java)
    }

    private val giteaApis = mutableMapOf<String, GiteaApi>()
    private val gitLabApis = mutableMapOf<String, GitLabApi>()

    private fun getGiteaApi(baseUrl: String): GiteaApi =
        giteaApis.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl.trimEnd('/') + "/")
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(GiteaApi::class.java)
        }

    private fun getGitLabApi(baseUrl: String): GitLabApi =
        gitLabApis.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl.trimEnd('/') + "/")
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(GitLabApi::class.java)
        }

    private suspend fun fetchReleaseFromSource(source: ReleaseSource): GitHubRelease? {
        return when (source) {
            is ReleaseSource.GitHub -> {
                val (owner, repo) = source.repo.split("/")
                val response = gitHubApi.getRepoLatestRelease(owner, repo)
                if (response.isSuccessful) response.body() else null
            }
            is ReleaseSource.Gitea -> {
                val (owner, repo) = source.repo.split("/")
                val api = getGiteaApi(source.baseUrl)
                val response = api.getLatestRelease(owner, repo)
                if (response.isSuccessful) response.body() else null
            }
            is ReleaseSource.GitLab -> {
                val api = getGitLabApi(source.baseUrl)
                val encodedPath = URLEncoder.encode(source.projectPath, "UTF-8")
                val response = api.getReleases(encodedPath, perPage = 1)
                if (!response.isSuccessful) return null
                val glRelease = response.body()?.firstOrNull() ?: return null
                GitHubRelease(
                    tagName = glRelease.tagName,
                    name = glRelease.name,
                    body = glRelease.description,
                    prerelease = false,
                    draft = false,
                    htmlUrl = "${source.baseUrl}/${source.projectPath}/-/releases/${glRelease.tagName}",
                    targetCommitish = null,
                    assets = glRelease.assets?.links?.map { link ->
                        GitHubAsset(
                            name = link.name,
                            downloadUrl = link.directAssetUrl,
                            size = 0,
                            contentType = "application/octet-stream"
                        )
                    } ?: emptyList()
                )
            }
        }
    }

    private suspend fun fetchTagsFromSource(source: ReleaseSource): List<GitHubTag>? {
        return when (source) {
            is ReleaseSource.GitHub -> {
                val (owner, repo) = source.repo.split("/")
                val response = gitHubApi.getRepoTags(owner, repo)
                if (response.isSuccessful) response.body() else null
            }
            is ReleaseSource.Gitea -> {
                val (owner, repo) = source.repo.split("/")
                val api = getGiteaApi(source.baseUrl)
                val response = api.getTags(owner, repo)
                if (response.isSuccessful) response.body() else null
            }
            is ReleaseSource.GitLab -> null
        }
    }

    suspend fun checkForUpdate(
        emulator: EmulatorDef,
        installedVersion: String?,
        storedVariant: String? = null
    ): EmulatorUpdateCheckResult {
        val source = emulator.releaseSource ?: return EmulatorUpdateCheckResult.Error(
            emulator.id,
            "No release source configured"
        )

        return try {
            val release = fetchReleaseFromSource(source)

            if (release == null) {
                Log.w(TAG, "API error for ${emulator.id}")
                return EmulatorUpdateCheckResult.Error(
                    emulator.id,
                    "Release API request failed"
                )
            }

            // Try to extract version from release info first
            var extractedVersion = extractVersionFromRelease(release)
            Log.d(TAG, "${emulator.id}: tag=${release.tagName}, name=${release.name}, extractedFromRelease=$extractedVersion")

            // If no semver found and tag looks like a commit hash, fetch tags to find version
            if (extractedVersion == null && isCommitHash(release.tagName)) {
                Log.d(TAG, "${emulator.id}: tag is commit hash, fetching tags...")
                fetchTagsFromSource(source)?.let { tags ->
                    Log.d(TAG, "${emulator.id}: got ${tags.size} tags")
                    tags.take(5).forEach { t -> Log.d(TAG, "  tag: ${t.name} -> ${t.commit.sha.take(7)}") }
                    extractedVersion = findVersionFromTags(tags, release.tagName)
                    Log.d(TAG, "${emulator.id}: found version from tags: $extractedVersion")
                }
            }

            // Final fallback to tag name
            extractedVersion = extractedVersion ?: release.tagName
            Log.d(TAG, "${emulator.id}: final version: $extractedVersion")

            val latestVersion = VersionInfo.parse(extractedVersion)

            // If installed version is a commit hash, try to find the associated tag
            var resolvedInstalledVersion = installedVersion
            if (installedVersion != null && isCommitHash(installedVersion)) {
                Log.d(TAG, "${emulator.id}: installed version is commit hash, fetching tags...")
                fetchTagsFromSource(source)?.let { tags ->
                    val matchingTag = tags.find { it.commit.sha.startsWith(installedVersion) || installedVersion.startsWith(it.commit.sha.take(7)) }
                    if (matchingTag != null) {
                        val tagVersion = VERSION_PATTERN.find(matchingTag.name)?.groupValues?.get(1) ?: matchingTag.name
                        Log.d(TAG, "${emulator.id}: resolved commit $installedVersion to tag ${matchingTag.name} -> $tagVersion")
                        resolvedInstalledVersion = tagVersion
                    }
                }
            }

            val currentVersionInfo = resolvedInstalledVersion?.let { VersionInfo.parse(it) }

            val matchResult = ApkAssetMatcher.matchApk(
                assets = release.assets,
                storedVariant = storedVariant
            )

            if (matchResult is ApkMatchResult.NoMatch) {
                return EmulatorUpdateCheckResult.Error(
                    emulator.id,
                    "No APK found in release"
                )
            }

            val hasUpdate = when {
                // No installed version means we can't compare
                resolvedInstalledVersion == null -> false
                // Both have valid semver - use semver comparison
                currentVersionInfo != null && latestVersion != null -> latestVersion > currentVersionInfo
                // Installed version is an unresolved commit hash -- assume release is newer
                currentVersionInfo == null && isCommitHash(resolvedInstalledVersion) && latestVersion != null -> true
                // If GitHub version couldn't be parsed but installed can, try extracting semver from installed
                latestVersion != null && currentVersionInfo == null -> {
                    val extractedInstalled = VERSION_PATTERN.find(resolvedInstalledVersion)?.groupValues?.get(1)
                    val extractedInfo = extractedInstalled?.let { VersionInfo.parse(it) }
                    extractedInfo != null && latestVersion > extractedInfo
                }
                // Can't compare - skip
                else -> false
            }

            if (hasUpdate) {
                Log.d(TAG, "Update available for ${emulator.id}: $installedVersion -> $extractedVersion")

                when (matchResult) {
                    is ApkMatchResult.SingleMatch -> {
                        emulatorUpdateDao.upsert(
                            EmulatorUpdateEntity(
                                emulatorId = emulator.id,
                                latestVersion = extractedVersion,
                                installedVersion = installedVersion,
                                downloadUrl = matchResult.asset.downloadUrl,
                                assetName = matchResult.asset.name,
                                assetSize = matchResult.asset.size,
                                checkedAt = Instant.now(),
                                installedVariant = storedVariant ?: matchResult.variant,
                                hasUpdate = true
                            )
                        )
                    }
                    is ApkMatchResult.MultipleMatches -> {
                        val firstAsset = matchResult.assets.first()
                        emulatorUpdateDao.upsert(
                            EmulatorUpdateEntity(
                                emulatorId = emulator.id,
                                latestVersion = extractedVersion,
                                installedVersion = installedVersion,
                                downloadUrl = firstAsset.downloadUrl,
                                assetName = firstAsset.name,
                                assetSize = firstAsset.size,
                                checkedAt = Instant.now(),
                                installedVariant = null,
                                hasUpdate = true
                            )
                        )
                    }
                    ApkMatchResult.NoMatch -> {}
                }

                EmulatorUpdateCheckResult.UpdateAvailable(
                    emulatorId = emulator.id,
                    currentVersion = installedVersion,
                    latestVersion = extractedVersion,
                    release = release,
                    matchResult = matchResult
                )
            } else {
                Log.d(TAG, "Up to date: ${emulator.id} (${release.tagName})")

                emulatorUpdateDao.upsert(
                    EmulatorUpdateEntity(
                        emulatorId = emulator.id,
                        latestVersion = extractedVersion,
                        installedVersion = installedVersion,
                        downloadUrl = when (matchResult) {
                            is ApkMatchResult.SingleMatch -> matchResult.asset.downloadUrl
                            is ApkMatchResult.MultipleMatches -> matchResult.assets.first().downloadUrl
                            ApkMatchResult.NoMatch -> ""
                        },
                        assetName = when (matchResult) {
                            is ApkMatchResult.SingleMatch -> matchResult.asset.name
                            is ApkMatchResult.MultipleMatches -> matchResult.assets.first().name
                            ApkMatchResult.NoMatch -> ""
                        },
                        assetSize = when (matchResult) {
                            is ApkMatchResult.SingleMatch -> matchResult.asset.size
                            is ApkMatchResult.MultipleMatches -> matchResult.assets.first().size
                            ApkMatchResult.NoMatch -> 0
                        },
                        checkedAt = Instant.now(),
                        installedVariant = storedVariant,
                        hasUpdate = false
                    )
                )

                EmulatorUpdateCheckResult.UpToDate(
                    emulatorId = emulator.id,
                    version = release.tagName
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Check failed for ${emulator.id}", e)
            EmulatorUpdateCheckResult.Error(
                emulator.id,
                e.message ?: "Unknown error"
            )
        }
    }

    suspend fun getAvailableUpdates(): List<EmulatorUpdateEntity> {
        return emulatorUpdateDao.getAvailableUpdates()
    }

    suspend fun getCachedUpdate(emulatorId: String): EmulatorUpdateEntity? {
        return emulatorUpdateDao.getByEmulatorId(emulatorId)
    }

    suspend fun markAsInstalled(emulatorId: String, version: String, variant: String?) {
        emulatorUpdateDao.markAsInstalled(emulatorId, version)
        if (variant != null) {
            emulatorUpdateDao.updateInstalledVariant(emulatorId, variant)
        }
    }

    suspend fun clearVariant(emulatorId: String) {
        emulatorUpdateDao.clearInstalledVariant(emulatorId)
    }

    suspend fun fetchLatestRelease(emulator: EmulatorDef): FetchReleaseResult {
        val source = emulator.releaseSource ?: return FetchReleaseResult.Error("No release source configured")

        return try {
            val release = fetchReleaseFromSource(source)
                ?: return FetchReleaseResult.Error("Release API request failed")

            // Try to extract version from release info first
            var version = extractVersionFromRelease(release)

            // If no semver found and tag looks like a commit hash, fetch tags to find version
            if (version == null && isCommitHash(release.tagName)) {
                fetchTagsFromSource(source)?.let { tags ->
                    version = findVersionFromTags(tags, release.tagName)
                }
            }

            // Final fallback to tag name
            version = version ?: release.tagName

            val matchResult = ApkAssetMatcher.matchApk(
                assets = release.assets,
                storedVariant = null
            )

            when (matchResult) {
                is ApkMatchResult.SingleMatch -> FetchReleaseResult.Success(
                    version = version,
                    downloadUrl = matchResult.asset.downloadUrl,
                    assetName = matchResult.asset.name,
                    assetSize = matchResult.asset.size,
                    variant = matchResult.variant
                )
                is ApkMatchResult.MultipleMatches -> FetchReleaseResult.MultipleVariants(
                    version = version,
                    variants = matchResult.assets.map { asset ->
                        VariantInfo(
                            variant = ApkAssetMatcher.extractVariantFromAssetName(asset.name) ?: asset.name,
                            downloadUrl = asset.downloadUrl,
                            assetName = asset.name,
                            assetSize = asset.size
                        )
                    }
                )
                ApkMatchResult.NoMatch -> FetchReleaseResult.Error("No APK found in release")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed for ${emulator.id}", e)
            FetchReleaseResult.Error(e.message ?: "Unknown error")
        }
    }
}

sealed class FetchReleaseResult {
    data class Success(
        val version: String,
        val downloadUrl: String,
        val assetName: String,
        val assetSize: Long,
        val variant: String?
    ) : FetchReleaseResult()

    data class MultipleVariants(
        val version: String,
        val variants: List<VariantInfo>
    ) : FetchReleaseResult()

    data class Error(val message: String) : FetchReleaseResult()
}

data class VariantInfo(
    val variant: String,
    val downloadUrl: String,
    val assetName: String,
    val assetSize: Long
)

object VersionFormatter {
    private val SEMVER_REGEX = Regex("""^v?(\d+\.\d+(?:\.\d+)?(?:-[\w.]+)?)$""")
    private val DATE_REGEX = Regex("""(\d{4})[-.]?(\d{2})[-.]?(\d{2})""")
    private val HEX_REGEX = Regex("""^v?[0-9a-fA-F]{7,40}$""")

    fun formatForDisplay(version: String): String {
        val trimmed = version.trim()

        // Check for semver (v1.2.3 or 1.2.3-beta)
        SEMVER_REGEX.find(trimmed)?.let { match ->
            return match.groupValues[1]
        }

        // Check for date-based versions (nightly-2024-01-15, 20240115, etc.)
        DATE_REGEX.find(trimmed)?.let { match ->
            val year = match.groupValues[1]
            val month = match.groupValues[2]
            val day = match.groupValues[3]
            return "$year-$month-$day"
        }

        // Check for commit hash (truncate to 7 chars)
        if (HEX_REGEX.matches(trimmed)) {
            val hash = trimmed.removePrefix("v")
            return hash.take(7)
        }

        // Special cases
        if (trimmed.equals("release", ignoreCase = true) ||
            trimmed.equals("nightly", ignoreCase = true)) {
            return "Latest"
        }

        // Fallback: return as-is but limit length
        return if (trimmed.length > 12) trimmed.take(12) + "..." else trimmed
    }
}

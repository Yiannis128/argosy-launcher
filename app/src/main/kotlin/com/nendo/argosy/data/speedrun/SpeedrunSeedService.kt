package com.nendo.argosy.data.speedrun

import com.nendo.argosy.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SpeedrunSeedService"
private const val THERUN_BASE = "https://therun.gg/api"
private const val THERUN_HISTORY_BASE = "https://d1qsrp2avfthuv.cloudfront.net"
private const val SRC_BASE = "https://www.speedrun.com/api/v1"
private const val MAX_PLACING_FALLBACKS = 5

data class SeedCategory(
    val label: String,
    val source: Source,
    val gameDisplayName: String? = null,
    val leaderboardUsernames: List<String> = emptyList()
) {
    enum class Source { THERUN, SPEEDRUN_COM }
}

data class SeedTemplate(
    val segments: List<String>,
    val runnerUsername: String
)

@Singleton
class SpeedrunSeedService @Inject constructor() {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    suspend fun fetchSeedCategories(gameTitle: String): List<SeedCategory> = withContext(Dispatchers.IO) {
        val theRun = fetchTheRunCategories(gameTitle)
        val src = fetchSpeedrunComCategoryNames(gameTitle)
        val seenNames = theRun.map { it.label.lowercase() }.toMutableSet()
        val merged = theRun.toMutableList()
        src.forEach { name ->
            if (seenNames.add(name.lowercase())) {
                merged.add(SeedCategory(label = name, source = SeedCategory.Source.SPEEDRUN_COM))
            }
        }
        merged
    }

    suspend fun fetchTheRunTemplate(category: SeedCategory): SeedTemplate? = withContext(Dispatchers.IO) {
        val gameName = category.gameDisplayName ?: return@withContext null
        category.leaderboardUsernames.take(MAX_PLACING_FALLBACKS).forEach { username ->
            val template = runCatching { fetchTemplateFromUser(username, gameName, category.label) }.getOrNull()
            if (template != null) return@withContext template
        }
        null
    }

    private fun fetchTheRunCategories(gameTitle: String): List<SeedCategory> {
        return try {
        val query = URLEncoder.encode(gameTitle, "UTF-8")
        val searchBody = get("$THERUN_BASE/games?query=$query") ?: return emptyList()
        val items = JSONObject(searchBody).optJSONArray("items") ?: return emptyList()
        if (items.length() == 0) return emptyList()
        val gameKey = items.getJSONObject(0).optString("objectID")
        if (gameKey.isBlank()) return emptyList()

        val detailBody = get("$THERUN_BASE/games/${URLEncoder.encode(gameKey, "UTF-8")}") ?: return emptyList()
        val leaderboards = JSONObject(detailBody)
            .optJSONObject("stats")
            ?.optJSONArray("categoryLeaderboards")
            ?: return emptyList()

        buildList {
            for (i in 0 until leaderboards.length()) {
                val board = leaderboards.getJSONObject(i)
                val display = board.optString("categoryNameDisplay")
                    .ifBlank { board.optString("categoryName") }
                if (display.isBlank()) continue
                val pb = board.optJSONArray("pbLeaderboard") ?: JSONArray()
                if (pb.length() == 0) continue
                val usernames = buildList {
                    for (j in 0 until pb.length()) {
                        pb.getJSONObject(j).optString("username").takeIf { it.isNotBlank() }?.let(::add)
                    }
                }
                val gameDisplay = (0 until pb.length())
                    .asSequence()
                    .map { pb.getJSONObject(it).optString("game") }
                    .firstOrNull { it.isNotBlank() }
                add(
                    SeedCategory(
                        label = display,
                        source = SeedCategory.Source.THERUN,
                        gameDisplayName = gameDisplay,
                        leaderboardUsernames = usernames
                    )
                )
            }
        }
        } catch (e: Exception) {
            Logger.debug(TAG, "therun.gg category fetch failed: ${e.message}")
            emptyList()
        }
    }

    private fun fetchTemplateFromUser(username: String, gameName: String, categoryLabel: String): SeedTemplate? {
        val body = get("$THERUN_BASE/users/${URLEncoder.encode(username, "UTF-8")}") ?: return null
        val runs = JSONArray(body)
        var historyFile: String? = null
        for (i in 0 until runs.length()) {
            val run = runs.getJSONObject(i)
            if (run.optString("game").equals(gameName, ignoreCase = true) &&
                run.optString("run").equals(categoryLabel, ignoreCase = true)
            ) {
                historyFile = run.optString("historyFilename").takeIf { it.isNotBlank() }
                break
            }
        }
        val file = historyFile ?: return null
        val historyBody = get("$THERUN_HISTORY_BASE/$file") ?: return null
        val splits = JSONObject(historyBody).optJSONArray("splits") ?: return null
        val segments = buildList {
            for (i in 0 until splits.length()) {
                splits.getJSONObject(i).optString("name").takeIf { it.isNotBlank() }?.let(::add)
            }
        }
        return if (segments.isNotEmpty()) SeedTemplate(segments, username) else null
    }

    private fun fetchSpeedrunComCategoryNames(gameTitle: String): List<String> {
        return try {
        val query = URLEncoder.encode(gameTitle, "UTF-8")
        val searchBody = get("$SRC_BASE/games?name=$query&max=1") ?: return emptyList()
        val games = JSONObject(searchBody).optJSONArray("data") ?: return emptyList()
        if (games.length() == 0) return emptyList()
        val gameId = games.getJSONObject(0).optString("id")
        if (gameId.isBlank()) return emptyList()

        val categoriesBody = get("$SRC_BASE/games/$gameId/categories") ?: return emptyList()
        val categories = JSONObject(categoriesBody).optJSONArray("data") ?: return emptyList()
        buildList {
            for (i in 0 until categories.length()) {
                val category = categories.getJSONObject(i)
                if (category.optString("type") != "per-game") continue
                category.optString("name").takeIf { it.isNotBlank() }?.let(::add)
            }
        }
        } catch (e: Exception) {
            Logger.debug(TAG, "speedrun.com category fetch failed: ${e.message}")
            emptyList()
        }
    }

    private fun get(url: String): String? {
        val request = Request.Builder().url(url).header("User-Agent", "argosy-launcher").build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) null else response.body?.string()
            }
        } catch (e: Exception) {
            Logger.debug(TAG, "GET $url failed: ${e.message}")
            null
        }
    }
}

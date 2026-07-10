package com.nendo.argosy.ui.theme.backdrop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nendo.argosy.data.repository.PlatformRepository
import com.nendo.argosy.ui.components.PlatformIconAssets
import com.nendo.argosy.util.SafeCoroutineScope
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

val LocalBackdropStamps = staticCompositionLocalOf<BackdropStampProvider?> { null }

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BackdropStampEntryPoint {
    fun backdropStampProvider(): BackdropStampProvider
}

fun backdropStampProvider(context: Context): BackdropStampProvider =
    EntryPointAccessors.fromApplication(context.applicationContext, BackdropStampEntryPoint::class.java)
        .backdropStampProvider()

private const val MAX_STAMPS = 24
private const val MAX_CACHE_ENTRIES = 4

private data class StampKey(val uris: List<String>, val sizePx: Int, val tint: Int)

/** Decodes the user's platform SVG logos into tinted monochrome mask bitmaps for the PLATFORMS backdrop preset. */
@Singleton
class BackdropStampProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    platformRepository: PlatformRepository
) {
    private val scope = SafeCoroutineScope(Dispatchers.IO, "BackdropStamps")
    private val mutex = Mutex()
    private val cache = LinkedHashMap<StampKey, List<ImageBitmap>>()

    val stampUris: StateFlow<List<String>> = platformRepository.observePlatformsWithGames()
        .map { platforms ->
            platforms.mapNotNull { PlatformIconAssets.resolveAssetUri(context, it.slug) }
                .distinct()
                .take(MAX_STAMPS)
        }
        .stateIn(scope, SharingStarted.Lazily, emptyList())

    suspend fun loadStamps(uris: List<String>, sizePx: Int, tint: Color, alpha: Float): List<ImageBitmap> {
        if (uris.isEmpty() || sizePx <= 0) return emptyList()
        val color = tint.copy(alpha = alpha.coerceIn(0f, 1f)).toArgb()
        val key = StampKey(uris, sizePx, color)
        return mutex.withLock {
            cache[key]?.let { hit ->
                cache.remove(key)
                cache[key] = hit
                return@withLock hit
            }
            val stamps = withContext(Dispatchers.IO) {
                uris.mapNotNull { decodeStamp(it, sizePx, color) }
            }
            cache[key] = stamps
            while (cache.size > MAX_CACHE_ENTRIES) cache.remove(cache.keys.first())
            stamps
        }
    }

    private suspend fun decodeStamp(uri: String, sizePx: Int, color: Int): ImageBitmap? {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .size(sizePx)
            .allowHardware(false)
            .build()
        val drawable = (context.imageLoader.execute(request) as? SuccessResult)?.drawable ?: return null
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        if (w > 0 && h > 0) {
            val scale = min(sizePx.toFloat() / w, sizePx.toFloat() / h)
            val bw = (w * scale).toInt().coerceAtLeast(1)
            val bh = (h * scale).toInt().coerceAtLeast(1)
            val left = (sizePx - bw) / 2
            val top = (sizePx - bh) / 2
            drawable.setBounds(left, top, left + bw, top + bh)
        } else {
            drawable.setBounds(0, 0, sizePx, sizePx)
        }
        drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }
}

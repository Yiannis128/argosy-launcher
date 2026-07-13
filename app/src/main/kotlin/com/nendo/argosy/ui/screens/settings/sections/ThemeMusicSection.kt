package com.nendo.argosy.ui.screens.settings.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.nendo.argosy.ui.components.NavigationPreference
import com.nendo.argosy.ui.components.SliderPreference
import com.nendo.argosy.ui.components.SwitchPreference
import com.nendo.argosy.ui.screens.settings.SettingsUiState
import com.nendo.argosy.ui.screens.settings.SettingsViewModel
import com.nendo.argosy.ui.screens.settings.components.SectionPaneLayout
import com.nendo.argosy.ui.screens.settings.menu.SettingsLayout
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.util.clickableNoFocus

internal data class ThemeMusicLayoutState(
    val bgmEnabled: Boolean,
    val bgmIsFolder: Boolean,
    val bgmIsPlaylist: Boolean,
    val musicApiSupported: Boolean
) {
    companion object {
        fun from(state: SettingsUiState) = ThemeMusicLayoutState(
            bgmEnabled = state.ambientAudio.enabled,
            bgmIsFolder = state.ambientAudio.isFolder,
            bgmIsPlaylist = state.ambientAudio.isPlaylistSource,
            musicApiSupported = state.server.musicApiSupported
        )
    }
}

internal sealed class ThemeMusicItem(
    val key: String,
    val section: String,
    val visibleWhen: (ThemeMusicLayoutState) -> Boolean = { true }
) {
    data object BgmToggle : ThemeMusicItem("bgmToggle", "music")
    data object BgmVolume : ThemeMusicItem("bgmVolume", "music", { it.bgmEnabled })
    data object BgmFile : ThemeMusicItem("bgmFile", "music", { it.bgmEnabled })
    data object BgmPlaylist : ThemeMusicItem("bgmPlaylist", "music", { it.bgmEnabled })
    data object BrowseServerMusic : ThemeMusicItem("browseServerMusic", "music", { it.bgmEnabled && it.musicApiSupported })
    data object BgmShuffle : ThemeMusicItem("bgmShuffle", "music", { it.bgmEnabled && (it.bgmIsFolder || it.bgmIsPlaylist) })

    companion object {
        val ALL: List<ThemeMusicItem> = listOf(
            BgmToggle, BgmVolume, BgmFile, BgmPlaylist, BrowseServerMusic, BgmShuffle
        )
    }
}

private val themeMusicLayout = SettingsLayout<ThemeMusicItem, ThemeMusicLayoutState>(
    allItems = ThemeMusicItem.ALL,
    isFocusable = { true },
    visibleWhen = { item, state -> item.visibleWhen(state) },
    sectionOf = { it.section },
    sectionTitle = {
        when (it) {
            "music" -> "Background Music"
            else -> null
        }
    }
)

internal fun themeMusicMaxFocusIndex(state: ThemeMusicLayoutState): Int =
    themeMusicLayout.maxFocusIndex(state)

internal fun themeMusicItemAtFocusIndex(index: Int, state: ThemeMusicLayoutState): ThemeMusicItem? =
    themeMusicLayout.itemAtFocusIndex(index, state)

internal fun themeMusicSections(state: ThemeMusicLayoutState) = themeMusicLayout.buildSections(state)

@Composable
fun ThemeMusicSection(uiState: SettingsUiState, viewModel: SettingsViewModel) {
    val bgmEnabled = uiState.ambientAudio.enabled
    val bgmIsFolder = uiState.ambientAudio.isFolder
    val bgmIsPlaylist = uiState.ambientAudio.isPlaylistSource
    val musicApiSupported = uiState.server.musicApiSupported

    val layoutState = remember(bgmEnabled, bgmIsFolder, bgmIsPlaylist, musicApiSupported) {
        ThemeMusicLayoutState(bgmEnabled, bgmIsFolder, bgmIsPlaylist, musicApiSupported)
    }

    val visibleItems = remember(layoutState) {
        themeMusicLayout.visibleItems(layoutState)
    }
    val sections = remember(layoutState) {
        themeMusicLayout.buildSections(layoutState)
    }

    fun isFocused(item: ThemeMusicItem): Boolean =
        uiState.focusedIndex == themeMusicLayout.focusIndexOf(item, layoutState)

    SectionPaneLayout(
        items = visibleItems,
        sections = sections,
        focusedIndex = uiState.focusedIndex,
        focusToListIndex = { themeMusicLayout.focusToListIndex(it, layoutState) },
        itemKey = { it.key },
        isNavItem = { false },
        isHeader = { false },
        onSectionTap = { viewModel.setFocusIndex(it.focusStartIndex) },
        modifier = Modifier.fillMaxSize().padding(Dimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) { item ->
        when (item) {
            ThemeMusicItem.BgmToggle -> SwitchPreference(
                title = "Background Music",
                subtitle = "Play music while using the launcher",
                isEnabled = uiState.ambientAudio.enabled,
                isFocused = isFocused(item),
                onToggle = { viewModel.setAmbientAudioEnabled(it) }
            )

            ThemeMusicItem.BgmVolume -> {
                val volumeLevels = listOf(2, 5, 10, 20, 35)
                val currentIndex = volumeLevels.indexOfFirst { it >= uiState.ambientAudio.volume }.takeIf { it >= 0 } ?: 0
                val sliderValue = currentIndex + 1
                SliderPreference(
                    title = "Volume",
                    value = sliderValue,
                    minValue = 1,
                    maxValue = 5,
                    isFocused = isFocused(item),
                    onClick = {
                        val nextIndex = (currentIndex + 1).mod(volumeLevels.size)
                        viewModel.setAmbientAudioVolume(volumeLevels[nextIndex])
                    }
                )
            }

            ThemeMusicItem.BgmFile -> BackgroundMusicFileItem(
                filePath = uiState.ambientAudio.audioUri,
                isFocused = isFocused(item),
                onClick = { viewModel.openAudioFileBrowser() }
            )

            ThemeMusicItem.BgmPlaylist -> {
                val trackCount = uiState.ambientAudio.playlistTrackCount
                val countLabel = when (trackCount) {
                    0 -> "No tracks"
                    1 -> "1 track"
                    else -> "$trackCount tracks"
                }
                NavigationPreference(
                    icon = Icons.Outlined.MusicNote,
                    title = "Music Playlist",
                    subtitle = if (uiState.ambientAudio.isPlaylistSource) "$countLabel - Active source" else countLabel,
                    isFocused = isFocused(item),
                    onClick = { viewModel.openBgmPlaylistManager() }
                )
            }

            ThemeMusicItem.BrowseServerMusic -> NavigationPreference(
                icon = Icons.Outlined.LibraryMusic,
                title = "Browse Server Music",
                subtitle = "Preview and download tracks from RomM",
                isFocused = isFocused(item),
                onClick = { viewModel.openMusicBrowserBgm() }
            )

            ThemeMusicItem.BgmShuffle -> SwitchPreference(
                title = "Shuffle",
                subtitle = "Randomize playback order",
                isEnabled = uiState.ambientAudio.shuffle,
                isFocused = isFocused(item),
                onToggle = { viewModel.setAmbientAudioShuffle(it) }
            )
        }
    }
}

private fun truncatePathMiddle(path: String, maxLength: Int = 40): String {
    if (path.length <= maxLength) return path

    val parts = path.split("/").filter { it.isNotEmpty() }
    if (parts.size <= 2) return path

    val first = parts.first()
    val last = parts.last()
    val secondLast = parts.getOrNull(parts.size - 2)

    val suffix = if (secondLast != null) "$secondLast/$last" else last
    val ellipsis = "/.../"

    val available = maxLength - first.length - ellipsis.length
    return if (available >= suffix.length) {
        "$first$ellipsis$suffix"
    } else {
        "$first$ellipsis$last"
    }
}

@Composable
private fun BackgroundMusicFileItem(
    filePath: String?,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val displayValue = when (filePath) {
        null -> "None selected"
        com.nendo.argosy.ui.audio.AmbientAudioManager.AMBIENT_SOURCE_PLAYLIST -> "Playlist"
        else -> truncatePathMiddle(filePath)
    }

    val focusAccent = LocalArgosyTheme.current.focusAccent
    val focusedContent = lerp(focusAccent, Color.White, 0.45f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(
                if (isFocused) focusAccent.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickableNoFocus(onClick = onClick)
            .padding(Dimens.spacingMd),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Music File(s)",
            style = MaterialTheme.typography.titleMedium,
            color = if (isFocused) focusedContent
                    else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = displayValue,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFocused) focusedContent.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package com.nendo.argosy.ui.screens.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nendo.argosy.ui.components.NavigationPreference
import com.nendo.argosy.ui.components.SliderPreference
import com.nendo.argosy.ui.components.SwitchPreference
import com.nendo.argosy.ui.screens.settings.SettingsUiState
import com.nendo.argosy.ui.screens.settings.SettingsViewModel
import com.nendo.argosy.ui.screens.settings.components.SectionPaneLayout
import com.nendo.argosy.ui.screens.settings.menu.SettingsLayout
import com.nendo.argosy.ui.theme.Dimens

internal data class ThemeMusicLayoutState(
    val bgmEnabled: Boolean,
    val musicApiSupported: Boolean
) {
    companion object {
        fun from(state: SettingsUiState) = ThemeMusicLayoutState(
            bgmEnabled = state.ambientAudio.enabled,
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
    data object BgmPlaylist : ThemeMusicItem("bgmPlaylist", "music", { it.bgmEnabled })
    data object BrowseServerMusic : ThemeMusicItem("browseServerMusic", "music", { it.bgmEnabled && it.musicApiSupported })
    data object BrowseLocalMusic : ThemeMusicItem("browseLocalMusic", "music", { it.bgmEnabled })
    data object BgmShuffle : ThemeMusicItem("bgmShuffle", "music", { it.bgmEnabled })

    companion object {
        val ALL: List<ThemeMusicItem> = listOf(
            BgmToggle, BgmVolume, BgmPlaylist, BrowseServerMusic, BrowseLocalMusic, BgmShuffle
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
    val musicApiSupported = uiState.server.musicApiSupported

    val layoutState = remember(bgmEnabled, musicApiSupported) {
        ThemeMusicLayoutState(bgmEnabled, musicApiSupported)
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

            ThemeMusicItem.BgmPlaylist -> {
                val entryCount = uiState.ambientAudio.playlistEntryCount
                val countLabel = when (entryCount) {
                    0 -> "No tracks"
                    1 -> "1 track"
                    else -> "$entryCount tracks"
                }
                NavigationPreference(
                    icon = Icons.Outlined.MusicNote,
                    title = "Music Playlist",
                    subtitle = countLabel,
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

            ThemeMusicItem.BrowseLocalMusic -> NavigationPreference(
                icon = Icons.Outlined.FolderOpen,
                title = "Browse Local Music",
                subtitle = "Add files or folders to the playlist",
                isFocused = isFocused(item),
                onClick = { viewModel.openBgmAddMusicBrowser() }
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


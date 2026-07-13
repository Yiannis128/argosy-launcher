package com.nendo.argosy.ui.screens.musicbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.nendo.argosy.core.input.SoundType
import com.nendo.argosy.data.remote.romm.RomMMusicFacet
import com.nendo.argosy.ui.components.FocusedScroll
import com.nendo.argosy.ui.components.FooterHints
import com.nendo.argosy.ui.components.InputButton
import com.nendo.argosy.ui.input.InputHandler
import com.nendo.argosy.ui.input.InputResult
import com.nendo.argosy.ui.input.ModalInputEffect
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.theme.LocalLauncherTheme
import com.nendo.argosy.ui.util.clickableNoFocus

@Composable
fun MusicBrowserScreen(
    mode: MusicBrowserMode,
    sfxTargetLabel: String? = null,
    onSfxSelected: ((String) -> Unit)? = null,
    onDismiss: () -> Unit,
    viewModel: MusicBrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnSfxSelected by rememberUpdatedState(onSfxSelected)

    LaunchedEffect(mode) {
        viewModel.open(mode)
    }

    LaunchedEffect(Unit) {
        viewModel.sfxAssignedEvent.collect { path ->
            currentOnSfxSelected?.invoke(path)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopPreview() }
    }

    val inputHandler = remember(viewModel) {
        object : InputHandler {
            override fun onUp(): InputResult {
                val st = viewModel.uiState.value
                if (st.facetPicker != null) viewModel.moveFacetFocus(-1) else viewModel.moveFocus(-1)
                return InputResult.HANDLED
            }

            override fun onDown(): InputResult {
                val st = viewModel.uiState.value
                if (st.facetPicker != null) viewModel.moveFacetFocus(1) else viewModel.moveFocus(1)
                return InputResult.HANDLED
            }

            override fun onLeft(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onRight(): InputResult = InputResult.handled(SoundType.SILENT)

            override fun onConfirm(): InputResult {
                val st = viewModel.uiState.value
                when {
                    st.facetPicker != null -> viewModel.confirmFacetSelection()
                    st.errorMessage != null -> viewModel.retry()
                    st.focusedIndex == -1 -> viewModel.focusSearch()
                    else -> viewModel.confirmRow()
                }
                return InputResult.HANDLED
            }

            override fun onBack(): InputResult {
                val st = viewModel.uiState.value
                when {
                    st.facetPicker != null -> viewModel.dismissFacetPicker()
                    st.previewingId != null -> viewModel.stopPreview()
                    else -> currentOnDismiss()
                }
                return InputResult.HANDLED
            }

            override fun onSecondaryAction(): InputResult {
                val st = viewModel.uiState.value
                if (st.facetPicker != null) return InputResult.handled(SoundType.SILENT)
                viewModel.removeFocusedFromPlaylist()
                return InputResult.HANDLED
            }

            override fun onContextMenu(): InputResult {
                val st = viewModel.uiState.value
                if (st.facetPicker != null) return InputResult.handled(SoundType.SILENT)
                viewModel.togglePreview()
                return InputResult.HANDLED
            }

            override fun onPrevSection(): InputResult {
                val st = viewModel.uiState.value
                if (st.facetPicker == null) viewModel.openFacetChooser()
                return InputResult.HANDLED
            }

            override fun onNextSection(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onPrevTrigger(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onNextTrigger(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onMenu(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onSelect(): InputResult = InputResult.handled(SoundType.SILENT)
        }
    }

    ModalInputEffect(active = true, handler = inputHandler)

    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.showKeyboard) {
        if (uiState.showKeyboard) {
            focusRequester.requestFocus()
        } else {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(uiState.focusedIndex, uiState.tracks.size) {
        if (uiState.focusedIndex >= 0 && uiState.focusedIndex < uiState.tracks.size) {
            val viewportHeight = listState.layoutInfo.viewportEndOffset
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val avgItemHeight = if (visibleItems.isNotEmpty()) {
                visibleItems.sumOf { it.size } / visibleItems.size
            } else 64
            val targetOffset = (viewportHeight / 2) - (avgItemHeight / 2)
            listState.animateScrollToItem(uiState.focusedIndex, -targetOffset)
        }
    }

    LaunchedEffect(listState, uiState.tracks.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                val count = viewModel.uiState.value.tracks.size
                if (lastVisible != null && count > 0 && lastVisible >= count - 6) {
                    viewModel.onListEndApproached()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickableNoFocus {}
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MusicBrowserHeader(
                mode = mode,
                sfxTargetLabel = sfxTargetLabel,
                notice = uiState.notice,
                onBack = onDismiss
            )

            MusicSearchField(
                query = uiState.searchQuery,
                isSearchFocused = uiState.focusedIndex == -1,
                focusRequester = focusRequester,
                onQueryChange = { viewModel.updateSearch(it) },
                onTap = { viewModel.focusSearch() }
            )

            MusicFilterChips(
                artistFilter = uiState.artistFilter,
                albumFilter = uiState.albumFilter,
                genreFilter = uiState.genreFilter,
                hasActiveFilters = uiState.hasActiveFilters,
                onFacetTap = { viewModel.openFacetValues(it) },
                onClearAll = { viewModel.clearFilters() }
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> MusicBrowserLoading()
                    uiState.isOffline -> MusicBrowserMessage(
                        icon = Icons.Outlined.CloudOff,
                        message = "Not connected to your RomM server"
                    )
                    uiState.isUnsupported -> MusicBrowserMessage(
                        icon = Icons.Outlined.MusicOff,
                        message = "This RomM server does not support music browsing yet"
                    )
                    uiState.errorMessage != null -> MusicBrowserError(
                        message = uiState.errorMessage ?: "Something went wrong",
                        onRetry = { viewModel.retry() }
                    )
                    uiState.tracks.isEmpty() -> MusicBrowserMessage(
                        icon = Icons.Outlined.MusicNote,
                        message = "No tracks found"
                    )
                    else -> LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            start = Dimens.spacingLg,
                            end = Dimens.spacingLg,
                            top = Dimens.spacingSm,
                            bottom = Dimens.footerHeight
                        ),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                    ) {
                        itemsIndexed(uiState.tracks, key = { _, track -> track.romFileId }) { index, track ->
                            MusicTrackRow(
                                track = track,
                                isFocused = uiState.focusedIndex == index,
                                isDownloaded = uiState.isDownloaded(track),
                                isDownloading = track.romFileId in uiState.downloadingIds,
                                isInPlaylist = mode == MusicBrowserMode.BGM && uiState.isInPlaylist(track),
                                isPreviewing = uiState.previewingId == track.romFileId,
                                onClick = {
                                    viewModel.setFocusIndex(index)
                                    viewModel.confirmRow(index)
                                },
                                onPreview = { viewModel.togglePreview(index) },
                                onRemove = {
                                    viewModel.setFocusIndex(index)
                                    viewModel.removeFocusedFromPlaylist()
                                }
                            )
                        }
                        if (uiState.isLoadingMore) {
                            item(key = "loading-more") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimens.spacingMd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(Dimens.iconMd))
                                }
                            }
                        }
                    }
                }
            }
        }

        uiState.facetPicker?.let { picker ->
            FacetPickerPopup(
                picker = picker,
                onSelect = { viewModel.confirmFacetSelection(it) },
                onDismiss = { viewModel.dismissFacetPicker() }
            )
        }

        val focusedTrack = uiState.tracks.getOrNull(uiState.focusedIndex)
        val hints = if (uiState.facetPicker != null || uiState.isUnsupported || uiState.isOffline) {
            emptyList()
        } else {
            buildList {
                add(InputButton.LB to "Filters")
                if (focusedTrack != null) {
                    add(InputButton.X to if (uiState.previewingId == focusedTrack.romFileId) "Stop" else "Preview")
                    if (mode == MusicBrowserMode.BGM && uiState.isInPlaylist(focusedTrack)) {
                        add(InputButton.Y to "Remove")
                    }
                    add(InputButton.A to if (mode == MusicBrowserMode.BGM) "Add" else "Assign")
                }
            }
        }

        FooterHints(
            hints = hints,
            onHintClick = { button ->
                when (button) {
                    InputButton.A -> inputHandler.onConfirm()
                    InputButton.B -> inputHandler.onBack()
                    InputButton.X -> inputHandler.onContextMenu()
                    InputButton.Y -> inputHandler.onSecondaryAction()
                    InputButton.LB -> inputHandler.onPrevSection()
                    else -> {}
                }
            }
        )
    }
}

@Composable
private fun MusicBrowserHeader(
    mode: MusicBrowserMode,
    sfxTargetLabel: String?,
    notice: String?,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(Dimens.spacingSm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Server Music",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = when (mode) {
                    MusicBrowserMode.BGM -> "Add tracks to the BGM playlist"
                    MusicBrowserMode.SFX -> "Assign a sound for ${sfxTargetLabel ?: "this action"}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        notice?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = Dimens.spacingMd)
            )
        }
    }
}

@Composable
private fun MusicSearchField(
    query: String,
    isSearchFocused: Boolean,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onTap: () -> Unit
) {
    val focusAccent = LocalArgosyTheme.current.focusAccent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingXs)
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(
                if (isSearchFocused) focusAccent.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surfaceVariant)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickableNoFocus(onClick = onTap)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = if (isSearchFocused) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconSm)
        )
        Spacer(modifier = Modifier.width(Dimens.spacingSm))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search tracks...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
    }
}

@Composable
private fun MusicFilterChips(
    artistFilter: String?,
    albumFilter: String?,
    genreFilter: String?,
    hasActiveFilters: Boolean,
    onFacetTap: (RomMMusicFacet) -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(label = artistFilter ?: "Artist", isActive = artistFilter != null) {
            onFacetTap(RomMMusicFacet.ARTISTS)
        }
        FilterChip(label = albumFilter ?: "Album", isActive = albumFilter != null) {
            onFacetTap(RomMMusicFacet.ALBUMS)
        }
        FilterChip(label = genreFilter ?: "Genre", isActive = genreFilter != null) {
            onFacetTap(RomMMusicFacet.GENRES)
        }
        if (hasActiveFilters) {
            FilterChip(label = "Clear", isActive = false, onClick = onClearAll)
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.radiusPill))
            .background(
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXs)
    )
}

@Composable
private fun MusicTrackRow(
    track: MusicTrackUi,
    isFocused: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    isInPlaylist: Boolean,
    isPreviewing: Boolean,
    onClick: () -> Unit,
    onPreview: () -> Unit,
    onRemove: () -> Unit
) {
    val focusAccent = LocalArgosyTheme.current.focusAccent
    val focusedContent = lerp(focusAccent, Color.White, 0.45f)
    val successColor = LocalLauncherTheme.current.semanticColors.success

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(
                if (isFocused) focusAccent.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surface)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDownloading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconMd),
                strokeWidth = Dimens.borderMedium
            )
        } else {
            Icon(
                imageVector = if (isPreviewing) Icons.Default.GraphicEq else Icons.Outlined.MusicNote,
                contentDescription = null,
                tint = when {
                    isPreviewing -> MaterialTheme.colorScheme.primary
                    isFocused -> focusedContent
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(Dimens.iconMd)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.spacingMd))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isFocused) focusedContent else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            track.artistAlbum?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFocused) focusedContent.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            track.gameLine?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFocused) focusedContent.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.spacingSm))

        if (isInPlaylist) {
            StatusIcon(
                icon = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "In playlist"
            )
        } else if (isDownloaded) {
            StatusIcon(
                icon = Icons.Default.DownloadDone,
                tint = successColor,
                contentDescription = "Downloaded"
            )
        }

        track.durationLabel?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) focusedContent.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Dimens.spacingSm)
            )
        }

        IconButton(onClick = onPreview) {
            Icon(
                imageVector = if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isPreviewing) "Stop preview" else "Preview",
                tint = if (isPreviewing) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isInPlaylist) {
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove from playlist",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusIcon(
    icon: ImageVector,
    tint: Color,
    contentDescription: String
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .padding(horizontal = Dimens.spacingXs)
            .size(Dimens.iconSm)
    )
}

@Composable
private fun MusicBrowserLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MusicBrowserMessage(
    icon: ImageVector,
    message: String
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(Dimens.iconXl)
            )
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MusicBrowserError(
    message: String,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .clickableNoFocus(onClick = onRetry)
                    .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
            )
        }
    }
}

@Composable
private fun FacetPickerPopup(
    picker: FacetPickerUi,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val isDarkTheme = LocalLauncherTheme.current.isDarkTheme
    val overlayColor = if (isDarkTheme) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f)
    val focusAccent = LocalArgosyTheme.current.focusAccent
    val focusedContent = lerp(focusAccent, Color.White, 0.45f)

    FocusedScroll(listState = listState, focusedIndex = picker.focusIndex)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayColor)
            .clickableNoFocus(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        val maxModalHeight = maxHeight * 0.85f

        Column(
            modifier = Modifier
                .width(Dimens.modalWidthLg)
                .heightIn(max = maxModalHeight)
                .clip(RoundedCornerShape(Dimens.radiusPanel))
                .background(MaterialTheme.colorScheme.surface)
                .clickableNoFocus(enabled = false) {}
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Text(
                text = picker.title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (picker.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spacingXl),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f, fill = false),
                    contentPadding = PaddingValues(vertical = Dimens.spacingXs),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                ) {
                    itemsIndexed(picker.options, key = { index, option -> "$index:$option" }) { index, option ->
                        val isFocused = picker.focusIndex == index
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isFocused) focusedContent else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.radiusMd))
                                .background(
                                    if (isFocused) focusAccent.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickableNoFocus { onSelect(index) }
                                .padding(Dimens.spacingMd)
                        )
                    }
                }
            }
        }
    }
}

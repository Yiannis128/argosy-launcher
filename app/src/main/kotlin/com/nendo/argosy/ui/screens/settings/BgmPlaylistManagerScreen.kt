package com.nendo.argosy.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nendo.argosy.core.input.SoundType
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
fun BgmPlaylistManagerScreen(
    onAddMusic: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: BgmPlaylistManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentOnAddMusic by rememberUpdatedState(onAddMusic)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    val inputHandler = remember(viewModel) {
        object : InputHandler {
            override fun onUp(): InputResult {
                val st = viewModel.uiState.value
                if (st.isReordering) viewModel.moveFocusedRow(-1) else viewModel.moveFocus(-1)
                return InputResult.HANDLED
            }

            override fun onDown(): InputResult {
                val st = viewModel.uiState.value
                if (st.isReordering) viewModel.moveFocusedRow(1) else viewModel.moveFocus(1)
                return InputResult.HANDLED
            }

            override fun onLeft(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onRight(): InputResult = InputResult.handled(SoundType.SILENT)

            override fun onConfirm(): InputResult {
                val st = viewModel.uiState.value
                if (st.entries.isEmpty()) return InputResult.handled(SoundType.SILENT)
                if (st.isReordering) viewModel.commitReorder() else viewModel.beginReorder()
                return InputResult.HANDLED
            }

            override fun onBack(): InputResult {
                val st = viewModel.uiState.value
                if (st.isReordering) viewModel.cancelReorder() else currentOnDismiss()
                return InputResult.HANDLED
            }

            override fun onSecondaryAction(): InputResult {
                val st = viewModel.uiState.value
                if (st.isReordering || st.entries.isEmpty()) return InputResult.handled(SoundType.SILENT)
                viewModel.removeFocused()
                return InputResult.HANDLED
            }

            override fun onContextMenu(): InputResult {
                val st = viewModel.uiState.value
                if (st.isReordering) return InputResult.handled(SoundType.SILENT)
                currentOnAddMusic()
                return InputResult.HANDLED
            }

            override fun onPrevSection(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onNextSection(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onPrevTrigger(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onNextTrigger(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onMenu(): InputResult = InputResult.handled(SoundType.SILENT)
            override fun onSelect(): InputResult = InputResult.handled(SoundType.SILENT)
        }
    }

    ModalInputEffect(active = true, handler = inputHandler)

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.focusedIndex, uiState.entries.size) {
        if (uiState.entries.isNotEmpty() && uiState.focusedIndex in uiState.entries.indices) {
            val viewportHeight = listState.layoutInfo.viewportEndOffset
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val avgItemHeight = if (visibleItems.isNotEmpty()) {
                visibleItems.sumOf { it.size } / visibleItems.size
            } else 64
            val targetOffset = (viewportHeight / 2) - (avgItemHeight / 2)
            listState.animateScrollToItem(uiState.focusedIndex, -targetOffset)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickableNoFocus {}
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BgmPlaylistHeader(
                onBack = onDismiss,
                onAddMusic = onAddMusic
            )

            if (uiState.entries.isEmpty()) {
                BgmPlaylistEmptyState()
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = Dimens.spacingLg,
                        end = Dimens.spacingLg,
                        top = Dimens.spacingSm,
                        bottom = Dimens.footerHeight
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                ) {
                    itemsIndexed(uiState.entries, key = { _, row -> row.id }) { index, row ->
                        BgmPlaylistEntryRow(
                            row = row,
                            position = index + 1,
                            isFocused = uiState.focusedIndex == index,
                            isBeingMoved = uiState.isReordering && uiState.focusedIndex == index,
                            canMoveUp = index > 0,
                            canMoveDown = index < uiState.entries.lastIndex,
                            onClick = { viewModel.setFocusIndex(index) },
                            onMoveUp = { viewModel.moveRow(index, -1) },
                            onMoveDown = { viewModel.moveRow(index, 1) },
                            onRemove = { viewModel.removeAt(index) }
                        )
                    }
                }
            }
        }

        val hints = when {
            uiState.isReordering -> listOf(
                InputButton.DPAD_VERTICAL to "Move",
                InputButton.A to "Done",
                InputButton.B to "Cancel"
            )
            uiState.entries.isEmpty() -> listOf(
                InputButton.X to "Add Music"
            )
            else -> listOf(
                InputButton.A to "Move",
                InputButton.X to "Add Music",
                InputButton.Y to "Remove"
            )
        }

        FooterHints(
            hints = hints,
            onHintClick = { button ->
                when (button) {
                    InputButton.A -> inputHandler.onConfirm()
                    InputButton.B -> inputHandler.onBack()
                    InputButton.X -> inputHandler.onContextMenu()
                    InputButton.Y -> inputHandler.onSecondaryAction()
                    else -> {}
                }
            }
        )
    }
}

@Composable
private fun BgmPlaylistHeader(
    onBack: () -> Unit,
    onAddMusic: () -> Unit
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

        Text(
            text = "Music Playlist",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.radiusMd))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .clickableNoFocus(onClick = onAddMusic)
                .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconSm)
            )
            Spacer(modifier = Modifier.width(Dimens.spacingXs))
            Text(
                text = "Add",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BgmPlaylistEntryRow(
    row: BgmPlaylistRowUi,
    position: Int,
    isFocused: Boolean,
    isBeingMoved: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val focusAccent = LocalArgosyTheme.current.focusAccent
    val focusedContentColor = lerp(focusAccent, Color.White, 0.45f)
    val warningColor = LocalLauncherTheme.current.semanticColors.warning

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(
                when {
                    isBeingMoved -> focusAccent.copy(alpha = 0.3f).compositeOver(MaterialTheme.colorScheme.surface)
                    isFocused -> focusAccent.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surface)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }
            )
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isBeingMoved) {
            Icon(
                Icons.Default.DragHandle,
                contentDescription = "Moving",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconMd)
            )
        } else {
            Icon(
                if (row.isFolder) Icons.Outlined.Folder else Icons.Outlined.MusicNote,
                contentDescription = null,
                tint = if (isFocused) focusedContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.spacingMd))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$position. ${row.displayName}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isFocused) focusedContentColor else MaterialTheme.colorScheme.onSurface
            )
            when {
                row.isMissing && row.isFolder -> Text(
                    text = "Folder missing - skipped during playback",
                    style = MaterialTheme.typography.bodySmall,
                    color = warningColor
                )
                row.isMissing -> Text(
                    text = "File missing - skipped during playback",
                    style = MaterialTheme.typography.bodySmall,
                    color = warningColor
                )
                row.isFolder -> Text(
                    text = when (row.folderTrackCount) {
                        null, 0 -> "Folder - empty"
                        1 -> "Folder - 1 track"
                        else -> "Folder - ${row.folderTrackCount} tracks"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFocused) focusedContentColor.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = onMoveUp, enabled = canMoveUp) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Move up",
                tint = if (canMoveUp) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
        IconButton(onClick = onMoveDown, enabled = canMoveDown) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Move down",
                tint = if (canMoveDown) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BgmPlaylistEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(Dimens.iconXl)
            )
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            Text(
                text = "No music - add from the music browser or local files",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.nendo.argosy.ui.screens.gamedetail.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.nendo.argosy.data.model.FilePickerRow
import com.nendo.argosy.ui.components.FocusedScroll
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.util.clickableNoFocus
import com.nendo.argosy.util.formatBytes

enum class GroupCheckState { ALL, NONE, PARTIAL }

/**
 * Cherry-pick file selection for a download. Renders no footer of its own;
 * hints ride the global footer bar while this modal holds input.
 */
@Composable
fun FilePickerModal(
    gameTitle: String,
    title: String,
    rows: List<FilePickerRow>,
    selectedIds: Set<Long>,
    selectedVersionIds: Set<Long>,
    focusIndex: Int,
    summary: String,
    onToggleRow: (FilePickerRow) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalArgosyTheme.current
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
            .clickableNoFocus { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(Dimens.radiusPanel),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .widthIn(max = Dimens.modalWidthLg)
                .fillMaxWidth(0.92f)
                .clickableNoFocus { }
        ) {
            Column(modifier = Modifier.padding(Dimens.spacingLg)) {
                Text(
                    text = gameTitle.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.focusAccent
                )
                Spacer(modifier = Modifier.height(Dimens.spacingMd))

                FocusedScroll(listState = listState, focusedIndex = focusIndex)
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    itemsIndexed(rows, key = { _, row -> "${row.groupKey}:${row.rommFileId ?: row.versionRommId ?: "h"}" }) { index, row ->
                        if (row.isHeader) {
                            FilePickerGroupHeader(
                                row = row,
                                checkState = groupCheckState(row.groupKey, rows, selectedIds, selectedVersionIds),
                                isFocused = focusIndex == index,
                                onClick = { onToggleRow(row) }
                            )
                        } else {
                            FilePickerFileRow(
                                row = row,
                                isSelected = row.versionRommId
                                    ?.let { it in selectedVersionIds }
                                    ?: (row.rommFileId in selectedIds),
                                isFocused = focusIndex == index,
                                onClick = { onToggleRow(row) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun groupCheckState(
    groupKey: String,
    rows: List<FilePickerRow>,
    selectedIds: Set<Long>,
    selectedVersionIds: Set<Long>
): GroupCheckState {
    val members = rows.filter { !it.isHeader && it.groupKey == groupKey }
    if (members.isEmpty()) return GroupCheckState.NONE
    val selectedCount = members.count { row ->
        row.versionRommId?.let { it in selectedVersionIds } ?: (row.rommFileId in selectedIds)
    }
    return when (selectedCount) {
        0 -> GroupCheckState.NONE
        members.size -> GroupCheckState.ALL
        else -> GroupCheckState.PARTIAL
    }
}

@Composable
private fun FilePickerGroupHeader(
    row: FilePickerRow,
    checkState: GroupCheckState,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalArgosyTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusControl))
            .background(
                if (isFocused) theme.focusAccent.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .clickableNoFocus { onClick() }
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm)
    ) {
        Icon(
            imageVector = when (checkState) {
                GroupCheckState.ALL -> Icons.Default.Check
                GroupCheckState.PARTIAL -> Icons.Default.IndeterminateCheckBox
                GroupCheckState.NONE -> Icons.Default.CheckBoxOutlineBlank
            },
            contentDescription = null,
            tint = if (checkState == GroupCheckState.NONE)
                MaterialTheme.colorScheme.onSurfaceVariant
            else theme.focusAccent
        )
        Spacer(modifier = Modifier.width(Dimens.spacingSm))
        Text(
            text = row.label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FilePickerFileRow(
    row: FilePickerRow,
    isSelected: Boolean,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalArgosyTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusControl))
            .background(
                if (isFocused) theme.focusAccent.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            )
            .clickableNoFocus(enabled = !row.isLocked) { onClick() }
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = when {
                row.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                isSelected -> theme.focusAccent
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Spacer(modifier = Modifier.width(Dimens.spacingSm))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = row.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (row.isDefaultVersion) {
                    Spacer(modifier = Modifier.width(Dimens.spacingSm))
                    Text(
                        text = "DEFAULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.focusAccent
                    )
                }
            }
            if (row.isDownloaded) {
                Text(
                    text = "On device",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spacingMd))
        Text(
            text = formatBytes(row.sizeBytes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

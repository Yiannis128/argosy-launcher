package com.nendo.argosy.ui.screens.settings.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nendo.argosy.data.emulator.LaunchMethod
import com.nendo.argosy.data.emulator.RomBindingFormat
import com.nendo.argosy.ui.components.Modal
import com.nendo.argosy.ui.screens.settings.LaunchArgsModalState
import com.nendo.argosy.ui.screens.settings.LaunchArgsRow
import com.nendo.argosy.ui.screens.settings.launchArgsModalRows
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.util.clickableNoFocus

@Composable
fun LaunchArgsModal(
    state: LaunchArgsModalState,
    onCycleDataBinding: () -> Unit,
    onCycleExtraBinding: () -> Unit,
    onCycleClipDataBinding: () -> Unit,
    onToggleFlag: (Int) -> Unit,
    onCycleMimeType: () -> Unit,
    onDismiss: () -> Unit
) {
    val rows = launchArgsModalRows(state)

    val hasOverride = state.override?.hasAnyOverride() == true
    Modal(
        title = "Launch Args  -  ${state.platformName} / ${state.emulatorName}",
        baseWidth = Dimens.modalWidthXl,
        onDismiss = onDismiss,
        footerHints = buildList {
            add(com.nendo.argosy.ui.components.InputButton.A to "Cycle")
            add(com.nendo.argosy.ui.components.InputButton.Y to "Reset Field")
            if (hasOverride) {
                add(com.nendo.argosy.ui.components.InputButton.X to "Reset All")
            }
            add(com.nendo.argosy.ui.components.InputButton.B to "Back")
        }
    ) {
        Text(
            text = "Override how Argosy launches this emulator on this platform. Resume-mode " +
                "flags are fixed regardless of overrides.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = Dimens.spacingMd)
        )

        val listState = rememberLazyListState()
        com.nendo.argosy.ui.components.FocusedScroll(listState = listState, focusedIndex = state.focusIndex)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            rows.forEachIndexed { index, row ->
                item(key = "row_$index") {
                    val focused = index == state.focusIndex
                    when (row) {
                        is LaunchArgsRow.DataBinding -> LaunchArgsOptionRow(
                            label = "Data URI",
                            value = bindingLabel(state.override?.dataBinding, state.defaultDataBinding),
                            subtitle = "Sets Intent.data (`-d` in shell). Used by ACTION_VIEW emulators " +
                                "that read the ROM from the intent's data URI.",
                            isOverridden = state.override?.dataBinding != null,
                            isFocused = focused,
                            onClick = onCycleDataBinding
                        )
                        is LaunchArgsRow.ExtraBinding -> LaunchArgsOptionRow(
                            label = "Extras",
                            value = bindingLabel(state.override?.extraBinding, state.defaultExtraBinding),
                            subtitle = "Rewrites every path-typed extra (e.g. bootPath, ROM, AutoStartFile) " +
                                "to this format. `None` removes them.",
                            isOverridden = state.override?.extraBinding != null,
                            isFocused = focused,
                            onClick = onCycleExtraBinding
                        )
                        is LaunchArgsRow.ClipDataBinding -> LaunchArgsOptionRow(
                            label = "ClipData URI",
                            value = bindingLabel(state.override?.clipDataBinding, state.defaultClipDataBinding),
                            subtitle = "Attaches the ROM URI to Intent.clipData. Required for " +
                                "FLAG_GRANT_READ_URI_PERMISSION to actually delegate access to the receiver.",
                            isOverridden = state.override?.clipDataBinding != null,
                            isFocused = focused,
                            onClick = onCycleClipDataBinding
                        )
                        is LaunchArgsRow.LockedBinding -> LaunchArgsOptionRow(
                            label = row.label,
                            value = row.value,
                            subtitle = "Fixed for this emulator -- not a file path.",
                            isOverridden = false,
                            isFocused = focused,
                            onClick = { }
                        )
                        is LaunchArgsRow.Flag -> {
                            val mask = state.override?.intentFlagsMask ?: state.defaultFlagsMask
                            val isOn = (mask and row.bit) != 0
                            val isOverridden = state.override?.intentFlagsMask != null
                            LaunchArgsOptionRow(
                                label = row.label,
                                value = if (isOn) "On" else "Off",
                                subtitle = flagSubtext(row.bit),
                                isOverridden = isOverridden,
                                isFocused = focused,
                                onClick = { onToggleFlag(row.bit) }
                            )
                        }
                        is LaunchArgsRow.MimeType -> LaunchArgsOptionRow(
                            label = "MIME type",
                            value = state.override?.mimeType ?: "Default (${state.defaultMimeType ?: "*/*"})",
                            subtitle = "MIME type sent with the ROM URI. Most emulators ignore this and " +
                                "filter by extension.",
                            isOverridden = state.override?.mimeType != null,
                            isFocused = focused,
                            onClick = onCycleMimeType
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchArgsOptionRow(
    label: String,
    value: String,
    subtitle: String,
    isOverridden: Boolean,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val background = if (isFocused) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val labelColor = if (isFocused) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val valueColor = if (isFocused) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val subtitleColor = if (isFocused) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(background, RoundedCornerShape(Dimens.radiusMd))
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.radiusLg, vertical = Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor
                )
                if (isOverridden) {
                    Text(
                        text = "(custom)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isFocused) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = subtitleColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

private fun methodLabel(override: String?, defaultName: String): String = when (override) {
    null -> "Default (${defaultName.lowercase().replaceFirstChar { it.uppercase() }})"
    LaunchMethod.INTENT.name -> "Intent"
    LaunchMethod.SHELL.name -> "Shell (am start)"
    else -> "Default"
}

private fun bindingLabel(override: String?, defaultLabel: String): String = when (override) {
    null -> "Default ($defaultLabel)"
    RomBindingFormat.NONE.name -> "None"
    RomBindingFormat.ABSOLUTE_PATH.name -> "Absolute path"
    RomBindingFormat.FILE_PROVIDER.name -> "FileProvider URI"
    RomBindingFormat.DOCUMENT_URI.name -> "Document URI (SAF)"
    else -> "Default ($defaultLabel)"
}

private fun flagSubtext(bit: Int): String = when (bit) {
    Intent.FLAG_ACTIVITY_NEW_TASK -> "Launches in a separate Android task. Required by almost every emulator."
    Intent.FLAG_ACTIVITY_CLEAR_TASK -> "Clears any existing instance of the emulator before launching."
    Intent.FLAG_ACTIVITY_NO_HISTORY -> "Hides the emulator from the recent apps list."
    Intent.FLAG_ACTIVITY_SINGLE_TOP -> "Reuses an existing emulator instance if it is at the top of its task."
    Intent.FLAG_GRANT_READ_URI_PERMISSION -> "Delegates read access to the ROM URI. Required when passing content:// URIs."
    Intent.FLAG_ACTIVITY_CLEAR_TOP -> "Clears activities above the target when reusing a task."
    else -> ""
}


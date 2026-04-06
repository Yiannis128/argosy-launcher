package com.nendo.argosy.ui.screens.gamedetail.modals

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nendo.argosy.data.emulator.VariantOption
import com.nendo.argosy.data.model.VariantCategory
import com.nendo.argosy.ui.components.FocusedScroll
import com.nendo.argosy.ui.components.Modal
import com.nendo.argosy.ui.screens.gamedetail.components.OptionItem
import com.nendo.argosy.ui.theme.Dimens

@Composable
fun VariantPickerModal(
    variants: List<VariantOption>,
    focusIndex: Int,
    onSelectVariant: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    Modal(
        title = "SELECT VARIANT",
        subtitle = "Choose which version to launch",
        onDismiss = onDismiss
    ) {
        val listState = rememberLazyListState()
        FocusedScroll(listState = listState, focusedIndex = focusIndex)

        val sorted = variants.sortedBy { VariantCategory.fromKey(it.category).sortOrder }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            sorted.forEachIndexed { index, variant ->
                val category = VariantCategory.fromKey(variant.category)
                val prevCategory = sorted.getOrNull(index - 1)?.let { VariantCategory.fromKey(it.category) }
                if (category != prevCategory) {
                    item(key = "header_${category.key}") {
                        Text(
                            text = category.displayLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(
                                top = Dimens.spacingMd,
                                bottom = Dimens.spacingXs,
                                start = Dimens.spacingSm
                            )
                        )
                    }
                }
                item(key = "variant_${variant.fileId ?: "primary"}") {
                    val label = if (variant.fileId == null) "Default (Original)" else variant.fileName
                    val downloaded = variant.isDownloaded
                    OptionItem(
                        label = label,
                        value = if (!downloaded) "Not downloaded" else if (variant.isMultiDisc) "Multi-disc" else "",
                        isFocused = focusIndex == index,
                        isEnabled = downloaded,
                        onClick = { if (downloaded) onSelectVariant(variant.fileId) }
                    )
                }
            }
        }
    }
}


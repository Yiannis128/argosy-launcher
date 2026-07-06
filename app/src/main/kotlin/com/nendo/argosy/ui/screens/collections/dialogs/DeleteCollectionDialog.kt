package com.nendo.argosy.ui.screens.collections.dialogs

import androidx.compose.runtime.Composable
import com.nendo.argosy.ui.primitives.ArgosyConfirmModalHost

@Composable
fun DeleteCollectionDialog(
    collectionName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ArgosyConfirmModalHost(
        visible = true,
        title = "Delete Collection",
        message = "Are you sure you want to delete \"$collectionName\"? This will remove the collection but not the games in it.",
        confirmLabel = "Delete",
        destructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

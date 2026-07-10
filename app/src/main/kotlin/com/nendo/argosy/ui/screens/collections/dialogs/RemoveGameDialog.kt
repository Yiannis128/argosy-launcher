package com.nendo.argosy.ui.screens.collections.dialogs

import androidx.compose.runtime.Composable
import com.nendo.argosy.ui.primitives.ArgosyConfirmModalHost

@Composable
fun RemoveGameDialog(
    gameTitle: String,
    collectionName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ArgosyConfirmModalHost(
        visible = true,
        title = "Remove Game",
        message = "Remove \"$gameTitle\" from \"$collectionName\"?",
        confirmLabel = "Remove",
        destructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

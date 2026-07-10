package com.nendo.argosy.ui.screens.collections.dialogs

import androidx.compose.runtime.Composable

@Composable
fun EditCollectionDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    CollectionNameDialog(
        title = "Edit Collection",
        label = "Collection name",
        confirmLabel = "Save",
        initialName = currentName,
        gamepadInput = true,
        onDismiss = onDismiss,
        onSubmit = onSave
    )
}

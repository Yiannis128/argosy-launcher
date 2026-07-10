package com.nendo.argosy.ui.screens.collections.dialogs

import androidx.compose.runtime.Composable

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    gamepadInput: Boolean = true
) {
    CollectionNameDialog(
        title = "Create Collection",
        label = "Collection name",
        confirmLabel = "Create",
        initialName = "",
        gamepadInput = gamepadInput,
        onDismiss = onDismiss,
        onSubmit = onCreate
    )
}

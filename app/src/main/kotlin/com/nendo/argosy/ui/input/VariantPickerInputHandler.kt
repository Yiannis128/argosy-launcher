package com.nendo.argosy.ui.input

import com.nendo.argosy.data.emulator.VariantOption

class VariantPickerInputHandler(
    private val getVariants: () -> List<VariantOption>,
    private val getFocusIndex: () -> Int,
    private val onFocusChange: (Int) -> Unit,
    private val onSelect: (Long?) -> Unit,
    private val onDismiss: () -> Unit
) : InputHandler {

    override fun onUp(): InputResult {
        val currentIndex = getFocusIndex()
        if (currentIndex > 0) {
            onFocusChange(currentIndex - 1)
        }
        return InputResult.HANDLED
    }

    override fun onDown(): InputResult {
        val variants = getVariants()
        val currentIndex = getFocusIndex()
        if (currentIndex < variants.size - 1) {
            onFocusChange(currentIndex + 1)
        }
        return InputResult.HANDLED
    }

    override fun onConfirm(): InputResult {
        val variants = getVariants()
        val variant = variants.getOrNull(getFocusIndex())
        if (variant != null) {
            onSelect(variant.fileId)
        }
        return InputResult.HANDLED
    }

    override fun onBack(): InputResult {
        onDismiss()
        return InputResult.HANDLED
    }
}

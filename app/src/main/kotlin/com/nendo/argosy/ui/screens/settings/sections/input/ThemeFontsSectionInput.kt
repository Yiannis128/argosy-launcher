package com.nendo.argosy.ui.screens.settings.sections.input

import com.nendo.argosy.ui.input.InputHandler
import com.nendo.argosy.ui.input.InputResult
import com.nendo.argosy.ui.screens.settings.SettingsViewModel
import com.nendo.argosy.ui.screens.settings.sections.ThemeFontsLayoutState
import com.nendo.argosy.ui.screens.settings.sections.themeFontsMaxFocusIndex
import com.nendo.argosy.ui.screens.settings.sections.themeFontsSections

internal class ThemeFontsSectionInput(
    private val viewModel: SettingsViewModel
) : InputHandler {

    private fun layoutState() = ThemeFontsLayoutState.from(viewModel.uiState.value)

    override fun onUp(): InputResult {
        viewModel.moveFocusWrapped(-1, themeFontsMaxFocusIndex(layoutState()))
        return InputResult.HANDLED
    }

    override fun onDown(): InputResult {
        viewModel.moveFocusWrapped(1, themeFontsMaxFocusIndex(layoutState()))
        return InputResult.HANDLED
    }

    override fun onPrevSection(): InputResult {
        if (viewModel.jumpToPrevSection(themeFontsSections(layoutState()))) {
            return InputResult.HANDLED
        }
        return InputResult.UNHANDLED
    }

    override fun onNextSection(): InputResult {
        if (viewModel.jumpToNextSection(themeFontsSections(layoutState()))) {
            return InputResult.HANDLED
        }
        return InputResult.UNHANDLED
    }
}

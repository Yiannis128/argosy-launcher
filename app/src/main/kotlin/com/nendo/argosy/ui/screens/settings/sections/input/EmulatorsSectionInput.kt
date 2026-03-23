package com.nendo.argosy.ui.screens.settings.sections.input

import android.util.Log
import com.nendo.argosy.ui.input.InputHandler
import com.nendo.argosy.ui.input.InputResult
import com.nendo.argosy.ui.screens.settings.SettingsViewModel
import com.nendo.argosy.ui.screens.settings.sections.EmulatorsItem
import com.nendo.argosy.ui.screens.settings.sections.createEmulatorsLayoutInfo
import com.nendo.argosy.ui.screens.settings.sections.emulatorsItemAtFocusIndex

internal class EmulatorsSectionInput(
    private val viewModel: SettingsViewModel
) : InputHandler {
    override fun onContextMenu(): InputResult {
        val state = viewModel.uiState.value
        val info = createEmulatorsLayoutInfo(
            platforms = state.emulators.platforms,
            canAutoAssign = state.emulators.canAutoAssign,
            builtinLibretroEnabled = state.emulators.builtinLibretroEnabled
        )
        val focused = emulatorsItemAtFocusIndex(state.focusedIndex, info)
        Log.d("EmuSectionInput", "onSecondaryAction: focusedIndex=${state.focusedIndex}, focused=$focused")
        if (focused is EmulatorsItem.PlatformItem) {
            val emulatorId = focused.config.effectiveEmulatorId
            Log.d("EmuSectionInput", "Platform: ${focused.config.platform.name}, emulatorId=$emulatorId, updateVersions=${state.emulators.emulatorUpdateVersions}")
            if (emulatorId == null) return InputResult.UNHANDLED
            if (emulatorId in state.emulators.emulatorUpdateVersions) {
                Log.d("EmuSectionInput", "Triggering update for $emulatorId")
                viewModel.triggerEmulatorUpdate(emulatorId)
                return InputResult.HANDLED
            }
        }
        return InputResult.UNHANDLED
    }
}

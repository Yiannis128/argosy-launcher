package com.nendo.argosy.ui.common.savechannel

import com.nendo.argosy.domain.model.UnifiedSaveEntry
import com.nendo.argosy.ui.screens.gamedetail.components.SaveStatusEvent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveChannelStateHolder @Inject constructor() {
    val state = MutableStateFlow(SaveChannelState())
    var currentGameId: Long = 0
    var rawEntries: List<UnifiedSaveEntry> = emptyList()
    var pendingSaveStatusChanged: ((SaveStatusEvent) -> Unit)? = null
}

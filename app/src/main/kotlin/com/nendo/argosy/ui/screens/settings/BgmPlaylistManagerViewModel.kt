package com.nendo.argosy.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nendo.argosy.ui.audio.BgmPlaylistCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class BgmPlaylistRowUi(
    val id: Long,
    val displayName: String,
    val filePath: String,
    val isMissing: Boolean
)

data class BgmPlaylistManagerState(
    val entries: List<BgmPlaylistRowUi> = emptyList(),
    val focusedIndex: Int = 0,
    val isReordering: Boolean = false
)

@HiltViewModel
class BgmPlaylistManagerViewModel @Inject constructor(
    private val coordinator: BgmPlaylistCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow(BgmPlaylistManagerState())
    val uiState: StateFlow<BgmPlaylistManagerState> = _uiState.asStateFlow()

    private var orderSnapshot: List<BgmPlaylistRowUi>? = null

    init {
        viewModelScope.launch {
            coordinator.entries
                .map { rows ->
                    rows.map {
                        BgmPlaylistRowUi(
                            id = it.id,
                            displayName = it.displayName,
                            filePath = it.filePath,
                            isMissing = !File(it.filePath).exists()
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { rows ->
                    _uiState.update { st ->
                        if (st.isReordering) st
                        else st.copy(
                            entries = rows,
                            focusedIndex = st.focusedIndex.coerceIn(0, (rows.size - 1).coerceAtLeast(0))
                        )
                    }
                }
        }
    }

    fun setFocusIndex(index: Int) {
        _uiState.update { it.copy(focusedIndex = index.coerceIn(0, (it.entries.size - 1).coerceAtLeast(0))) }
    }

    fun moveFocus(delta: Int) {
        _uiState.update { st ->
            if (st.entries.isEmpty()) st
            else st.copy(focusedIndex = (st.focusedIndex + delta).mod(st.entries.size))
        }
    }

    fun beginReorder() {
        val st = _uiState.value
        if (st.entries.isEmpty() || st.isReordering) return
        orderSnapshot = st.entries
        _uiState.update { it.copy(isReordering = true) }
    }

    fun moveFocusedRow(delta: Int) {
        _uiState.update { st ->
            if (!st.isReordering) return@update st
            val target = st.focusedIndex + delta
            if (target < 0 || target >= st.entries.size) return@update st
            val reordered = st.entries.toMutableList().apply { add(target, removeAt(st.focusedIndex)) }
            st.copy(entries = reordered, focusedIndex = target)
        }
    }

    fun commitReorder() {
        val st = _uiState.value
        if (!st.isReordering) return
        orderSnapshot = null
        _uiState.update { it.copy(isReordering = false) }
        viewModelScope.launch { coordinator.reorder(st.entries.map { it.id }) }
    }

    fun cancelReorder() {
        val snapshot = orderSnapshot
        orderSnapshot = null
        _uiState.update { st ->
            st.copy(
                isReordering = false,
                entries = snapshot ?: st.entries,
                focusedIndex = st.focusedIndex.coerceIn(0, ((snapshot ?: st.entries).size - 1).coerceAtLeast(0))
            )
        }
    }

    fun moveRow(index: Int, delta: Int) {
        val st = _uiState.value
        if (st.isReordering) return
        val target = index + delta
        if (index !in st.entries.indices || target < 0 || target >= st.entries.size) return
        val reordered = st.entries.toMutableList().apply { add(target, removeAt(index)) }
        _uiState.update { it.copy(entries = reordered, focusedIndex = target) }
        viewModelScope.launch { coordinator.reorder(reordered.map { it.id }) }
    }

    fun removeAt(index: Int) {
        val st = _uiState.value
        if (st.isReordering) return
        val row = st.entries.getOrNull(index) ?: return
        viewModelScope.launch { coordinator.removeById(row.id) }
    }

    fun removeFocused() = removeAt(_uiState.value.focusedIndex)
}

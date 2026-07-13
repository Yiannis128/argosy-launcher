package com.nendo.argosy.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import com.nendo.argosy.ui.audio.BgmPlaylistCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val NOTICE_DURATION_MS = 2500L

data class BgmFolderSourceUi(
    val id: Long,
    val displayName: String,
    val filePath: String,
    val trackCount: Int,
    val isMissing: Boolean
)

data class BgmPlaylistRowUi(
    val id: Long,
    val displayName: String,
    val filePath: String,
    val isMissing: Boolean,
    val sourceFolderName: String? = null
)

data class BgmPlaylistManagerState(
    val folderSources: List<BgmFolderSourceUi> = emptyList(),
    val entries: List<BgmPlaylistRowUi> = emptyList(),
    val focusedIndex: Int = 0,
    val isReordering: Boolean = false,
    val notice: String? = null
) {
    val focusCount: Int get() = folderSources.size + entries.size
    val isEmpty: Boolean get() = focusCount == 0
    val focusedSource: BgmFolderSourceUi? get() = folderSources.getOrNull(focusedIndex)
    val focusedEntry: BgmPlaylistRowUi? get() =
        if (focusedIndex >= folderSources.size) entries.getOrNull(focusedIndex - folderSources.size) else null
}

@HiltViewModel
class BgmPlaylistManagerViewModel @Inject constructor(
    private val coordinator: BgmPlaylistCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow(BgmPlaylistManagerState())
    val uiState: StateFlow<BgmPlaylistManagerState> = _uiState.asStateFlow()

    private var orderSnapshot: List<BgmPlaylistRowUi>? = null
    private var noticeJob: Job? = null

    init {
        viewModelScope.launch {
            coordinator.entries
                .map { rows -> rows.toGroups() }
                .flowOn(Dispatchers.IO)
                .collect { (sources, tracks) ->
                    _uiState.update { st ->
                        if (st.isReordering) st
                        else st.copy(
                            folderSources = sources,
                            entries = tracks,
                            focusedIndex = st.focusedIndex.coerceIn(
                                0,
                                (sources.size + tracks.size - 1).coerceAtLeast(0)
                            )
                        )
                    }
                }
        }
    }

    private fun List<BgmPlaylistEntity>.toGroups(): Pair<List<BgmFolderSourceUi>, List<BgmPlaylistRowUi>> {
        val folders = filter { it.entryType == BgmPlaylistEntity.TYPE_FOLDER }
        val files = filter { it.entryType != BgmPlaylistEntity.TYPE_FOLDER }
        val folderNameById = folders.associate { it.id to it.displayName }
        val sourcedCounts = files.mapNotNull { it.sourceEntryId }.groupingBy { it }.eachCount()
        val sources = folders.map { folder ->
            BgmFolderSourceUi(
                id = folder.id,
                displayName = folder.displayName,
                filePath = folder.filePath,
                trackCount = sourcedCounts[folder.id] ?: 0,
                isMissing = !File(folder.filePath).isDirectory
            )
        }
        val tracks = files.map { file ->
            BgmPlaylistRowUi(
                id = file.id,
                displayName = file.displayName,
                filePath = file.filePath,
                isMissing = !File(file.filePath).exists(),
                sourceFolderName = file.sourceEntryId?.let { folderNameById[it] }
            )
        }
        return sources to tracks
    }

    fun setFocusIndex(index: Int) {
        _uiState.update { it.copy(focusedIndex = index.coerceIn(0, (it.focusCount - 1).coerceAtLeast(0))) }
    }

    fun moveFocus(delta: Int) {
        _uiState.update { st ->
            if (st.isEmpty) st
            else st.copy(focusedIndex = (st.focusedIndex + delta).mod(st.focusCount))
        }
    }

    fun beginReorder() {
        val st = _uiState.value
        if (st.entries.isEmpty() || st.isReordering || st.focusedEntry == null) return
        orderSnapshot = st.entries
        _uiState.update { it.copy(isReordering = true) }
    }

    fun moveFocusedRow(delta: Int) {
        _uiState.update { st ->
            if (!st.isReordering) return@update st
            val trackIndex = st.focusedIndex - st.folderSources.size
            val target = trackIndex + delta
            if (trackIndex < 0 || target < 0 || target >= st.entries.size) return@update st
            val reordered = st.entries.toMutableList().apply { add(target, removeAt(trackIndex)) }
            st.copy(entries = reordered, focusedIndex = st.folderSources.size + target)
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
            val entries = snapshot ?: st.entries
            st.copy(
                isReordering = false,
                entries = entries,
                focusedIndex = st.focusedIndex.coerceIn(
                    0,
                    (st.folderSources.size + entries.size - 1).coerceAtLeast(0)
                )
            )
        }
    }

    fun moveTrack(trackIndex: Int, delta: Int) {
        val st = _uiState.value
        if (st.isReordering) return
        val target = trackIndex + delta
        if (trackIndex !in st.entries.indices || target < 0 || target >= st.entries.size) return
        val reordered = st.entries.toMutableList().apply { add(target, removeAt(trackIndex)) }
        _uiState.update { it.copy(entries = reordered, focusedIndex = it.folderSources.size + target) }
        viewModelScope.launch { coordinator.reorder(reordered.map { it.id }) }
    }

    fun removeSource(sourceIndex: Int) {
        val st = _uiState.value
        if (st.isReordering) return
        val source = st.folderSources.getOrNull(sourceIndex) ?: return
        viewModelScope.launch { coordinator.removeFolderSource(source.id) }
    }

    fun removeTrack(trackIndex: Int) {
        val st = _uiState.value
        if (st.isReordering) return
        val row = st.entries.getOrNull(trackIndex) ?: return
        if (row.sourceFolderName != null) {
            postNotice("Managed by synced folder - remove the file or the folder source")
            return
        }
        viewModelScope.launch { coordinator.removeById(row.id) }
    }

    fun removeFocused() {
        val st = _uiState.value
        if (st.isReordering) return
        if (st.focusedIndex < st.folderSources.size) removeSource(st.focusedIndex)
        else removeTrack(st.focusedIndex - st.folderSources.size)
    }

    private fun postNotice(message: String) {
        noticeJob?.cancel()
        _uiState.update { it.copy(notice = message) }
        noticeJob = viewModelScope.launch {
            delay(NOTICE_DURATION_MS)
            _uiState.update { it.copy(notice = null) }
        }
    }
}

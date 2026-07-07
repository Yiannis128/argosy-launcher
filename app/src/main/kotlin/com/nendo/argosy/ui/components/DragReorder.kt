package com.nendo.argosy.ui.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Hold-drag-drop reorder state for a LazyColumn whose items map 1:1 to a flat keyed list. */
@Stable
class DragReorderState internal constructor(
    private val listState: LazyListState,
    private val scope: CoroutineScope,
    private val canDrag: (Any) -> Boolean,
    private val onLift: (Any) -> Unit,
    private val onMove: (Any, Int) -> Unit,
    private val onDrop: () -> Unit
) {
    var draggingKey by mutableStateOf<Any?>(null)
        private set
    var dragDelta by mutableFloatStateOf(0f)
        private set

    private var autoscrollJob: Job? = null
    private var autoscrollSpeed = 0f

    internal fun startDrag(position: Offset) {
        val item = listState.layoutInfo.visibleItemsInfo.firstOrNull {
            position.y >= it.offset && position.y < it.offset + it.size
        } ?: return
        if (!canDrag(item.key)) return
        draggingKey = item.key
        dragDelta = 0f
        onLift(item.key)
    }

    internal fun drag(deltaY: Float) {
        if (draggingKey == null) return
        dragDelta += deltaY
        resolveMove()
        updateAutoscroll()
    }

    internal fun endDrag() {
        autoscrollJob?.cancel()
        autoscrollJob = null
        if (draggingKey == null) return
        draggingKey = null
        dragDelta = 0f
        onDrop()
    }

    private fun draggedItem() = draggingKey?.let { key ->
        listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }
    }

    private fun resolveMove() {
        val key = draggingKey ?: return
        val item = draggedItem() ?: return
        val draggedCenter = item.offset + dragDelta + item.size / 2f
        val target = listState.layoutInfo.visibleItemsInfo.firstOrNull {
            it.key != key && canDrag(it.key) &&
                draggedCenter >= it.offset && draggedCenter < it.offset + it.size
        } ?: return
        dragDelta += item.offset - target.offset
        onMove(key, target.index)
    }

    private fun updateAutoscroll() {
        val item = draggedItem() ?: return
        val info = listState.layoutInfo
        val edge = item.size * 0.75f
        val top = item.offset + dragDelta
        val bottom = top + item.size
        autoscrollSpeed = when {
            top < info.viewportStartOffset + edge -> -(info.viewportStartOffset + edge - top) / edge * MAX_SCROLL_STEP
            bottom > info.viewportEndOffset - edge -> (bottom - (info.viewportEndOffset - edge)) / edge * MAX_SCROLL_STEP
            else -> 0f
        }
        if (autoscrollSpeed == 0f) {
            autoscrollJob?.cancel()
            autoscrollJob = null
            return
        }
        if (autoscrollJob?.isActive == true) return
        autoscrollJob = scope.launch {
            while (isActive && draggingKey != null && autoscrollSpeed != 0f) {
                val consumed = listState.scrollBy(autoscrollSpeed)
                if (consumed == 0f) break
                dragDelta += consumed
                resolveMove()
                delay(FRAME_MILLIS)
            }
        }
    }

    private companion object {
        const val MAX_SCROLL_STEP = 12f
        const val FRAME_MILLIS = 16L
    }
}

@Composable
fun rememberDragReorderState(
    listState: LazyListState,
    canDrag: (Any) -> Boolean,
    onLift: (Any) -> Unit,
    onMove: (Any, Int) -> Unit,
    onDrop: () -> Unit
): DragReorderState {
    val scope = rememberCoroutineScope()
    val canDragCurrent = rememberUpdatedState(canDrag)
    val onLiftCurrent = rememberUpdatedState(onLift)
    val onMoveCurrent = rememberUpdatedState(onMove)
    val onDropCurrent = rememberUpdatedState(onDrop)
    return remember(listState, scope) {
        DragReorderState(
            listState = listState,
            scope = scope,
            canDrag = { canDragCurrent.value(it) },
            onLift = { onLiftCurrent.value(it) },
            onMove = { key, index -> onMoveCurrent.value(key, index) },
            onDrop = { onDropCurrent.value() }
        )
    }
}

fun Modifier.dragReorderContainer(state: DragReorderState): Modifier =
    pointerInput(state) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset -> state.startDrag(offset) },
            onDrag = { change, amount ->
                if (state.draggingKey != null) {
                    change.consume()
                    state.drag(amount.y)
                }
            },
            onDragEnd = { state.endDrag() },
            onDragCancel = { state.endDrag() }
        )
    }

fun Modifier.dragReorderItem(state: DragReorderState, key: Any): Modifier =
    if (state.draggingKey == key) {
        this
            .zIndex(1f)
            .graphicsLayer { translationY = state.dragDelta }
    } else {
        this
    }

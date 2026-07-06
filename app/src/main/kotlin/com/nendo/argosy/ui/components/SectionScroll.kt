package com.nendo.argosy.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.abs

data class ListSection(
    val name: String? = null,
    val listStartIndex: Int,
    val listEndIndex: Int,
    val focusStartIndex: Int,
    val focusEndIndex: Int
)

@Composable
fun FocusedScroll(
    listState: LazyListState,
    focusedIndex: Int
) {
    LaunchedEffect(focusedIndex) {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo

        if (visibleItems.isEmpty()) {
            listState.scrollToItem(focusedIndex)
            return@LaunchedEffect
        }
        if (!listState.canScrollForward && !listState.canScrollBackward) {
            return@LaunchedEffect
        }

        val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val targetItem = visibleItems.find { it.index == focusedIndex }
        val itemHeight = targetItem?.size ?: visibleItems.maxOfOrNull { it.size } ?: 80
        val lastListIndex = layoutInfo.totalItemsCount - 1

        if (focusedIndex >= lastListIndex) {
            val bottomAlignOffset = if (targetItem != null) itemHeight - viewportHeight else 0
            listState.animateScrollToItem(lastListIndex, bottomAlignOffset)
            return@LaunchedEffect
        }

        val centerOffset = (viewportHeight - itemHeight) / 2
        listState.animateScrollToItem(focusedIndex, -centerOffset)
    }
}

@Composable
fun SectionFocusedScroll(
    listState: LazyListState,
    focusedIndex: Int,
    focusToListIndex: (Int) -> Int,
    sections: List<ListSection>
) {
    var previousFocusIndex by remember { mutableIntStateOf(focusedIndex) }

    LaunchedEffect(focusedIndex) {
        val jumped = abs(focusedIndex - previousFocusIndex) > 1
        previousFocusIndex = focusedIndex

        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo
        if (visibleItems.isNotEmpty() && !listState.canScrollForward && !listState.canScrollBackward) {
            return@LaunchedEffect
        }

        val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val listIndex = focusToListIndex(focusedIndex)
        val targetItem = visibleItems.find { it.index == listIndex }
        val itemHeight = targetItem?.size ?: visibleItems.maxOfOrNull { it.size } ?: 80
        val lastListIndex = layoutInfo.totalItemsCount - 1

        suspend fun scroll(index: Int, offset: Int) {
            if (jumped) {
                listState.scrollToItem(index, offset)
            } else {
                listState.animateScrollToItem(index, offset)
            }
        }

        val firstFocusable = sections.firstOrNull()?.focusStartIndex
        if (firstFocusable != null && focusedIndex <= firstFocusable) {
            scroll(0, 0)
            if (listState.layoutInfo.visibleItemsInfo.none { it.index == listIndex }) {
                scroll(listIndex, itemHeight - viewportHeight)
            }
            return@LaunchedEffect
        }

        val lastFocusable = sections.lastOrNull()?.focusEndIndex
        if (lastFocusable != null && focusedIndex >= lastFocusable && lastListIndex >= 0) {
            val lastItem = visibleItems.find { it.index == lastListIndex }
            val bottomAlignOffset = if (lastItem != null) lastItem.size - viewportHeight else 0
            scroll(lastListIndex, bottomAlignOffset)
            if (listState.layoutInfo.visibleItemsInfo.none { it.index == listIndex }) {
                scroll(listIndex, 0)
            }
            return@LaunchedEffect
        }

        val centerOffset = (viewportHeight - itemHeight) / 2
        scroll(listIndex, -centerOffset)
    }
}

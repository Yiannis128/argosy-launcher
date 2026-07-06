package com.nendo.argosy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

enum class FooterVariant { STANDARD, SUBTLE }

data class FooterEntry(
    val hints: List<FooterHintItem>,
    val style: FooterStyleConfig,
    val variant: FooterVariant,
    val onHintClick: ((InputButton) -> Unit)?,
    val trailingContent: (@Composable () -> Unit)?
)

/** Singleton hint stack backing the app-root guide bar; the most recently registered active surface wins. */
class FooterHostController {
    private var nextId = 0L
    private val stack = mutableStateListOf<Pair<Long, FooterEntry>>()

    internal fun allocateId(): Long = nextId++

    internal fun set(id: Long, entry: FooterEntry) {
        val index = stack.indexOfFirst { it.first == id }
        if (index >= 0) stack[index] = id to entry else stack.add(id to entry)
    }

    internal fun remove(id: Long) {
        stack.removeAll { it.first == id }
    }

    val top: FooterEntry? get() = stack.lastOrNull()?.second
}

val LocalFooterHost = staticCompositionLocalOf { FooterHostController() }

/** Registers gamepad hints on the app-root guide bar for the lifetime of the calling composable. */
@Composable
fun FooterHints(
    hints: List<Pair<InputButton, String>>,
    variant: FooterVariant = FooterVariant.STANDARD,
    onHintClick: ((InputButton) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    FooterHintsWithState(
        hints = remember(hints) { hints.map { FooterHintItem(it.first, it.second) } },
        variant = variant,
        onHintClick = onHintClick,
        trailingContent = trailingContent
    )
}

/** FooterHints variant carrying per-hint enabled state. */
@Composable
fun FooterHintsWithState(
    hints: List<FooterHintItem>,
    variant: FooterVariant = FooterVariant.STANDARD,
    onHintClick: ((InputButton) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val controller = LocalFooterHost.current
    val style = LocalFooterStyle.current
    val id = remember(controller) { controller.allocateId() }
    val entry = FooterEntry(hints, style, variant, onHintClick, trailingContent)
    SideEffect { controller.set(id, entry) }
    DisposableEffect(controller, id) {
        onDispose { controller.remove(id) }
    }
}

/** Renders the top of the hint stack; composed exactly once at the app root. */
@Composable
fun FooterHost(
    controller: FooterHostController,
    modifier: Modifier = Modifier
) {
    val entry = controller.top
    CompositionLocalProvider(LocalFooterStyle provides (entry?.style ?: FooterStyleConfig())) {
        when (entry?.variant) {
            FooterVariant.SUBTLE -> SubtleFooterBar(
                hints = entry.hints.map { it.button to it.action },
                modifier = modifier,
                onHintClick = entry.onHintClick
            )
            else -> FooterBarWithState(
                hints = entry?.hints ?: emptyList(),
                modifier = modifier,
                onHintClick = entry?.onHintClick,
                trailingContent = entry?.trailingContent
            )
        }
    }
}

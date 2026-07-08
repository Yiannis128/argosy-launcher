package com.nendo.argosy.ui.theme.backdrop

import com.nendo.argosy.data.preferences.BackdropEdgeStyle
import com.nendo.argosy.data.preferences.BackdropMotion
import com.nendo.argosy.data.preferences.BackdropPreset
import com.nendo.argosy.data.preferences.BackdropVertexIcon
import com.nendo.argosy.ui.theme.generated.ComponentDefaults

/** Full user-facing surface-backdrop configuration; the tile is seed-deterministic, motion only translates it. */
data class BackdropConfig(
    val enabled: Boolean = false,
    val preset: BackdropPreset = BackdropPreset.PLATFORMS,
    val cellSize: Int = ComponentDefaults.SurfaceBackdrop.cellSizeDefaultDp,
    val scatter: Int = 200,
    val scaleJitter: Int = 100,
    val strength: Int = 20,
    val edgeStyle: BackdropEdgeStyle = BackdropEdgeStyle.CONNECTIONS,
    val vertexIcons: BackdropVertexIcon = BackdropVertexIcon.NONE,
    val seed: Long = 0L,
    val motion: BackdropMotion = BackdropMotion.SWAY,
    val motionSpeed: Int = 75,
    val driftAngle: Float = 45f
)

enum class BackdropCellShape { NONE, DOT, HEX_OUTLINE, PLATFORM_STAMPS }

val BackdropPreset.cellShape: BackdropCellShape
    get() = when (this) {
        BackdropPreset.DOTS -> BackdropCellShape.DOT
        BackdropPreset.HEX -> BackdropCellShape.HEX_OUTLINE
        BackdropPreset.PLATFORMS -> BackdropCellShape.PLATFORM_STAMPS
        BackdropPreset.SCANLINES, BackdropPreset.ICON_GRID -> BackdropCellShape.NONE
    }

val BackdropPreset.horizontalEdgesOnly: Boolean
    get() = this == BackdropPreset.SCANLINES

val BackdropPreset.defaultEdgeStyle: BackdropEdgeStyle
    get() = when (this) {
        BackdropPreset.SCANLINES -> BackdropEdgeStyle.SOLID
        BackdropPreset.ICON_GRID -> BackdropEdgeStyle.FADED
        BackdropPreset.DOTS, BackdropPreset.HEX, BackdropPreset.PLATFORMS -> BackdropEdgeStyle.NONE
    }

val BackdropPreset.defaultVertexIcons: BackdropVertexIcon
    get() = when (this) {
        BackdropPreset.ICON_GRID -> BackdropVertexIcon.PLUS
        else -> BackdropVertexIcon.NONE
    }

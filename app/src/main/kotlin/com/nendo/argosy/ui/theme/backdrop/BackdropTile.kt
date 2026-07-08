package com.nendo.argosy.ui.theme.backdrop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import com.nendo.argosy.data.preferences.BackdropEdgeStyle
import com.nendo.argosy.data.preferences.BackdropVertexIcon
import com.nendo.argosy.ui.theme.generated.ComponentDefaults
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private const val TILE_CELLS = 4
private const val EDGE_STROKE_RATIO = 0.04f
private const val HEX_STROKE_RATIO = 0.05f
private const val VERTEX_STROKE_RATIO = 0.08f
private const val DOT_RADIUS_RATIO = 0.11f
private const val HEX_RADIUS_RATIO = 0.34f
private const val VERTEX_DOT_RADIUS_RATIO = 0.07f
private const val VERTEX_PLUS_ARM_RATIO = 0.13f
private const val VERTEX_DIAMOND_RATIO = 0.11f
private const val DASH_ON_RATIO = 0.3f
private const val DASH_OFF_RATIO = 0.2f
private const val TWO_PI = 2f * PI.toFloat()
private const val PERCENT = 100f
private const val VERTEX_LAYER_SALT = 0x56455254L
private const val MIN_SHAPE_SCALE = 0.1f

private fun ringExtent(config: BackdropConfig): Int {
    val shapeReach = if (config.preset.cellShape == BackdropCellShape.PLATFORM_STAMPS) {
        ComponentDefaults.SurfaceBackdrop.stampCellRatio
    } else {
        HEX_RADIUS_RATIO + HEX_STROKE_RATIO
    }
    val reach = (config.scatter / 100f) * ComponentDefaults.SurfaceBackdrop.scatterMaxCellRatio + shapeReach
    return kotlin.math.ceil(reach.toDouble()).toInt().coerceAtLeast(1)
}
private const val CELL_LAYER_SALT = 0x43454C4CL
private const val STAMP_PICK_SALT = 0x5354414DL
private const val EDGE_CONN_SALT = 0x434F4E4EL
private const val CONNECTION_CHANCE = 0.4f
private const val CONNECTION_PAD_RATIO = 0.08f
private const val MIN_CONNECTION_RATIO = 0.15f

private data class ConnectionAnchor(val position: Offset, val clearance: Float)
private const val MIX_GAMMA = -0x61c8864680b583ebL
private const val MIX_MUL_1 = -0x40a7b892e31b1a47L
private const val MIX_MUL_2 = -0x6b2fb644ecceee15L

/** Renders one seamless grid-lattice tile (edges, vertices, cells) deterministically from the stored seed. */
internal fun renderBackdropTile(
    config: BackdropConfig,
    tint: Color,
    alpha: Float,
    cellSizePx: Float,
    stamps: List<ImageBitmap> = emptyList()
): ImageBitmap {
    val sizePx = (cellSizePx * TILE_CELLS).roundToInt().coerceAtLeast(TILE_CELLS)
    val cell = sizePx.toFloat() / TILE_CELLS
    val color = tint.copy(alpha = alpha.coerceIn(0f, 1f))
    val bitmap = ImageBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    drawEdgeLayer(canvas, config, color, cell)
    drawVertexLayer(canvas, config, color, cell)
    drawCellLayer(canvas, config, color, cell, stamps)

    return bitmap
}

private fun strokePaint(color: Color, width: Float): Paint = Paint().apply {
    this.color = color
    style = PaintingStyle.Stroke
    strokeWidth = width
    isAntiAlias = true
}

private fun fillPaint(color: Color): Paint = Paint().apply {
    this.color = color
    style = PaintingStyle.Fill
    isAntiAlias = true
}

private fun drawEdgeLayer(canvas: Canvas, config: BackdropConfig, color: Color, cell: Float) {
    if (config.edgeStyle == BackdropEdgeStyle.NONE) return
    val stroke = max(1f, cell * EDGE_STROKE_RATIO)
    val verticalToo = !config.preset.horizontalEdgesOnly
    when (config.edgeStyle) {
        BackdropEdgeStyle.SOLID, BackdropEdgeStyle.DASHED -> {
            val paint = strokePaint(color, stroke)
            if (config.edgeStyle == BackdropEdgeStyle.DASHED) {
                paint.pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(cell * DASH_ON_RATIO, cell * DASH_OFF_RATIO)
                )
            }
            val span = cell * TILE_CELLS
            for (line in 0..TILE_CELLS) {
                val pos = line * cell
                canvas.drawLine(Offset(0f, pos), Offset(span, pos), paint)
                if (verticalToo) canvas.drawLine(Offset(pos, 0f), Offset(pos, span), paint)
            }
        }
        BackdropEdgeStyle.FADED -> {
            val mid = color.copy(alpha = 0f)
            for (line in 0..TILE_CELLS) {
                for (seg in 0 until TILE_CELLS) {
                    val from = Offset(seg * cell, line * cell)
                    val to = Offset((seg + 1) * cell, line * cell)
                    canvas.drawLine(from, to, strokePaint(color.copy(alpha = 1f), stroke).apply {
                        shader = LinearGradientShader(from, to, listOf(color, mid, color))
                    })
                    if (verticalToo) {
                        val fromV = Offset(line * cell, seg * cell)
                        val toV = Offset(line * cell, (seg + 1) * cell)
                        canvas.drawLine(fromV, toV, strokePaint(color.copy(alpha = 1f), stroke).apply {
                            shader = LinearGradientShader(fromV, toV, listOf(color, mid, color))
                        })
                    }
                }
            }
        }
        BackdropEdgeStyle.CONNECTIONS -> drawConnections(canvas, config, color, cell, stroke)
        BackdropEdgeStyle.NONE -> {}
    }
}

private fun connectionAnchor(config: BackdropConfig, cell: Float, i: Int, j: Int): ConnectionAnchor {
    val pad = cell * CONNECTION_PAD_RATIO
    return if (config.preset.cellShape != BackdropCellShape.NONE) {
        val params = shapeParams(config, cell, i, j, CELL_LAYER_SALT)
        val radiusRatio = when (config.preset.cellShape) {
            BackdropCellShape.DOT -> DOT_RADIUS_RATIO
            BackdropCellShape.HEX_OUTLINE -> HEX_RADIUS_RATIO
            BackdropCellShape.PLATFORM_STAMPS -> ComponentDefaults.SurfaceBackdrop.stampCellRatio / 2f
            BackdropCellShape.NONE -> 0f
        }
        ConnectionAnchor(
            Offset((i + 0.5f) * cell + params.dx, (j + 0.5f) * cell + params.dy),
            cell * radiusRatio * params.scale + pad,
        )
    } else {
        val params = shapeParams(config, cell, i, j, VERTEX_LAYER_SALT)
        val radiusRatio = if (config.vertexIcons == BackdropVertexIcon.NONE) 0f else VERTEX_PLUS_ARM_RATIO
        ConnectionAnchor(
            Offset(i * cell + params.dx, j * cell + params.dy),
            cell * radiusRatio * params.scale + pad,
        )
    }
}

private fun drawConnections(canvas: Canvas, config: BackdropConfig, color: Color, cell: Float, stroke: Float) {
    val mid = color.copy(alpha = 0f)
    val ring = ringExtent(config) + 1
    for (j in -ring..TILE_CELLS + ring - 1) {
        for (i in -ring..TILE_CELLS + ring - 1) {
            val links = latticeRandom(config.seed, i, j, EDGE_CONN_SALT)
            val toRight = links.nextFloat() < CONNECTION_CHANCE
            val toDown = links.nextFloat() < CONNECTION_CHANCE
            if (!toRight && !toDown) continue
            val from = connectionAnchor(config, cell, i, j)
            val minLength = cell * MIN_CONNECTION_RATIO
            if (toRight) drawConnectionLine(canvas, from, connectionAnchor(config, cell, i + 1, j), color, mid, stroke, minLength)
            if (toDown) drawConnectionLine(canvas, from, connectionAnchor(config, cell, i, j + 1), color, mid, stroke, minLength)
        }
    }
}

private fun drawConnectionLine(
    canvas: Canvas,
    from: ConnectionAnchor,
    to: ConnectionAnchor,
    color: Color,
    mid: Color,
    stroke: Float,
    minLength: Float,
) {
    val delta = to.position - from.position
    val length = delta.getDistance()
    if (length <= from.clearance + to.clearance + minLength) return
    val dir = delta / length
    val start = from.position + dir * from.clearance
    val end = to.position - dir * to.clearance
    canvas.drawLine(start, end, strokePaint(color.copy(alpha = 1f), stroke).apply {
        shader = LinearGradientShader(start, end, listOf(color, mid, color))
    })
}

private class ShapeParams(val dx: Float, val dy: Float, val scale: Float, val rotation: Float)

private fun mix64(value: Long): Long {
    var z = value + MIX_GAMMA
    z = (z xor (z ushr 30)) * MIX_MUL_1
    z = (z xor (z ushr 27)) * MIX_MUL_2
    return z xor (z ushr 31)
}

private fun latticeRandom(seed: Long, i: Int, j: Int, salt: Long): Random =
    Random(mix64(mix64(mix64(seed xor salt) + i.mod(TILE_CELLS)) + j.mod(TILE_CELLS)))

private fun shapeParams(config: BackdropConfig, cell: Float, i: Int, j: Int, salt: Long): ShapeParams {
    val random = latticeRandom(config.seed, i, j, salt)
    val angle = random.nextFloat() * TWO_PI
    val magnitude = random.nextFloat() * (config.scatter / PERCENT) *
        ComponentDefaults.SurfaceBackdrop.scatterMaxCellRatio * cell
    val jitter = config.scaleJitter / PERCENT
    val scale = (1f - random.nextFloat() * jitter * ComponentDefaults.SurfaceBackdrop.jitterMaxScaleDropRatio)
        .coerceAtLeast(MIN_SHAPE_SCALE)
    val rotation = (random.nextFloat() - 0.5f) * jitter * ComponentDefaults.SurfaceBackdrop.jitterMaxRotationDegrees
    return ShapeParams(cos(angle) * magnitude, sin(angle) * magnitude, scale, rotation)
}

private fun drawVertexLayer(canvas: Canvas, config: BackdropConfig, color: Color, cell: Float) {
    if (config.vertexIcons == BackdropVertexIcon.NONE) return
    val stroke = max(1f, cell * VERTEX_STROKE_RATIO)
    val ring = ringExtent(config)
    for (j in -ring + 1..TILE_CELLS + ring - 1) {
        for (i in -ring + 1..TILE_CELLS + ring - 1) {
            val params = shapeParams(config, cell, i, j, VERTEX_LAYER_SALT)
            canvas.save()
            canvas.translate(i * cell + params.dx, j * cell + params.dy)
            canvas.rotate(params.rotation)
            when (config.vertexIcons) {
                BackdropVertexIcon.DOTS ->
                    canvas.drawCircle(Offset.Zero, cell * VERTEX_DOT_RADIUS_RATIO * params.scale, fillPaint(color))
                BackdropVertexIcon.PLUS -> {
                    val arm = cell * VERTEX_PLUS_ARM_RATIO * params.scale
                    val paint = strokePaint(color, stroke)
                    canvas.drawLine(Offset(-arm, 0f), Offset(arm, 0f), paint)
                    canvas.drawLine(Offset(0f, -arm), Offset(0f, arm), paint)
                }
                BackdropVertexIcon.DIAMOND -> {
                    val half = cell * VERTEX_DIAMOND_RATIO * params.scale
                    val path = Path().apply {
                        moveTo(0f, -half)
                        lineTo(half, 0f)
                        lineTo(0f, half)
                        lineTo(-half, 0f)
                        close()
                    }
                    canvas.drawPath(path, fillPaint(color))
                }
                BackdropVertexIcon.NONE -> {}
            }
            canvas.restore()
        }
    }
}

private fun drawCellLayer(canvas: Canvas, config: BackdropConfig, color: Color, cell: Float, stamps: List<ImageBitmap>) {
    val shape = config.preset.cellShape
    if (shape == BackdropCellShape.NONE) return
    if (shape == BackdropCellShape.PLATFORM_STAMPS && stamps.isEmpty()) return
    val ring = ringExtent(config)
    val stampPaint = Paint().apply {
        isAntiAlias = true
        filterQuality = FilterQuality.Medium
    }
    for (j in -ring..TILE_CELLS + ring - 1) {
        for (i in -ring..TILE_CELLS + ring - 1) {
            val params = shapeParams(config, cell, i, j, CELL_LAYER_SALT)
            canvas.save()
            canvas.translate((i + 0.5f) * cell + params.dx, (j + 0.5f) * cell + params.dy)
            canvas.rotate(params.rotation)
            when (shape) {
                BackdropCellShape.DOT ->
                    canvas.drawCircle(Offset.Zero, cell * DOT_RADIUS_RATIO * params.scale, fillPaint(color))
                BackdropCellShape.HEX_OUTLINE ->
                    canvas.drawPath(
                        hexPath(cell * HEX_RADIUS_RATIO * params.scale),
                        strokePaint(color, max(1f, cell * HEX_STROKE_RATIO))
                    )
                BackdropCellShape.PLATFORM_STAMPS -> {
                    val pick = latticeRandom(config.seed, i, j, STAMP_PICK_SALT).nextInt(stamps.size)
                    val stamp = stamps[pick]
                    canvas.scale(params.scale, params.scale)
                    canvas.drawImage(stamp, Offset(-stamp.width / 2f, -stamp.height / 2f), stampPaint)
                }
                BackdropCellShape.NONE -> {}
            }
            canvas.restore()
        }
    }
}

private fun hexPath(radius: Float): Path = Path().apply {
    for (k in 0 until 6) {
        val angle = Math.toRadians(60.0 * k - 30.0)
        val x = radius * cos(angle).toFloat()
        val y = radius * sin(angle).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

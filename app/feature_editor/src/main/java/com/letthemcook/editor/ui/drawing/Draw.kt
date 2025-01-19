package com.letthemcook.editor.ui.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.letthemcook.editor.domain.editor.components.containment.ComponentContainment
import com.letthemcook.editor.domain.editor.components.containment.Relation

const val DRAW_PADDING = 12f
const val MIN_LINE_LENGTH = 48f
const val HORIZONTAL_COMPONENT_PADDING = 64f
const val SMOOTHEN_VALUE = 4f
const val ROUNDED_RECT_CORNER_RADIUS = 32f

fun DrawScope.drawCenterHelper(
    squareSize: Float,
    gridColor: Color,
    strokeWidth: Float
) {
    drawLine(
        color = gridColor,
        start = Offset(size.width / 2 - squareSize / 2, size.height / 2),
        end = Offset(size.width / 2 + squareSize / 2, size.height / 2),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = gridColor,
        start = Offset(size.width / 2, size.height / 2 - squareSize / 2),
        end = Offset(size.width / 2, size.height / 2 + squareSize / 2),
        strokeWidth = strokeWidth
    )
}

fun DrawScope.drawGrid(
    step: Float,
    canvasZoom: Float,
    canvasOffset: Offset,
    backgroundColor: Color,
    gridColor: Color
) {
    drawRect(
        color = backgroundColor,
        topLeft = Offset.Zero,
        size = size
    )

    val offsetX = canvasOffset.x * canvasZoom % step
    val offsetY = canvasOffset.y * canvasZoom % step

    var currentX = size.width / 2
    var currentY = size.height / 2

    while (currentX <= size.width + step) {
        drawLine(
            color = gridColor,
            start = Offset(currentX + offsetX, 0f),
            end = Offset(currentX + offsetX, size.height),
            strokeWidth = 1f
        )

        currentX += step
    }

    currentX = size.width / 2 - step

    while (currentX >= -step) {
        drawLine(
            color = gridColor,
            start = Offset(currentX + offsetX, 0f),
            end = Offset(currentX + offsetX, size.height),
            strokeWidth = 1f
        )

        currentX -= step
    }

    while (currentY <= size.height + step) {
        drawLine(
            color = gridColor,
            start = Offset(0f, currentY + offsetY),
            end = Offset(size.width, currentY + offsetY),
            strokeWidth = 1f
        )

        currentY += step
    }

    currentY = size.height / 2 - step

    while (currentY >= -step) {
        drawLine(
            color = gridColor,
            start = Offset(0f, currentY + offsetY),
            end = Offset(size.width, currentY + offsetY),
            strokeWidth = 1f
        )

        currentY -= step
    }
}

fun DrawScope.drawRoundRectQuarter(
    position: Offset,
    size: Size,
    relation: Relation,
    color: Color
) {
    val brush = getRoundedRectQuarterBrush(position, size, relation, color)

    val center = position.plus(Offset(size.width / 2, size.height / 2))
    val topLeft = position.plus(Offset(0f, MIN_LINE_LENGTH))
    val topRight = position.plus(Offset(size.width , MIN_LINE_LENGTH))
    val bottomLeft = position.plus(Offset(0f, size.height - MIN_LINE_LENGTH))
    val bottomRight = position.plus(Offset(size.width, size.height - MIN_LINE_LENGTH))

    when (relation) {
        is ComponentContainment.Left -> {
            drawPath(
                path = Path().apply {
                    moveTo(topLeft.x, topLeft.y)
                    lineTo(center.x, center.y)
                    lineTo(bottomLeft.x, bottomLeft.y)
                    lineTo(topLeft.x, topLeft.y)
                },
                brush = brush
            )
        }
        is ComponentContainment.Top -> {
            drawPath(
                path = Path().apply {
                    moveTo(topLeft.x, topLeft.y)
                    lineTo(center.x, center.y)
                    lineTo(topRight.x, topRight.y)
                    lineTo(topLeft.x, topLeft.y)
                },
                brush = brush
            )
        }
        is ComponentContainment.Right -> {
            drawPath(
                path = Path().apply {
                    moveTo(topRight.x, topRight.y)
                    lineTo(center.x, center.y)
                    lineTo(bottomRight.x, bottomRight.y)
                    lineTo(topRight.x, topRight.y)
                },
                brush = brush
            )
        }
        is ComponentContainment.Bottom -> {
            drawPath(
                path = Path().apply {
                    moveTo(bottomLeft.x, bottomLeft.y)
                    lineTo(center.x, center.y)
                    lineTo(bottomRight.x, bottomRight.y)
                    lineTo(bottomLeft.x, bottomLeft.y)
                },
                brush = brush
            )
        }
    }
}

private fun getRoundedRectQuarterBrush(
    position: Offset,
    size: Size,
    relation: Relation,
    color: Color
): Brush {
    val paddingPercent = 0.025f
    val paddingPercentOther = 1f - paddingPercent

    return when (relation) {
        is ComponentContainment.Left -> Brush.horizontalGradient(
            colors = listOf(color, Color.Transparent),
            startX = position.x + size.width / 2,
            endX = position.x + size.width * paddingPercent
        )
        is ComponentContainment.Top -> Brush.verticalGradient(
            colors = listOf(color, Color.Transparent),
            startY = position.y + size.height / 2,
            endY = position.y + (size.height - 2 * MIN_LINE_LENGTH) * paddingPercent + MIN_LINE_LENGTH
        )
        is ComponentContainment.Right -> Brush.horizontalGradient(
            colors = listOf(color, Color.Transparent),
            startX = position.x + size.width / 2,
            endX = position.x + size.width * paddingPercentOther
        )
        is ComponentContainment.Bottom -> Brush.verticalGradient(
            colors = listOf(color, Color.Transparent),
            startY = position.y + size.height / 2,
            endY = position.y + (size.height - 2 * MIN_LINE_LENGTH) * paddingPercentOther + MIN_LINE_LENGTH
        )
        else -> SolidColor(color)
    }
}
package com.letthemcook.editor.ui.drawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextDecoration

fun DrawScope.drawProductLabel(
    textLayout: TextLayoutResult,
    topLeft: Offset,
    padding: Float,
    color: Color,
    textColor: Color
) {
    val labelSize = Size(
        textLayout.size.width + padding * 4,
        textLayout.size.height + padding * 2
    )
    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = labelSize,
        cornerRadius = CornerRadius(32f, 32f),
        style = Fill
    )
    drawText(
        textLayoutResult = textLayout,
        color = textColor,
        topLeft = topLeft.plus(Offset(padding * 2, padding))
    )
}
package com.letthemcook.editor.ui.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

fun getBlockBodyBrush(
    containerColor: Color,
    otherColor: Color,
    position: Offset? = null,
    size: Size? = null
): Brush {
    return if (position == null || size == null) {
        Brush.linearGradient(
            listOf(
                containerColor,
                containerColor.compositeOver(otherColor),
                otherColor
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                containerColor,
                containerColor.compositeOver(otherColor),
                otherColor
            ),
            start = position,
            end = position + Offset(size.width, size.height)
        )
    }
}
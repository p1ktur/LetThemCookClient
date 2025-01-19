package com.letthemcook.editor.domain.editor.geometry

import androidx.compose.ui.geometry.*
import com.letthemcook.editor.ui.drawing.SMOOTHEN_VALUE

fun Offset.smoothen(about: Float = SMOOTHEN_VALUE): Offset {
    return copy(
        x = x - x % about,
        y = y - y % about
    )
}

//fun Size.smoothen(about: Float = SMOOTHEN_VALUE): Size {
//    return copy(
//        width = width - width % about,
//        height = height - height % about
//    )
//}

fun Float.smoothen(about: Float = SMOOTHEN_VALUE): Float {
    return this - this % about
}
package com.letthemcook.editor.domain.editor.geometry

import androidx.compose.ui.geometry.*

fun Offset.zoom(center: Offset, zoom: Float): Offset {
    val x = (this.x - center.x) / zoom + center.x
    val y = (this.y - center.y) / zoom + center.y

    return Offset(x, y)
}

fun Offset.unZoom(center: Offset, zoom: Float): Offset {
    val x = (this.x - center.x) * zoom + center.x
    val y = (this.y - center.y) * zoom + center.y

    return Offset(x, y)
}
package com.letthemcook.editor.domain.viewModels.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.editor.domain.editor.geometry.unZoom
import com.letthemcook.editor.domain.editor.geometry.zoom

data class CanvasUiState(
    // Config
    val size: Size = Size.Zero,
    val center: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val offset: Offset = Offset.Zero,
    val cachedOffset: Offset = Offset.Zero,
    // Pointer data
    val pointerDownOffset: Offset? = null,
    val pointerMoveOffset: Offset? = null,
    val pointerMoveDeltaOffset: Offset? = null
) {
    fun scaleAndTranslate(position: Offset): Offset {
        return position.zoom(center, zoom) - offset
    }

    fun undoScaleAndTranslate(position: Offset): Offset {
        return (position + offset).unZoom(center, zoom)
    }
}
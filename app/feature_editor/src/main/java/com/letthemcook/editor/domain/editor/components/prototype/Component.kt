package com.letthemcook.editor.domain.editor.components.prototype

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent

interface Component {
    var prevComponent: Component
    var nextComponent: Component
    var parentComponent: ComposedComponent?

    var position: Offset
    val size: Size

    var containedPointerPosition: Offset?
}
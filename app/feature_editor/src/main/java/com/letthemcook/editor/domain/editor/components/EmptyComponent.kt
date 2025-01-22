package com.letthemcook.editor.domain.editor.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component

object EmptyComponent : Component {
    override var parentComponent: ComposedComponent? = null
    override var position: Offset = Offset.Zero
    override val size: Size = Size.Zero
    override var containedPointerPosition: Offset? = null

    // Graphics

    override fun shadeQuarterForNextFrame(relation: Relation) = Unit
    override fun highlightForNextFrame() = Unit

    // Other

    override fun toString(): String {
        return "Empty"
    }

    override fun hashCode(): Int {
        return 0
    }
}
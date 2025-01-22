package com.letthemcook.editor.domain.viewModels.builder

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.block.unused.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.geometry.zoom
import com.letthemcook.editor.ui.components.popups.BlockEditorState

data class BuilderUiState(
    // Builder screen
    val blockEditorState: BlockEditorState = BlockEditorState.Hidden,
    // Products
    val unusedProducts: List<ProductItemData> = listOf(
        ProductItemData(0, "Tomato"),
        ProductItemData(1, "Potato"),
        ProductItemData(2, "Carrot")
    ),
    val movedProducts: List<ProductItemData> = emptyList(),
    // Components
    val unusedBlockComponents: List<UnusedBlockComponent> = listOf(
        UnusedBlockComponent(name = "Comp 1"),
        UnusedBlockComponent(name = "Comp 2"),
        UnusedBlockComponent(name = "Comp 3"),
        UnusedBlockComponent(name = "I am here! 1"),
        UnusedBlockComponent(name = "I am here! 2"),
        UnusedBlockComponent(name = "I am here! 3"),
        UnusedBlockComponent(name = "I am here! 4"),
        UnusedBlockComponent(name = "I am here! 5"),
        UnusedBlockComponent(name = "I am here! 6"),
        UnusedBlockComponent(name = "I am here! 7"),
        UnusedBlockComponent(name = "I am here! 8"),
    ),
    val blockComponents: List<BlockComponent> = emptyList(),
    val componentFocus: ComponentFocus = ComponentFocus.NONE,
    val startComponent: StartComponent = StartComponent(),
    val centralComponent: Component = EmptyComponent,
    val endComponent: EndComponent = EndComponent(),
    // Canvas data
    val canvasUiState: CanvasUiState = CanvasUiState(),
    val canvasCounter: Int = 0,
    // Dragging
    val draggingState: DraggingState = DraggingState.NONE
) {
    enum class ComponentFocus {
        NONE,
        BLOCK
    }

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
    )

    fun scaleAndTranslate(position: Offset): Offset {
        return position.zoom(canvasUiState.center, canvasUiState.zoom) - canvasUiState.offset
    }
}

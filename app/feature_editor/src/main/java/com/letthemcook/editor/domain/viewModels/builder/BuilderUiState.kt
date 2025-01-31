package com.letthemcook.editor.domain.viewModels.builder

import com.letthemcook.core.domain.model.items.ProductItemData
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.color.ColorOption
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.ComponentFocus
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState
import com.letthemcook.editor.ui.components.popups.BlockEditorState

data class BuilderUiState(
    // Builder screen
    val blockEditorState: BlockEditorState = BlockEditorState.Hidden,
    val savedBlockEditorState: BlockEditorState? = null,
    val viewedMediaFile: File? = null,
    // Products
    val unusedProducts: List<ProductItemData> = listOf(
        ProductItemData(0, "Tomato"),
        ProductItemData(1, "Potato"),
        ProductItemData(2, "Carrot")
    ),
    // Components
    val unusedBlockComponents: List<UnusedBlockComponent> = listOf(
        UnusedBlockComponent(name = "Comp 1", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "Comp 2", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "Comp 3", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 1", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 2", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 3", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 4", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 5", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 6", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 7", colorOption = ColorOption.entries.random()),
        UnusedBlockComponent(name = "I am here! 8", colorOption = ColorOption.entries.random()),
    ),
    val componentFocus: ComponentFocus = ComponentFocus.None,
    val startComponent: StartComponent = StartComponent(),
    val centralComponent: Component = EmptyComponent,
    val endComponent: EndComponent = EndComponent(),
    // Canvas data
    val canvasUiState: CanvasUiState = CanvasUiState(),
    val canvasCounter: Int = 0,
    // Dragging
    val draggingState: DraggingState = DraggingState.NONE
)

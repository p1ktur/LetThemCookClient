package com.letthemcook.editor.domain.viewModels.builder

import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.ComponentFocus
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState
import com.letthemcook.editor.ui.components.popups.BlockEditorState

data class BuilderUiState(
    // Data
    val ownerId: String,
    val recipeId: String,
    val recipeName: String,
    // Builder screen
    val blockEditorState: BlockEditorState = BlockEditorState.Hidden,
    // Products
    val unusedProducts: List<WeightedProduct> = listOf(),
    // Components
    val unusedBlockComponents: List<UnusedBlockComponent> = listOf(),
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

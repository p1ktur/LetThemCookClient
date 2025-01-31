package com.letthemcook.editor.domain.viewModels.builder

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.core.domain.model.items.ProductItemData
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.ui.components.popups.BlockEditorState

sealed interface BuilderUiAction {
    data object NavigateBack : BuilderUiAction
    data object NavigateToTutorial : BuilderUiAction

    data class ViewMediaFile(val file: File) : BuilderUiAction
    data object StopViewingMediaFile : BuilderUiAction

    // Common
    data object FetchData : BuilderUiAction
    data object SaveChanges : BuilderUiAction
    data object TryDemoCooking : BuilderUiAction

    // Products
    data class AddProduct(val product: ProductItemData, val position: Offset) : BuilderUiAction

    // Components
    data class AddUnusedComponent(val unusedBlockComponent: UnusedBlockComponent) : BuilderUiAction
    data class UpdateUnusedComponent(val oldComponent: UnusedBlockComponent, val newComponent: UnusedBlockComponent) : BuilderUiAction

    data class AddComponent(val unusedBlockComponent: UnusedBlockComponent, val blockComponent: BlockComponent, val position: Offset) : BuilderUiAction
    data class UpdateComponent(val oldComponent: BlockComponent, val newComponent: UnusedBlockComponent) : BuilderUiAction
    data object RemoveComponent : BuilderUiAction

    // Canvas actions
    data class UpdateCanvasSize(val size: Size) : BuilderUiAction

    // Pointer actions
    data class PointerDown(val offset: Offset) : BuilderUiAction
    data class PointerMove(val offset: Offset, val deltaOffset: Offset) : BuilderUiAction
    data object PointerRelease : BuilderUiAction
    data class PointerZoom(val zoom: Float) : BuilderUiAction

    // Dragging
    data class SetDraggingState(val state: DraggingState) : BuilderUiAction

    // Block Editor
    data class SetBlockEditorState(val state: BlockEditorState) : BuilderUiAction
    data class SaveBlockEditorState(val state: BlockEditorState?) : BuilderUiAction
}
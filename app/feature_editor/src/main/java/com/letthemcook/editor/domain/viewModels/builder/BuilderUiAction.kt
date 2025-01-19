package com.letthemcook.editor.domain.viewModels.builder

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.unused.UnusedBlockComponent

sealed interface BuilderUiAction {
    data object NavigateBack : BuilderUiAction
    data object NavigateToTutorial : BuilderUiAction

    // Common
    data object FetchData : BuilderUiAction
    data object SaveChanges : BuilderUiAction

    // Products
    data class AddProduct(val product: ProductItemData, val position: Offset) : BuilderUiAction

    // Components
    data class AddUnusedComponent(val unusedBlockComponent: UnusedBlockComponent) : BuilderUiAction
    data class AddComponent(val unusedBlockComponent: UnusedBlockComponent, val position: Offset, val size: Size) : BuilderUiAction
    data class UpdateComponentData(val updater: BlockComponent.() -> Unit) : BuilderUiAction
    data class RemoveComponent(val index: Int) : BuilderUiAction

    // Canvas actions
    data class UpdateCanvasSize(val size: Size) : BuilderUiAction

    // Pointer actions
    data class PointerDown(val offset: Offset) : BuilderUiAction
    data class PointerMove(
        val offset: Offset,
        val deltaOffset: Offset,
        val draggingState: DraggingState = DraggingState.NONE
    ) : BuilderUiAction
    data object PointerRelease : BuilderUiAction
    data class PointerZoom(val zoom: Float) : BuilderUiAction
}
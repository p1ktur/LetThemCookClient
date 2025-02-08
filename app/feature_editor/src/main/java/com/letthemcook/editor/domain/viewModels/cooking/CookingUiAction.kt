package com.letthemcook.editor.domain.viewModels.cooking

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.editor.domain.editor.components.block.BlockComponent

sealed interface CookingUiAction {
    data object NavigateBack : CookingUiAction

    data class ViewMediaFile(val blockId: String, val file: File) : CookingUiAction

    // Cooking
    data object StartCooking : CookingUiAction
    data object PauseCooking : CookingUiAction
    data object StopCooking : CookingUiAction

    data class FinishBlock(val ref: BlockComponent) : CookingUiAction
    data class SelectBlock(val ref: BlockComponent) : CookingUiAction
    data class RestoreBlock(val ref: BlockComponent) : CookingUiAction

    // Blocks
    data object DeselectBlock : CookingUiAction

    // Canvas actions
    data class UpdateCanvasSize(val size: Size) : CookingUiAction

    // Pointer actions
    data class PointerDown(val offset: Offset) : CookingUiAction
    data class PointerMove(val offset: Offset, val deltaOffset: Offset) : CookingUiAction
    data object PointerRelease : CookingUiAction
    data class PointerZoom(val zoom: Float) : CookingUiAction
}
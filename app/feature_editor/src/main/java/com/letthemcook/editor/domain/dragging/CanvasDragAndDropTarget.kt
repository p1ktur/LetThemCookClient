package com.letthemcook.editor.domain.dragging

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.editor.components.UnusedBlockComponent
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction

class CanvasDragAndDropTarget(
    private val canvasGlobalPosition: Offset,
    private val unusedProducts: List<ProductItemData>,
    private val unusedBlockComponents: List<UnusedBlockComponent>,
    private val textMeasurer: TextMeasurer,
    private val blockComponentNameTextStyle: TextStyle,
    private val blockComponentContentTextStyle: TextStyle,
    private val onUiAction: (BuilderUiAction) -> Unit
) : DragAndDropTarget {

    private var currentPointerPosition: Offset = Offset.Zero

    var draggingState: DraggingState = DraggingState.NONE

    override fun onEnded(event: DragAndDropEvent) {
        draggingState = DraggingState.NONE
    }

    override fun onDrop(event: DragAndDropEvent): Boolean {
        val position = Offset(
            event.toAndroidDragEvent().x,
            event.toAndroidDragEvent().y
        ).minus(
            canvasGlobalPosition
        )

        val clipData = event.toAndroidDragEvent().clipData

        when (clipData.description.label) {
            "Product" -> {
                val productId = clipData.getItemAt(0).text.toString().toIntOrNull()

                productId?.let {
                    unusedProducts.find { it.id == productId }?.let {
                        onUiAction(BuilderUiAction.AddProduct(it, position))
                    }
                }
            }
            "Block" -> {
                val blockHashcode = clipData.getItemAt(0).text.toString().toIntOrNull()

                blockHashcode?.let {
                    unusedBlockComponents.find { it.hashCode() == blockHashcode }?.let {
                        val size = it.calculateSize(textMeasurer, blockComponentNameTextStyle, blockComponentContentTextStyle)
                        onUiAction(BuilderUiAction.AddComponent(it, position, size))
                    }
                }
            }
        }

        return true
    }

    override fun onMoved(event: DragAndDropEvent) {
        val position = Offset(
            event.toAndroidDragEvent().x,
            event.toAndroidDragEvent().y
        ).minus(
            canvasGlobalPosition
        )
        val deltaPosition = position - currentPointerPosition

        currentPointerPosition = position

        onUiAction(BuilderUiAction.PointerMove(currentPointerPosition, deltaPosition, draggingState))
    }
}
package com.letthemcook.editor.domain.dragging

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.items.ProductItemData
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction

class CanvasDragAndDropManager(
    private val canvasGlobalPosition: Offset,
    private val unusedProducts: List<ProductItemData>,
    private val unusedBlockComponents: List<UnusedBlockComponent>,
    private val textMeasurer: TextMeasurer,
    private val nameTextStyle: TextStyle,
    private val contentTextStyle: TextStyle,
    private val onUiAction: (BuilderUiAction) -> Any?
) : DragAndDropTarget {

    private var currentPointerPosition: Offset = Offset.Zero

    override fun onEnded(event: DragAndDropEvent) {
        onUiAction(BuilderUiAction.SetDraggingState(DraggingState.NONE))
        super.onEnded(event)
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
                        val productBlock = onUiAction(BuilderUiAction.AddProduct(it, position)) as? BlockComponent

                        productBlock?.calculateSize(textMeasurer, nameTextStyle, contentTextStyle)
                    }
                }
            }
            "Block" -> {
                val blockHashcode = clipData.getItemAt(0).text.toString().toIntOrNull()

                blockHashcode?.let {
                    unusedBlockComponents.find { it.hashCode() == blockHashcode }?.let {
                        val component = it.toBlockComponent(textMeasurer, nameTextStyle, contentTextStyle)
                        onUiAction(BuilderUiAction.AddComponent(it, component, position))
                    }
                }

            }
        }

        onUiAction(BuilderUiAction.SetDraggingState(DraggingState.NONE))
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

        onUiAction(BuilderUiAction.PointerMove(currentPointerPosition, deltaPosition))
        super.onMoved(event)
    }
}
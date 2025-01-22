package com.letthemcook.editor.domain.editor.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.ROUNDED_RECT_CORNER_RADIUS

data class StartComponent(
    override var size: Size = Size.Zero,
    override var position: Offset = Offset.Zero
) : Component {

    override var parentComponent: ComposedComponent? = null

    override var containedPointerPosition: Offset? = null

    // Graphics

    fun drawOn(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        textColor: Color,
        containerColor: Color,
        frameColor: Color,
        textStyle: TextStyle,
        positionXIsCentral: Boolean = false
    ) {
        val sizeWasZero = size == Size.Zero

        val textLayout = textMeasurer.measure(
            text = "Start", // TODO Localize
            style = textStyle.copy(textAlign = TextAlign.Center)
        )

        if (sizeWasZero) {
            size = Size(
                width = textLayout.size.width + DRAW_PADDING * 8,
                height = textLayout.size.height + DRAW_PADDING * 4 + COMPONENT_PADDING
            )
        } else if (positionXIsCentral) {
            position -= Offset(size.width / 2, 0f)
        }

        val afterLinePosition = position.copy(y = position.y)

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = position.x + size.width / 2, y = position.y + size.height - COMPONENT_PADDING),
            end = position.copy(x = position.x + size.width / 2, y = position.y + size.height),
            strokeWidth = 4f
        )

        drawScope.drawRoundRect(
            color = containerColor,
            topLeft = afterLinePosition,
            size = size.copy(height = size.height - COMPONENT_PADDING),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS,ROUNDED_RECT_CORNER_RADIUS),
            style = Fill
        )
        drawScope.drawRoundRect(
            color = textColor,
            topLeft = afterLinePosition,
            size = size.copy(height = size.height - COMPONENT_PADDING),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS,ROUNDED_RECT_CORNER_RADIUS),
            style = Stroke(4f)
        )
        drawScope.drawText(
            textLayoutResult = textLayout,
            color = textColor,
            topLeft = Offset(
                x = afterLinePosition.x + DRAW_PADDING * 4,
                y = afterLinePosition.y + DRAW_PADDING * 2
            )
        )
    }

    fun calculateSize(
        textMeasurer: TextMeasurer,
        textStyle: TextStyle
    ): Size {
        val textLayout = textMeasurer.measure(
            text = "Start", // TODO Localize
            style = textStyle.copy(textAlign = TextAlign.Center)
        )
        return Size(
            width = textLayout.size.width + DRAW_PADDING * 8,
            height = textLayout.size.height + DRAW_PADDING * 4 + COMPONENT_PADDING
        )
    }

    override fun shadeQuarterForNextFrame(relation: Relation) = Unit
    override fun highlightForNextFrame() = Unit

    // Other

    override fun toString(): String {
        return "StartComponent"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StartComponent

        if (size != other.size) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}

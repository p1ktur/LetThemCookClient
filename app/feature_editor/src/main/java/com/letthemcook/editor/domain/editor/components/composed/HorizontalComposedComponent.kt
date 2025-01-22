package com.letthemcook.editor.domain.editor.components.composed

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.componentHashCodes
import com.letthemcook.editor.domain.editor.components.prototype.drawOn
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import kotlin.math.max

data class HorizontalComposedComponent(
    override val components: MutableList<Component>,
    override var parentComponent: ComposedComponent? = null
) : ComposedComponent {

    override var containedPointerPosition: Offset? = null

    override var position: Offset = Offset.Zero
    override val size: Size get() = calculateSize()

    private var cachedComponentsHashcode: Int = 0
    private var cachedSize: Size = Size.Zero

    private var shadingQuarterForNextFrame: Relation? = null

    // Graphics

    override fun drawOn(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle,
        frameColor: Color,
        containerColor: Color,
        textColor: Color,
        highlightColor: Color,
        positionXIsCentral: Boolean
    ) {
        if (positionXIsCentral) {
            position -= Offset(size.width / 2, 0f)
        }

//        drawScope.drawRect(
//            color = Color.Red.copy(alpha = 0.15f),
//            topLeft = position,
//            size = size
//        )

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = position.x + size.width / 2),
            end = position.copy(x = position.x + size.width / 2, y = position.y + COMPONENT_PADDING),
            strokeWidth = 4f
        )

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = position.x + size.width / 2, y = position.y + size.height),
            end = position.copy(x = position.x + size.width / 2, y = position.y + size.height - COMPONENT_PADDING),
            strokeWidth = 4f
        )

        var cursorPosition = Offset(
            x = position.x + COMPONENT_PADDING,
            y = position.y
        )

        components.forEach { component ->
            component.centerChild(cursorPosition, size.height)

            val lineX = cursorPosition.x + component.size.width / 2

            drawScope.drawLine(
                color = frameColor,
                start = cursorPosition.copy(x = lineX, y = cursorPosition.y + COMPONENT_PADDING),
                end = component.position.copy(x = lineX),
                strokeWidth = 4f
            )
            drawScope.drawLine(
                color = frameColor,
                start = component.position.copy(x = lineX, y = component.position.y + component.size.height),
                end = cursorPosition.copy(x = lineX, y = position.y + size.height - COMPONENT_PADDING),
                strokeWidth = 4f
            )

            component.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor
            )

            cursorPosition += Offset(component.size.width + COMPONENT_PADDING, 0f)
        }

        val commonHorizontalLineStartX = components.first().position.x + components.first().size.width / 2
        val commonHorizontalLineEndX = components.last().position.x + components.last().size.width / 2

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = commonHorizontalLineStartX, y = position.y + COMPONENT_PADDING),
            end = position.copy(x = commonHorizontalLineEndX, y = position.y + COMPONENT_PADDING),
            strokeWidth = 4f
        )
        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = commonHorizontalLineStartX, y = position.y + size.height - COMPONENT_PADDING),
            end = position.copy(x = commonHorizontalLineEndX, y = position.y + size.height - COMPONENT_PADDING),
            strokeWidth = 4f
        )

        shadingQuarterForNextFrame?.let { containment ->
            drawScope.drawRoundRectQuarter(
                position = position,
                size = size,
                relation = containment,
                color = frameColor.copy(alpha = 0.5f)
            )

            shadingQuarterForNextFrame = null
        }
    }

    override fun shadeQuarterForNextFrame(relation: Relation) {
        shadingQuarterForNextFrame = relation
    }

    override fun highlightForNextFrame() {
        components.forEach { it.highlightForNextFrame() }
    }

    private fun calculateSize(): Size {
        return if (cachedComponentsHashcode == components.componentHashCodes()) {
            cachedSize
        } else {
            // (components.size - 1 + 2)
            val width = components.map { it.size.width }.reduce { acc, width -> acc + width } + (components.size + 1) * COMPONENT_PADDING
            val height = getComponentsMaxHeight()
            val size = Size(width, height + 2 * COMPONENT_PADDING)

            cachedComponentsHashcode = components.componentHashCodes()
            cachedSize = size

            size
        }
    }

    private fun getComponentsMaxHeight(): Float {
        var maxHeight = 0f

        components.forEach { component ->
            maxHeight = max(maxHeight, component.size.height)
        }

        return maxHeight
    }

    private fun Component.centerChild(cursorPosition: Offset, maxHeight: Float) {
        position = Offset(
            x = cursorPosition.x,
            y = cursorPosition.y + (maxHeight - this.size.height) / 2
        )
    }

    // Other

    override fun toString(): String {
        return "HorizontalComposedComponent$components"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HorizontalComposedComponent

        if (components.componentHashCodes() != other.components.componentHashCodes()) return false
        if (position != other.position) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = components.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}

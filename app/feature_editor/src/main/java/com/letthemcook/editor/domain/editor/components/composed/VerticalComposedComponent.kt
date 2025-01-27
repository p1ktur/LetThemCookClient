package com.letthemcook.editor.domain.editor.components.composed

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.serialization.OffsetSerializer
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.componentHashCodes
import com.letthemcook.editor.domain.editor.components.prototype.drawOn
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.max

@Serializable
@SerialName(value = "vertical_composed")
data class VerticalComposedComponent(
    override val components: MutableList<Component>,
    @Transient override var parentComponent: ComposedComponent? = null
) : ComposedComponent {

    @Transient
    override var containedPointerPosition: Offset? = null

    @Serializable(with = OffsetSerializer::class)
    override var position: Offset = Offset.Zero
    override val size: Size get() = calculateSize()

    @Transient
    private var cachedComponentsHashcode: Int = 0
    @Transient
    private var cachedSize: Size = Size.Zero
    @Transient
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
        warningHighlightColor: Color,
        goodHighlightColor: Color,
        positionXIsCentral: Boolean,
        canvasUiState: CanvasUiState
    ) {
        if (positionXIsCentral) {
            position -= Offset(size.width / 2, 0f)
        }

//        drawScope.drawRect(
//            color = Color.Blue.copy(alpha = 0.15f),
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
            x = position.x,
            y = position.y + COMPONENT_PADDING
        )

        components.forEach { component ->
            component.centerChild(cursorPosition, size.width)

            component.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                warningHighlightColor = warningHighlightColor,
                goodHighlightColor = goodHighlightColor,
                canvasUiState = canvasUiState
            )

            cursorPosition += Offset(0f, component.size.height)
        }

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
            val width = getComponentsMaxWidth() + 2 * COMPONENT_PADDING
            val height = components.map { it.size.height }.reduce { acc, height -> acc + height }
            val size = Size(width, height + 2 * COMPONENT_PADDING)

            cachedComponentsHashcode = components.componentHashCodes()
            cachedSize = size

            size
        }
    }

    override fun tryRecalculateSize() {
        components.forEach { it.tryRecalculateSize() }

        calculateSize()
    }

    private fun getComponentsMaxWidth(): Float {
        var maxWidth = 0f

        components.forEach { component ->
            maxWidth = max(maxWidth, component.size.width)
        }

        return maxWidth
    }

    private fun Component.centerChild(cursorPosition: Offset, maxWidth: Float) {
        position = Offset(
            x = cursorPosition.x + (maxWidth - this.size.width) / 2,
            y = cursorPosition.y
        )
    }

    // Other

    override fun toString(): String {
        return "VerticalComposedComponent$components"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VerticalComposedComponent

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

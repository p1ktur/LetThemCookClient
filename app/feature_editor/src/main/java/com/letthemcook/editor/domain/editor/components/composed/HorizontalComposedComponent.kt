package com.letthemcook.editor.domain.editor.components.composed

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.containment.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.componentHashCodes
import com.letthemcook.editor.ui.drawing.HORIZONTAL_COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import kotlinx.serialization.Transient
import kotlin.math.max

data class HorizontalComposedComponent(
    override val components: MutableList<Component>,
    override var prevComponent: Component,
    override var nextComponent: Component,
    override var parentComponent: ComposedComponent? = null
) : ComposedComponent {

    override var containedPointerPosition: Offset? = null

    override var position: Offset = Offset.Zero
    override val size: Size get() = calculateSize()

    private var cachedComponentsHashcode: Int = 0
    private var cachedSize: Size = Size.Zero

    private var shadingQuarterForNextFrame: Relation? = null

    // Graphics
    // TODO optimize

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
//            color = Color.Red.copy(alpha = 0.25f),
//            topLeft = position,
//            size = size
//        )

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = position.x + size.width / 2),
            end = position.copy(x = position.x + size.width / 2, y = position.y + MIN_LINE_LENGTH),
            strokeWidth = 4f
        )

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = position.x + size.width / 2, y = position.y + size.height),
            end = position.copy(x = position.x + size.width / 2, y = position.y + size.height - MIN_LINE_LENGTH),
            strokeWidth = 4f
        )

        var cursorPosition: Offset = position.copy(x = position.x + HORIZONTAL_COMPONENT_PADDING)

        //TODO pass custom MIN_LINE_LENGTH so that vertically blocks can be stretched

        components.forEach { component ->
            component.centerChild(cursorPosition, size.height)

            val lineX = cursorPosition.x + component.size.width / 2

            drawScope.drawLine(
                color = frameColor,
                start = cursorPosition.copy(x = lineX, y = cursorPosition.y + MIN_LINE_LENGTH),
                end = component.position.copy(x = lineX),
                strokeWidth = 4f
            )
            drawScope.drawLine(
                color = frameColor,
                start = component.position.copy(x = lineX, y = component.position.y + component.size.height),
                end = cursorPosition.copy(x = lineX, y = position.y + size.height - MIN_LINE_LENGTH),
                strokeWidth = 4f
            )

            when (component) {
                is BlockComponent -> {
//                    var currentComponent = component
//                    var localCursorPosition = component.position
//
//                    while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
//                        currentComponent.position = localCursorPosition
//
//                        when (currentComponent) {
//                            is BlockComponent -> currentComponent.drawOn(
//                                drawScope = drawScope,
//                                textMeasurer = textMeasurer,
//                                nameTextStyle = nameTextStyle,
//                                contentTextStyle = contentTextStyle,
//                                frameColor = frameColor,
//                                containerColor = containerColor,
//                                textColor = textColor,
//                                highlightColor = highlightColor
//                            )
//                            is HorizontalComposedComponent -> currentComponent.drawOn(
//                                drawScope = drawScope,
//                                textMeasurer = textMeasurer,
//                                nameTextStyle = nameTextStyle,
//                                contentTextStyle = contentTextStyle,
//                                frameColor = frameColor,
//                                containerColor = containerColor,
//                                textColor = textColor,
//                                highlightColor = highlightColor
//                            )
//                            is VerticalComposedComponent -> currentComponent.drawOn(
//                                drawScope = drawScope,
//                                textMeasurer = textMeasurer,
//                                nameTextStyle = nameTextStyle,
//                                contentTextStyle = contentTextStyle,
//                                frameColor = frameColor,
//                                containerColor = containerColor,
//                                textColor = textColor,
//                                highlightColor = highlightColor
//                            )
//                        }
//
//                        localCursorPosition += Offset(0f, currentComponent.size.height)
//                        currentComponent = currentComponent.nextComponent
//                    }
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
                }
                is HorizontalComposedComponent -> {
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
                }
                is VerticalComposedComponent -> {
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
                }
            }

            cursorPosition += Offset(component.size.width + HORIZONTAL_COMPONENT_PADDING, 0f)
        }

        val commonHorizontalLineStartX = components.first().position.x + components.first().size.width / 2
        val commonHorizontalLineEndX = components.last().position.x + components.last().size.width / 2

        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = commonHorizontalLineStartX, y = position.y + MIN_LINE_LENGTH),
            end = position.copy(x = commonHorizontalLineEndX, y = position.y + MIN_LINE_LENGTH),
            strokeWidth = 4f
        )
        drawScope.drawLine(
            color = frameColor,
            start = position.copy(x = commonHorizontalLineStartX, y = position.y + size.height - MIN_LINE_LENGTH),
            end = position.copy(x = commonHorizontalLineEndX, y = position.y + size.height - MIN_LINE_LENGTH),
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
            val width = components.map { it.size.width }.reduce { acc, width -> acc + width } + (components.size + 1) * HORIZONTAL_COMPONENT_PADDING
            val height = getComponentsMaxHeight()
            val size = Size(width, height + 2 * MIN_LINE_LENGTH)

            cachedComponentsHashcode = components.componentHashCodes()
            cachedSize = size

            size
        }
    }

    private fun getComponentsMaxHeight(): Float {
        var maxHeight = 0f

        components.forEachIndexed { index, component ->
            when (component) {
                is BlockComponent -> {
                    var currentComponent = component
                    var accumulatedHeight = currentComponent.size.height

                    while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
                        currentComponent = currentComponent.nextComponent
                        accumulatedHeight += currentComponent.size.height
                    }

                    maxHeight = max(maxHeight, accumulatedHeight)
                }
                is HorizontalComposedComponent -> maxHeight = max(maxHeight, component.size.height)
                is VerticalComposedComponent -> maxHeight = max(maxHeight, component.size.height)
            }
        }

        return maxHeight
    }

    private fun Component.centerChild(cursorPosition: Offset, maxHeight: Float) {
        var childHeight = 0f
        var currentComponent = this

        while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
            childHeight += currentComponent.size.height
            currentComponent = currentComponent.nextComponent
        }

        position = Offset(
            x = cursorPosition.x,
            y = cursorPosition.y + (maxHeight - childHeight) / 2
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
        if (nextComponent.hashCode() != other.nextComponent.hashCode()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = components.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + nextComponent.hashCode()
        return result
    }
}

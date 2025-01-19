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
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import kotlinx.serialization.Transient
import kotlin.math.max

data class VerticalComposedComponent(
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

    @Transient
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

        drawScope.drawRect(
            color = Color.Blue.copy(alpha = 0.25f),
            topLeft = position,
            size = size
        )

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

        var cursorPosition: Offset = position.copy(y = position.y + MIN_LINE_LENGTH)

        //TODO pass custom MIN_LINE_LENGTH so that vertically blocks can be stretched

        components.forEach { component ->
            component.centerChild(cursorPosition, size.width)

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

    private fun calculateSize(): Size {
        return if (cachedComponentsHashcode == components.componentHashCodes()) {
            cachedSize
        } else {
            val width = getComponentsMaxWidth()
            val height = components.map { it.size.height }.reduce { acc, height -> acc + height }
            val size = Size(width, height + 2 * MIN_LINE_LENGTH)

            cachedComponentsHashcode = components.componentHashCodes()
            cachedSize = size

            size
        }
    }

    private fun getComponentsMaxWidth(): Float {
        var maxWidth = 0f

        components.forEachIndexed { index, component ->
            when (component) {
                is BlockComponent -> {
                    var currentComponent = component
                    var accumulatedWidth = currentComponent.size.width

                    while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
                        currentComponent = currentComponent.nextComponent
                        accumulatedWidth += currentComponent.size.width
                    }

                    maxWidth = max(maxWidth, accumulatedWidth)
                }
                is VerticalComposedComponent -> maxWidth = max(maxWidth, component.size.width)
                is HorizontalComposedComponent -> maxWidth = max(maxWidth, component.size.width)
            }
        }

        return maxWidth
    }

    private fun Component.centerChild(cursorPosition: Offset, maxWidth: Float) {
        var childWidth = 0f
        var currentComponent = this

        while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
            childWidth += currentComponent.size.width
            currentComponent = currentComponent.nextComponent
        }

        position = Offset(
            x = cursorPosition.x + (maxWidth - childWidth) / 2,
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

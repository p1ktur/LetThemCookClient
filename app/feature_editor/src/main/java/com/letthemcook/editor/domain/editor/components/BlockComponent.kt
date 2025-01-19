package com.letthemcook.editor.domain.editor.components

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.core.domain.serialization.OffsetSerializer
import com.letthemcook.core.domain.serialization.SizeSerializer
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.geometry.checkForContainment
import com.letthemcook.editor.domain.editor.components.containment.ComponentContainment
import com.letthemcook.editor.domain.editor.components.containment.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH
import com.letthemcook.editor.ui.drawing.ROUNDED_RECT_CORNER_RADIUS
import com.letthemcook.editor.ui.drawing.drawProductLabel
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import kotlinx.serialization.*
import kotlin.math.*

@Serializable
data class BlockComponent(
    // Data
    var name: String = "Recipe Block",
    var description: String = "Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block",
    var time: Long = 0L,
    val productNames: MutableList<ProductItemData> = mutableListOf(),
    override var prevComponent: Component = EmptyComponent,
    override var nextComponent: Component = EmptyComponent,
    override var parentComponent: ComposedComponent? = null,
    // Graphics
    @Serializable(with = OffsetSerializer::class) override var position: Offset = Offset.Zero,
    @Serializable(with = SizeSerializer::class) override var size: Size = Size.Zero,
    @Transient var isHighlighted: Boolean = false
) : Component {

    companion object {
        const val MIN_WIDTH = 144f
        const val MIN_HEIGHT = 72f

        const val DRAW_LINES_WIDTH = 2f
        const val DRAW_POINTS_RADIUS = 8f
    }

    @Transient override var containedPointerPosition: Offset? = null

    @Transient private var cachedSize: Size? = null
    @Transient private var cachedPosition: Offset? = null

    @Transient
    val cachedDrawnProductLabelSizes: MutableList<IntSize> = mutableListOf()

    @Transient
    private var shadingQuarterForNextFrame: Relation? = null

    // Graphics

//    fun moveBy(delta: Offset) {
//        this.position = (position + delta).smoothen()
//    }
//
//    fun resizeLeftBy(delta: Float) {
//        if (cachedSize == null) cachedSize = this.size
//        if (cachedPosition == null) cachedPosition = position
//
//        if (delta >= 0f) {
//            size = cachedSize?.let { it.copy(width = max(MIN_WIDTH, it.width - delta)) } ?: this.size
//            position = cachedPosition?.let { it.copy(x = min(it.x + (cachedSize?.width ?: MIN_WIDTH) - MIN_WIDTH, it.x + delta)) } ?: position
//        } else {
//            this.size = cachedSize?.let { it.copy(width = max(MIN_WIDTH, it.width - delta)) } ?: this.size
//            position = cachedPosition?.let { it.copy(x = min(it.x, it.x + delta)) } ?: position
//        }
//    }
//
//    fun resizeRightBy(delta: Float) {
//        if (cachedSize == null) cachedSize = this.size
//
//        size = cachedSize?.let { it.copy(width = max(MIN_WIDTH, it.width + delta)) } ?: this.size
//    }
//
//    fun applyResizing() {
//        cachedSize = null
//        cachedPosition = null
//    }

    fun drawOn(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle,
        frameColor: Color,
        containerColor: Color,
        textColor: Color,
        highlightColor: Color,
        positionXIsCentral: Boolean = false
    ) {
        val color = if (isHighlighted) highlightColor else frameColor

        val maxWidth = MIN_WIDTH.toInt() * 3
        var contentHeightSum = 0f
        var productsContentHeightSum = 0f

        val nameTextLayout = textMeasurer.measure(
            text = name,
            style = nameTextStyle.copy(textAlign = TextAlign.Center),
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += nameTextLayout.size.height

        val descriptionTextLayout = textMeasurer.measure(
            text = description,
            style = contentTextStyle,
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += descriptionTextLayout.size.height

        val timeTextLayout = textMeasurer.measure(
            text = time.toShortTimeString(),
            style = contentTextStyle,
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += timeTextLayout.size.height

        size = size.copy(
            width = max(descriptionTextLayout.size.width, nameTextLayout.size.width) + DRAW_PADDING * 4
        )
        if (positionXIsCentral) {
            position -= Offset(size.width / 2, 0f)
        }

        val productTextLayouts = productNames.map {
            textMeasurer.measure(
                text = it.name,
                style = contentTextStyle
            ).apply {
                productsContentHeightSum += size.height + DRAW_PADDING * 3
            }
        }
        if (productTextLayouts.isNotEmpty()) productsContentHeightSum += DRAW_PADDING * productTextLayouts.size

        val comparedHeight = max(contentHeightSum + DRAW_PADDING * 2, productsContentHeightSum)
        size = size.copy(height = max(MIN_HEIGHT, comparedHeight) + MIN_LINE_LENGTH * 2)

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

        drawScope.drawRoundRect(
            color = containerColor,
            topLeft = position.copy(y = position.y + MIN_LINE_LENGTH),
            size = this.size.copy(
                width = if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width,
                height = this.size.height - 2 * MIN_LINE_LENGTH
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Fill
        )
        drawScope.drawRoundRect(
            color = color,
            topLeft = position.copy(y = position.y + MIN_LINE_LENGTH),
            size = this.size.copy(
                width = if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width,
                height = this.size.height - 2 * MIN_LINE_LENGTH
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Stroke(2f)
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

        var currentPosition = position.copy(x = position.x + DRAW_PADDING * 2, y = position.y + MIN_LINE_LENGTH)

        currentPosition += Offset(0f, DRAW_PADDING)
        drawScope.drawText(
            textLayoutResult = nameTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + this.size.width / 2f - nameTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, nameTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = timeTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + this.size.width / 2f - timeTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, timeTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = descriptionTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + this.size.width / 2f - descriptionTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, descriptionTextLayout.size.height.toFloat())

        if (productTextLayouts.isNotEmpty()) {
            var productCurrentPosition = position.plus(Offset(this.size.width - DRAW_PADDING * 2, DRAW_PADDING + MIN_LINE_LENGTH))
            cachedDrawnProductLabelSizes.clear()

            productTextLayouts.forEachIndexed { index, layout ->
                drawScope.drawProductLabel(
                    textLayout = layout,
                    topLeft = productCurrentPosition,
                    padding = DRAW_PADDING,
                    color = textColor,
                    textColor = containerColor
                )

                cachedDrawnProductLabelSizes.add(layout.size)
                productCurrentPosition += Offset(0f, DRAW_PADDING * 4 + layout.size.height)
            }
        }
//
//        if (functionTextLayouts.isNotEmpty()) {
//            currentPosition += Offset(0f, DRAW_PADDING)
//            drawScope.drawLine(
//                color = color,
//                start = currentPosition.copy(x = position.x),
//                end = currentPosition.copy(x = position.x + size.width),
//                strokeWidth = 1f
//            )
//            currentPosition += Offset(0f, DRAW_PADDING)
//
//            functionTextLayouts.forEachIndexed { index, layout ->
//                drawScope.drawText(
//                    textLayoutResult = layout,
//                    color = textColor,
//                    topLeft = currentPosition,
//                    textDecoration = if (functions[index].isStatic) TextDecoration.Underline else null
//                )
//                currentPosition += Offset(0f, layout.size.height.toFloat())
//            }
//        }
    }

    fun shadeQuarterForNextFrame(relation: Relation) {
        shadingQuarterForNextFrame = relation
    }

    fun calculateSize(
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle
    ): Size {
        val maxWidth = MIN_WIDTH.toInt() * 3
        var contentHeightSum = 0f
        var productsContentHeightSum = 0f

        val nameTextLayout = textMeasurer.measure(
            text = name,
            style = nameTextStyle.copy(textAlign = TextAlign.Center),
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += nameTextLayout.size.height

        val descriptionTextLayout = textMeasurer.measure(
            text = description,
            style = contentTextStyle,
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += descriptionTextLayout.size.height

        val timeTextLayout = textMeasurer.measure(
            text = time.toShortTimeString(),
            style = contentTextStyle,
            constraints = Constraints(maxWidth = maxWidth)
        )
        contentHeightSum += timeTextLayout.size.height

        val productTextLayouts = productNames.map {
            textMeasurer.measure(
                text = it.name,
                style = contentTextStyle
            ).apply {
                productsContentHeightSum += size.height + DRAW_PADDING * 3
            }
        }
        if (productTextLayouts.isNotEmpty()) productsContentHeightSum += DRAW_PADDING * productTextLayouts.size

        val comparedHeight = max(contentHeightSum + DRAW_PADDING * 2, productsContentHeightSum)
        val size = Size(
            width = max(descriptionTextLayout.size.width, nameTextLayout.size.width) + DRAW_PADDING * 4,
            height = max(MIN_HEIGHT, comparedHeight) + MIN_LINE_LENGTH * 2
        )

        return size
    }

    fun clearHighlight() {
        isHighlighted = false
    }

    // Geometry

    fun containsPointer(pointerPosition: Offset, isDown: Boolean): ComponentContainment {
        val containment = checkForContainment(pointerPosition)

        if (containment == ComponentContainment.Whole) {
            if (isDown) containedPointerPosition = pointerPosition - position
            isHighlighted = true
        } else {
            clearHighlight()
        }

        return containment
    }

    // Component

    fun toUnusedBlockComponent(): UnusedBlockComponent {
        return UnusedBlockComponent(name, description, time, productNames)
    }

    // Other

    fun equalsTo(other: BlockComponent): Boolean {
        return try {
            this.name == other.name &&
                    this.position == other.position &&
                    this.size == other.size
        } catch (_: Exception) {
            false
        }
    }

//    private fun SideDirection.getSide(): Side {
//        return when (this) {
//            SideDirection.LEFT -> Side(
//                Offset(position.x, position.y),
//                Offset(position.x, position.y + size.height)
//            )
//            SideDirection.TOP -> Side(
//                Offset(position.x, position.y),
//                Offset(position.x + size.width, position.y)
//            )
//            SideDirection.RIGHT -> Side(
//                Offset(position.x + size.width, position.y),
//                Offset(position.x + size.width, position.y + size.height)
//            )
//            SideDirection.BOTTOM -> Side(
//                Offset(position.x, position.y + size.height),
//                Offset(position.x + size.width, position.y + size.height)
//            )
//        }
//    }
//
//    private fun VertexDirection.getVertex(): Vertex {
//        return when (this) {
//            VertexDirection.TOP_LEFT -> Vertex(Offset(position.x, position.y))
//            VertexDirection.TOP_RIGHT -> Vertex(Offset(position.x + size.width, position.y))
//            VertexDirection.BOTTOM_LEFT -> Vertex(Offset(position.x, position.y + size.height))
//            VertexDirection.BOTTOM_RIGHT -> Vertex(Offset(position.x + size.width, position.y + size.height))
//        }
//    }

    override fun toString(): String {
        return "BlockComponent(${hashCode()}; ${prevComponent.hashCode()}; ${nextComponent.hashCode()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockComponent

        if (name != other.name) return false
        if (productNames.hashCode() != other.productNames.hashCode()) return false
        if (position != other.position) return false
        if (size != other.size) return false
        if (nextComponent.hashCode() != other.nextComponent.hashCode()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + productNames.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + nextComponent.hashCode()
        return result
    }
}
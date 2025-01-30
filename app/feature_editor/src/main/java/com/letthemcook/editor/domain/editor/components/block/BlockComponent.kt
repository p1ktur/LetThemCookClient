package com.letthemcook.editor.domain.editor.components.block

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.core.domain.model.data.file.File
import com.letthemcook.core.domain.dataConvertion.serialization.OffsetSerializer
import com.letthemcook.core.domain.dataConvertion.serialization.SizeSerializer
import com.letthemcook.editor.domain.cooking.BlockCookingState
import com.letthemcook.editor.domain.editor.color.ColorOption
import com.letthemcook.editor.domain.editor.components.block.unused.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.isVisible
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.ROUNDED_RECT_CORNER_RADIUS
import com.letthemcook.editor.ui.drawing.drawProductLabel
import com.letthemcook.editor.ui.drawing.drawRoundRectQuarter
import com.letthemcook.editor.ui.drawing.getBlockBodyBrush
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.max

@Serializable
@SerialName(value = "block")
data class BlockComponent(
    // Data
    var name: String = "Recipe Block",
    var description: String = "Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block",
    var time: Long = 0L,
    val productNames: MutableList<ProductItemData> = mutableListOf(),
    @Transient override var parentComponent: ComposedComponent? = null,
    var cookingState: BlockCookingState = BlockCookingState.NOT_REACHED,
    var file: File? = null,
    // Graphics
    var colorOption: ColorOption = ColorOption.WHITE,
    @Serializable(with = OffsetSerializer::class) override var position: Offset = Offset.Zero,
    @Serializable(with = SizeSerializer::class) override var size: Size = Size.Zero
) : Component {

    companion object {
        const val MIN_WIDTH = 144f
        const val MAX_WIDTH = 432f

        const val MIN_HEIGHT = 72f
    }

    @Transient private val _time = time

    @Transient override var containedPointerPosition: Offset? = null

    @Transient
    val cachedDrawnProductLabelSizes: MutableList<IntSize> = mutableListOf()

    @Transient
    private var shadingQuarterForNextFrame: Relation? = null
    @Transient
    private var highlightingForNextFrame: Boolean = false
    @Transient
    private var highlighting: Boolean = false

    @Transient
    private var localTextMeasurer: TextMeasurer? = null
    @Transient
    private var localNameTextStyle: TextStyle? = null
    @Transient
    private var localContentTextStyle: TextStyle? = null

    @Transient
    private var pretendCookingState: BlockCookingState? = null

    // Graphics

    fun drawOn(
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
        positionXIsCentral: Boolean = false,
        canvasUiState: CanvasUiState
    ) {
        if (!isVisible(canvasUiState)) return

        val currentFrameColor = if (highlightingForNextFrame || highlighting) {
            highlightColor
        } else {
            frameColor
        }
        val currentContainerColor = if (highlightingForNextFrame || highlighting) {
            highlightingForNextFrame = false
            highlightColor.copy(0.25f).compositeOver(containerColor)
        } else when (cookingState) {
            BlockCookingState.NOT_REACHED -> containerColor
            BlockCookingState.WAITING -> warningHighlightColor
            BlockCookingState.COOKING -> highlightColor.copy(0.25f).compositeOver(containerColor)
            BlockCookingState.DONE -> goodHighlightColor
        }

        var contentHeightSum = 0f
        var productsContentHeightSum = 0f
        var productsTopWidth = 0f

        val nameTextLayout = textMeasurer.measure(
            text = name,
            style = nameTextStyle.copy(textAlign = TextAlign.Center),
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += nameTextLayout.size.height

        val descriptionTextLayout = textMeasurer.measure(
            text = description,
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += descriptionTextLayout.size.height

        val timeTextLayout = textMeasurer.measure(
            text = time.toShortTimeString(),
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += timeTextLayout.size.height

        val productTextLayouts = productNames.map {
            textMeasurer.measure(
                text = it.name,
                style = contentTextStyle
            ).apply {
                productsContentHeightSum += size.height + DRAW_PADDING * 3
                productsTopWidth = max(productsTopWidth, size.width.toFloat())
            }
        }
        if (productTextLayouts.isNotEmpty()) {
            productsContentHeightSum += DRAW_PADDING * productTextLayouts.size
            productsTopWidth -= DRAW_PADDING * 2
        }

        val comparedHeight = max(contentHeightSum + DRAW_PADDING * 2, productsContentHeightSum)
        val widthValue = max(max(descriptionTextLayout.size.width, nameTextLayout.size.width), timeTextLayout.size.width) + DRAW_PADDING * 4 + productsTopWidth

        size = size.copy(
            width = max(MIN_WIDTH, widthValue),
            height = max(MIN_HEIGHT, comparedHeight) + COMPONENT_PADDING * 2
        )
        if (positionXIsCentral) {
            position -= Offset(size.width / 2, 0f)
        }

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

        drawScope.drawRoundRect(
            brush = getBlockBodyBrush(
                containerColor = currentContainerColor,
                otherColor = colorOption.color,
                position = position,
                size = size
            ),
            topLeft = position.copy(y = position.y + COMPONENT_PADDING),
            size = this.size.copy(
                width = -productsTopWidth + if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width,
                height = this.size.height - 2 * COMPONENT_PADDING
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Fill
        )
        drawScope.drawRoundRect(
            color = currentFrameColor,
            topLeft = position.copy(y = position.y + COMPONENT_PADDING),
            size = this.size.copy(
                width = -productsTopWidth + if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width,
                height = this.size.height - 2 * COMPONENT_PADDING
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Stroke(4f)
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

        var currentPosition = position.copy(x = position.x + DRAW_PADDING * 2, y = position.y + COMPONENT_PADDING)

        currentPosition += Offset(0f, DRAW_PADDING)
        drawScope.drawText(
            textLayoutResult = nameTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - nameTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, nameTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = timeTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - timeTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, timeTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = descriptionTextLayout,
            color = textColor,
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - descriptionTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, descriptionTextLayout.size.height.toFloat())

        if (productTextLayouts.isNotEmpty()) {
            var productCurrentPosition = position.plus(
                Offset(size.width - DRAW_PADDING * 2 - productsTopWidth, DRAW_PADDING + COMPONENT_PADDING)
            )
            cachedDrawnProductLabelSizes.clear()

            productTextLayouts.forEach { layout ->
                drawScope.drawProductLabel(
                    textLayout = layout,
                    topLeft = productCurrentPosition,
                    padding = DRAW_PADDING,
                    color = textColor,
                    textColor = currentContainerColor
                )

                cachedDrawnProductLabelSizes.add(layout.size)
                productCurrentPosition += Offset(0f, DRAW_PADDING * 4 + layout.size.height)
            }
        }
    }

    fun drawDraggableOn(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle,
        frameColor: Color,
        containerColor: Color,
        textColor: Color,
        centerPosition: Offset
    ) {
        val alpha = 0.5f
        val size = size.copy(height = size.height - 2 * COMPONENT_PADDING)
        val position = centerPosition - Offset(size.width / 2, size.height / 2)

        val currentFrameColor = frameColor.copy(alpha = alpha)
        val currentContainerColor = containerColor.copy(alpha = alpha)

        val nameTextLayout = textMeasurer.measure(
            text = name,
            style = nameTextStyle.copy(textAlign = TextAlign.Center),
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )

        val descriptionTextLayout = textMeasurer.measure(
            text = description,
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )

        val timeTextLayout = textMeasurer.measure(
            text = time.toShortTimeString(),
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )

        var productsTopWidth = 0f
        val productTextLayouts = productNames.map {
            textMeasurer.measure(
                text = it.name,
                style = contentTextStyle
            ).apply {
                productsTopWidth = max(productsTopWidth, this.size.width.toFloat())
            }
        }

        drawScope.drawRoundRect(
            brush = getBlockBodyBrush(
                containerColor = currentContainerColor,
                otherColor = colorOption.color.copy(alpha = alpha),
                position = position,
                size = size
            ),
            topLeft = position,
            size = size.copy(
                width = -productsTopWidth + if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Fill
        )
        drawScope.drawRoundRect(
            color = currentFrameColor,
            topLeft = position,
            size = size.copy(
                width = -productsTopWidth + if (productNames.isNotEmpty()) this.size.width + DRAW_PADDING else this.size.width
            ),
            cornerRadius = CornerRadius(ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS),
            style = Stroke(4f)
        )

        var currentPosition = position.copy(x = position.x + DRAW_PADDING * 2)

        currentPosition += Offset(0f, DRAW_PADDING)
        drawScope.drawText(
            textLayoutResult = nameTextLayout,
            color = textColor.copy(alpha = alpha),
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - nameTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, nameTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = timeTextLayout,
            color = textColor.copy(alpha = alpha),
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - timeTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, timeTextLayout.size.height.toFloat())

        drawScope.drawText(
            textLayoutResult = descriptionTextLayout,
            color = textColor.copy(alpha = alpha),
            topLeft = Offset(
                x = position.x + (size.width - productsTopWidth) / 2f - descriptionTextLayout.size.width / 2f,
                y = currentPosition.y
            )
        )
        currentPosition += Offset(0f, descriptionTextLayout.size.height.toFloat())

        if (productTextLayouts.isNotEmpty()) {
            var productCurrentPosition = position.plus(
                Offset(size.width - DRAW_PADDING * 2 - productsTopWidth, DRAW_PADDING)
            )
            cachedDrawnProductLabelSizes.clear()

            productTextLayouts.forEach { layout ->
                drawScope.drawProductLabel(
                    textLayout = layout,
                    topLeft = productCurrentPosition,
                    padding = DRAW_PADDING,
                    color = textColor.copy(alpha = alpha),
                    textColor = currentContainerColor
                )

                cachedDrawnProductLabelSizes.add(layout.size)
                productCurrentPosition += Offset(0f, DRAW_PADDING * 4 + layout.size.height)
            }
        }
    }

    fun calculateSize(
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle
    ): Size {
        localTextMeasurer = textMeasurer
        localNameTextStyle = nameTextStyle
        localContentTextStyle = contentTextStyle

        var contentHeightSum = 0f
        var productsContentHeightSum = 0f
        var productsTopWidth = 0f

        val nameTextLayout = textMeasurer.measure(
            text = name,
            style = nameTextStyle.copy(textAlign = TextAlign.Center),
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += nameTextLayout.size.height

        val descriptionTextLayout = textMeasurer.measure(
            text = description,
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += descriptionTextLayout.size.height

        val timeTextLayout = textMeasurer.measure(
            text = time.toShortTimeString(),
            style = contentTextStyle,
            constraints = Constraints(maxWidth = MAX_WIDTH.toInt())
        )
        contentHeightSum += timeTextLayout.size.height

        val productTextLayouts = productNames.map {
            textMeasurer.measure(
                text = it.name,
                style = contentTextStyle
            ).apply {
                productsContentHeightSum += size.height + DRAW_PADDING * 3
                productsTopWidth = max(productsTopWidth, size.width.toFloat())
            }
        }
        if (productTextLayouts.isNotEmpty()) {
            productsContentHeightSum += DRAW_PADDING * productTextLayouts.size
            productsTopWidth -= DRAW_PADDING * 2
        }

        val comparedHeight = max(contentHeightSum + DRAW_PADDING * 2, productsContentHeightSum)
        val widthValue = max(max(descriptionTextLayout.size.width, nameTextLayout.size.width), timeTextLayout.size.width) + DRAW_PADDING * 4 + productsTopWidth

        size = size.copy(
            width = max(MIN_WIDTH, widthValue),
            height = max(MIN_HEIGHT, comparedHeight) + COMPONENT_PADDING * 2
        )

        return size
    }

    override fun tryRecalculateSize() {
        val textMeasurer = localTextMeasurer
        val nameTextStyle = localNameTextStyle
        val contentTextStyle = localContentTextStyle

        if (textMeasurer == null) return
        if (nameTextStyle == null) return
        if (contentTextStyle == null) return

        try {
            calculateSize(textMeasurer, nameTextStyle, contentTextStyle)
        } catch (_: Exception) {}
    }

    override fun shadeQuarterForNextFrame(relation: Relation) {
        shadingQuarterForNextFrame = relation
    }

    override fun highlightForNextFrame() {
        highlightingForNextFrame = true
    }

    fun highlight() {
        highlighting = true
    }

    fun removeHighlight() {
        highlighting = false
    }

    // Geometry

    fun containsPointer(pointerPosition: Offset, isDown: Boolean): BlockContainment {
        var containment: BlockContainment = BlockContainment.None

        if (productNames.isNotEmpty()) {
            val productsTopWidth = cachedDrawnProductLabelSizes.maxOf { it.width }
            var productCurrentPosition = position.plus(Offset(size.width - DRAW_PADDING * 2 - productsTopWidth, DRAW_PADDING + COMPONENT_PADDING))

            for (index in productNames.indices) {
                val widthRange = productCurrentPosition.x..productCurrentPosition.x + cachedDrawnProductLabelSizes[index].width + DRAW_PADDING * 4
                val heightRange = productCurrentPosition.y..productCurrentPosition.y + cachedDrawnProductLabelSizes[index].height + DRAW_PADDING * 2

                val containsWhole = pointerPosition.x in widthRange && pointerPosition.y in heightRange

                if (containsWhole) {
                    containment = BlockContainment.ProductLabel(index)
                    break
                }

                productCurrentPosition += Offset(0f, DRAW_PADDING * 4 + cachedDrawnProductLabelSizes[index].height)
            }
        }

        val containsWhole = pointerPosition.x in (position.x..position.x + size.width) &&
                pointerPosition.y in (position.y + COMPONENT_PADDING..position.y + size.height - COMPONENT_PADDING)

        if (containment == BlockContainment.None) {
            containment = if (containsWhole) BlockContainment.Whole else BlockContainment.None
        }

        if (containment == BlockContainment.Whole && isDown) {
            containedPointerPosition = pointerPosition - position
        }

        return containment
    }

    // Component

    fun toUnusedBlockComponent(): UnusedBlockComponent {
        return UnusedBlockComponent(name, description, time, productNames, colorOption)
    }

    // Cooking

    fun getPretendedState(): BlockCookingState {
        val returnedState = pretendCookingState ?: cookingState
        pretendCookingState = null
        return returnedState
    }

    fun pretendCookingState(state: BlockCookingState) {
        pretendCookingState = state
    }

    // Other

    fun restoreTime() {
        time = _time
    }

    override fun toString(): String {
        return "BlockComponent(${hashCode()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockComponent

        if (name != other.name) return false
        if (description != other.description) return false
        if (productNames.hashCode() != other.productNames.hashCode()) return false
        if (position != other.position) return false
        if (size != other.size) return false
        if (cookingState != other.cookingState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + productNames.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + cookingState.hashCode()
        return result
    }
}
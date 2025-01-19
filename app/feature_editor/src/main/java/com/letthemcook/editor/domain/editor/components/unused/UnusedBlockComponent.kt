package com.letthemcook.editor.domain.editor.components.unused

import androidx.compose.ui.geometry.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.BlockComponent.Companion.MIN_HEIGHT
import com.letthemcook.editor.domain.editor.components.BlockComponent.Companion.MIN_WIDTH
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH
import kotlinx.serialization.*
import kotlin.math.*

@Serializable
data class UnusedBlockComponent(
    var name: String = "Recipe Block",
    var description: String = "Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block",
    var time: Long = 0L,
    val productNames: MutableList<ProductItemData> = mutableListOf()
) {
    fun toBlockComponent(prevComponent: Component, nextComponent: Component): BlockComponent {
        return BlockComponent(name, description, time, productNames, prevComponent, nextComponent)
    }

    // Graphics

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
            height = max(MIN_HEIGHT, comparedHeight) + 2 * MIN_LINE_LENGTH
        )

        return size
    }
}
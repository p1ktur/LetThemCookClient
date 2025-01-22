package com.letthemcook.editor.domain.editor.components.block.unused

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import kotlinx.serialization.Serializable

@Serializable
data class UnusedBlockComponent(
    var name: String = "Recipe Block",
    var description: String = "Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block",
    var time: Long = 0L,
    val productNames: MutableList<ProductItemData> = mutableListOf()
) {
    // Other

    fun toBlockComponent(): BlockComponent {
        return BlockComponent(name, description, time, productNames)
    }

    fun toBlockComponent(
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle
    ): BlockComponent {
        return BlockComponent(name, description, time, productNames).apply {
            calculateSize(textMeasurer, nameTextStyle, contentTextStyle)
        }
    }
}
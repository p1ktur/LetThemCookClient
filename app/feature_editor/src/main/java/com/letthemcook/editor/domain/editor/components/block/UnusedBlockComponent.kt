package com.letthemcook.editor.domain.editor.components.block

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.items.ProductItemData
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.editor.domain.editor.color.ColorOption
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UnusedBlockComponent(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Recipe Block",
    var description: String = "Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block",
    var time: Long = 0L,
    val productNames: MutableList<ProductItemData> = mutableListOf(),
    var colorOption: ColorOption = ColorOption.WHITE,
    var file: File? = null
) {
    // Other

    fun toBlockComponent(
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle
    ): BlockComponent {
        return BlockComponent(
            id,
            name,
            description,
            time,
            productNames,
            colorOption = colorOption,
            file = file
        ).apply {
            calculateSize(textMeasurer, nameTextStyle, contentTextStyle)
        }
    }
}
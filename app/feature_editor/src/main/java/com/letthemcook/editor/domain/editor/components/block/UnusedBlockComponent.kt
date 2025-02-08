package com.letthemcook.editor.domain.editor.components.block

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.editor.domain.editor.color.ColorOption
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity
data class UnusedBlockComponent(
    @PrimaryKey(autoGenerate = false) val id: String = UUID.randomUUID().toString(),
    val recipeId: String,
    var name: String = "",
    var description: String = "",
    var time: Long = 0L,
    var products: MutableList<WeightedProduct> = mutableListOf(),
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
            products,
            colorOption = colorOption,
            file = file
        ).apply {
            calculateSize(textMeasurer, nameTextStyle, contentTextStyle)
        }
    }
}
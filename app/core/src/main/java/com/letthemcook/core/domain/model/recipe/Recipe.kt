package com.letthemcook.core.domain.model.recipe

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.items.RecipeItemData
import java.time.LocalDateTime

data class Recipe(
    val id: String,
    val ownerId: String,
    val imageId: String?,
    val authorLogin: String,
    val name: String?,
    val description: String?,
    val recipeJson: String,
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val viewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val reviewsAmount: Int,
    var products: List<String> = emptyList(),
    var publicationDate: LocalDateTime
) {
    fun asItemData(bitmap: Bitmap?): RecipeItemData {
        return RecipeItemData(
            id = id,
            name = name ?: "",
            authorLogin = authorLogin,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            reviewsAmount = reviewsAmount,
            preparationsAmount = preparationsAmount,
            description = description ?: "",
            bitmap = bitmap,
            publicationDate = publicationDate
        )
    }
}

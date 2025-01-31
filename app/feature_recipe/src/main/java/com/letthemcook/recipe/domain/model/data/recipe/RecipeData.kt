package com.letthemcook.recipe.domain.model.data.recipe

import androidx.compose.ui.graphics.ImageBitmap

data class RecipeData(
    val id: Int = 0,
    val name: String = "",
    val authorLogin: String = "",
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val description: String = "",
    val image: ImageBitmap? = null,
    val isPublished: Boolean = false
)

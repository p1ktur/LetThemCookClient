package com.letthemcook.core.domain.model.items

import androidx.compose.ui.graphics.ImageBitmap

data class RecipeItemData(
    val id: String = "",
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

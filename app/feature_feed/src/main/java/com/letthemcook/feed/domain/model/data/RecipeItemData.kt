package com.letthemcook.feed.domain.model.data

import androidx.compose.ui.graphics.ImageBitmap

data class RecipeItemData(
    val id: Int = 0,
    val name: String = "",
    val authorLogin: String = "",
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val description: String = "",
    val image: ImageBitmap? = null
)

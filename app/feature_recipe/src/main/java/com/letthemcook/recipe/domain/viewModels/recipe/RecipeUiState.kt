package com.letthemcook.recipe.domain.viewModels.recipe

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.ImageBitmap
import com.letthemcook.recipe.domain.viewModels.reviews.LikeStatus

data class RecipeUiState(
    val name: String = "",
    val authorLogin: String = "",
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val description: String = "",
    val cookingTime: Long = 0L,
    val categories: List<String> = listOf("Frenc fires"),
    val products: List<String> = listOf("potatoes"),
    val image: ImageBitmap? = null,
    val isLiked: LikeStatus = LikeStatus.NONE,
    val isSaved: Boolean = false,
    val reviewText: TextFieldState = TextFieldState()
)

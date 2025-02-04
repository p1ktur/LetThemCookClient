package com.letthemcook.recipe.domain.viewModels.editedRecipe

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.ImageBitmap
import com.letthemcook.core.domain.model.recipe.Category
import com.letthemcook.core.domain.model.recipe.Product
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.recipe.domain.model.data.WeightedProductItemData
import com.letthemcook.recipe.domain.viewModels.reviews.LikeStatus

data class EditedRecipeUiState(
    val id: String,
    val name: TextFieldState = TextFieldState(),
    val authorLogin: String = "",
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val description: TextFieldState = TextFieldState(),
    val cookingTime: Long = 0L,
    // Categories
    val categoriesSearchText: TextFieldState = TextFieldState(),
    val categoriesFilter: List<Category> = emptyList(),
    val searchedCategories: List<Category> = emptyList(),
    // Products
    val productsSearchText: TextFieldState = TextFieldState(),
    val productsFilter: List<WeightedProductItemData> = emptyList(),
    val searchedProducts: List<Product> = emptyList(),
    // Other
    val image: ImageBitmap? = null,
    val isLiked: LikeStatus = LikeStatus.NONE,
    val isPublished: Boolean = false,
    val reviewText: TextFieldState = TextFieldState(),
    val files: List<File> = emptyList()
)

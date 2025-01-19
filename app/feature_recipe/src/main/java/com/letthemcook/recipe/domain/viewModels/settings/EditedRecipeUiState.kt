package com.letthemcook.recipe.domain.viewModels.settings

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.ImageBitmap
import com.letthemcook.core.domain.model.data.CategoryItemData
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.recipe.domain.model.data.WeightedProductItemData
import com.letthemcook.recipe.domain.viewModels.reviews.LikeStatus

data class EditedRecipeUiState(
    val id: Int = 0,
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
    val categories: List<CategoryItemData> = listOf(
        CategoryItemData(0, "Dariya Fries"),
        CategoryItemData(0, "Dariya Fries 1"),
        CategoryItemData(0, "Dariya Fries  2"),
        CategoryItemData(0, "Dariya Fries   3"),
        CategoryItemData(0, "Dariya Fries    4"),
        CategoryItemData(0, "Dariya Fries     5"),
        CategoryItemData(0, "Dariya Fries      6"),
    ),
    val searchedCategories: List<CategoryItemData> = listOf(CategoryItemData(0, "Dariya Fries")),
    // Products
    val productsSearchText: TextFieldState = TextFieldState(),
    val products: List<WeightedProductItemData> = listOf(WeightedProductItemData("Renat Tomatoes", 100, 2)),
    val searchedProducts: List<ProductItemData> = listOf(ProductItemData(0, "Renat Tomatoes")),
    // Other
    val image: ImageBitmap? = null,
    val isLiked: LikeStatus = LikeStatus.NONE,
    val isPublished: Boolean = false,
    val reviewText: TextFieldState = TextFieldState()
)

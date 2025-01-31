package com.letthemcook.feed.domain.viewModels.search

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.items.CategoryItemData
import com.letthemcook.core.domain.model.items.ProductItemData
import com.letthemcook.core.domain.model.local.FavoredRecipe

data class SearchUiState(
    val searchText: TextFieldState = TextFieldState(),
    val searchType: SearchType = SearchType.NAME,
    val sortType: SortType = SortType.DATE,
    val searchedRecipes: List<FavoredRecipe> = emptyList(),
    val resultsAmount: Int = 0,
    // Categories
    val categoriesSearchText: TextFieldState = TextFieldState(),
    val categoriesFilter: List<CategoryItemData> = listOf(
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
    val productsFilter: List<ProductItemData> = listOf(ProductItemData(0, "Renat Tomatoes")),
    val searchedProducts: List<ProductItemData> = listOf(ProductItemData(0, "Renat Tomatoes"))
) {
    enum class SearchType {
        NAME,
        CATEGORY,
        PRODUCTS
    }

    enum class SortType {
        DATE,
        REVIEWS,
        PREPARATIONS,
        LIKES,
        POPULARITY
    }
}

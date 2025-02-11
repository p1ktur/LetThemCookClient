package com.letthemcook.feed.domain.viewModels.search

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.remote.Category
import com.letthemcook.core.domain.model.remote.Product
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.core.domain.model.items.UserItemData

data class SearchUiState(
    // Common
    val searchClass: SearchClass = SearchClass.RECIPE,
    val searchText: TextFieldState = TextFieldState(),
    // Recipes
    val loadingRecipes: Boolean = false,
    val recipeSearchType: SearchType = SearchType.NAME,
    val recipeSortType: SortType = SortType.DATE,
    val searchedRecipes: List<RecipeItemData> = emptyList(),
    val resultsAmount: Int = 0,
    // Categories
    val loadingCategories: Boolean = false,
    val categoriesSearchText: TextFieldState = TextFieldState(),
    val categoriesFilter: List<Category> = emptyList(),
    val searchedCategories: List<Category> = emptyList(),
    // Products
    val loadingProducts: Boolean = false,
    val productsSearchText: TextFieldState = TextFieldState(),
    val productsFilter: List<Product> = emptyList(),
    val searchedProducts: List<Product> = emptyList(),
    // Users
    val loadingUsers: Boolean = false,
    val searchedUsers: List<UserItemData> = emptyList()
) {
    enum class SearchClass {
        USER,
        RECIPE
    }

    enum class SearchType(val value: String) {
        NAME("name"),
        CATEGORY("category"),
        PRODUCTS("products")
    }

    enum class SortType(val value: String) {
        DATE("date"),
        REVIEWS("reviews"),
        PREPARATIONS("preparations"),
        LIKES("likes"),
        POPULARITY("popularity")
    }

    fun searchHashCode(): Int {
        var result = searchText.text.hashCode()
        result = 31 * result + recipeSearchType.hashCode()
        result = 31 * result + recipeSortType.hashCode()
        result = 31 * result + categoriesFilter.hashCode()
        result = 31 * result + productsFilter.hashCode()
        return result
    }
}

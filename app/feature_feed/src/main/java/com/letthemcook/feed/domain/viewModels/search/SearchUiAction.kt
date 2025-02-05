package com.letthemcook.feed.domain.viewModels.search

sealed interface SearchUiAction {
    data object NavigateBack : SearchUiAction
    data class NavigateToRecipe(val recipeId: String) : SearchUiAction
    data class NavigateToUser(val userId: String) : SearchUiAction

    data class SetSortType(val type: SearchUiState.SortType) : SearchUiAction
    data class SetSearchType(val type: SearchUiState.SearchType) : SearchUiAction

    data object LoadCategories : SearchUiAction
    data class AddCategory(val index: Int) : SearchUiAction
    data class RemoveCategory(val index: Int) : SearchUiAction

    data object LoadProducts : SearchUiAction
    data class AddProduct(val index: Int) : SearchUiAction
    data class RemoveProduct(val index: Int) : SearchUiAction

    data object LoadNextPage : SearchUiAction

    data class SwitchSearchClass(val searchClass: SearchUiState.SearchClass) : SearchUiAction
}
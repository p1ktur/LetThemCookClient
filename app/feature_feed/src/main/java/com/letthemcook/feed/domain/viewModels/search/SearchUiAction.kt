package com.letthemcook.feed.domain.viewModels.search

sealed interface SearchUiAction {
    data object NavigateBack : SearchUiAction
    data class NavigateToRecipe(val id: Int) : SearchUiAction

    data class SetSortType(val type: SearchUiState.SortType) : SearchUiAction
    data class SetSearchType(val type: SearchUiState.SearchType) : SearchUiAction
}
package com.letthemcook.editor.domain.viewModels.cooking

sealed interface CookingUiAction {
    data object NavigateBack : CookingUiAction
    data class NavigateToRecipe(val id: Int) : CookingUiAction

    data class SetSortType(val type: CookingUiState.SortType) : CookingUiAction
    data class SetSearchType(val type: CookingUiState.SearchType) : CookingUiAction
}
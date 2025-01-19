package com.letthemcook.feed.domain.viewModels.savedRecipes

sealed interface SavedRecipesUiAction {
    data object NavigateBack : SavedRecipesUiAction
    data object NavigateToFeed : SavedRecipesUiAction
    data object NavigateToProfile : SavedRecipesUiAction
    data object NavigateToAddRecipe : SavedRecipesUiAction
    data class NavigateToRecipe(val id: Int) : SavedRecipesUiAction
}
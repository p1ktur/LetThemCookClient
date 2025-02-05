package com.letthemcook.feed.domain.viewModels.favoredRecipes

sealed interface FavoredRecipesUiAction {
    data object NavigateBack : FavoredRecipesUiAction
    data object NavigateToFeed : FavoredRecipesUiAction
    data object NavigateToProfile : FavoredRecipesUiAction
    data object NavigateToNewRecipe : FavoredRecipesUiAction
    data class NavigateToRecipe(val recipeId: String) : FavoredRecipesUiAction
}
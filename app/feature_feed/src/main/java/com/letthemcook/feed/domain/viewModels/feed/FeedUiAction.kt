package com.letthemcook.feed.domain.viewModels.feed

sealed interface FeedUiAction {
    data object NavigateToProfile : FeedUiAction
    data object NavigateToAddRecipe : FeedUiAction
    data object NavigateToSearch : FeedUiAction
    data object NavigateToSavedRecipes : FeedUiAction
    data class NavigateToRecipe(val recipeId: String) : FeedUiAction

    data object LoadNextRecipes : FeedUiAction
}
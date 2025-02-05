package com.letthemcook.recipe.domain.viewModels.recipe

sealed interface RecipeUiAction {
    data object NavigateBack : RecipeUiAction
    data object NavigateToHome : RecipeUiAction
    data object NavigateToNewRecipe : RecipeUiAction
    data object NavigateToProfile : RecipeUiAction
    data class NavigateToOtherProfile(val userId: String) : RecipeUiAction

    data object Save : RecipeUiAction
    data object RemoveFromSaved : RecipeUiAction
    data object StartCooking : RecipeUiAction
    data object LikeRecipe : RecipeUiAction
    data object DislikeRecipe : RecipeUiAction

    data object SendReview : RecipeUiAction
    data class LikeReview(val id: String) : RecipeUiAction
    data class DislikeReview(val id: String) : RecipeUiAction
}
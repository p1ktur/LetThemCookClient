package com.letthemcook.recipe.domain.viewModels.recipe

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.file.File

sealed interface RecipeUiAction {
    data object NavigateBack : RecipeUiAction
    data object NavigateToHome : RecipeUiAction
    data object NavigateToNewRecipe : RecipeUiAction
    data object NavigateToProfile : RecipeUiAction
    data class NavigateToOtherProfile(val userId: String) : RecipeUiAction
    data object EditRecipe : RecipeUiAction

    data class ViewMediaFile(val file: File) : RecipeUiAction
    data class ViewRecipeBitmap(val bitmap: Bitmap) : RecipeUiAction

    data object LoadData : RecipeUiAction

    data object Favor : RecipeUiAction
    data object RemoveFromFavored : RecipeUiAction

    data class Cook(val recipeJson: String): RecipeUiAction

    data object LikeRecipe : RecipeUiAction
    data object UnlikeRecipe : RecipeUiAction
    data object DislikeRecipe : RecipeUiAction
    data object UnDislikeRecipe : RecipeUiAction

    data object LoadReviews : RecipeUiAction
    data class SendReview(val text: String) : RecipeUiAction
    data class LikeReview(val index: Int) : RecipeUiAction
    data class UnlikeReview(val index: Int) : RecipeUiAction
}
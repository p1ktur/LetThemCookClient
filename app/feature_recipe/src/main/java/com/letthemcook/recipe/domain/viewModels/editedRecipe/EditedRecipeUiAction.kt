package com.letthemcook.recipe.domain.viewModels.editedRecipe

import com.letthemcook.core.domain.model.file.File
import com.letthemcook.recipe.domain.model.data.WeightedProductItemData

sealed interface EditedRecipeUiAction {
    data object NavigateBack : EditedRecipeUiAction
    data object NavigateToHome : EditedRecipeUiAction
    data object NavigateToAddRecipe : EditedRecipeUiAction
    data object NavigateToProfile : EditedRecipeUiAction
    data class NavigateToOtherProfile(val id: Int) : EditedRecipeUiAction

    data class ViewMediaFile(val file: File) : EditedRecipeUiAction
    data class AddFile(val file: File) : EditedRecipeUiAction

    data class AddWeightedProduct(val data: WeightedProductItemData) : EditedRecipeUiAction
    data class DeleteWeightedProduct(val index: Int) : EditedRecipeUiAction

    data object Publish : EditedRecipeUiAction
    data object Archive : EditedRecipeUiAction
    data object StartEditing : EditedRecipeUiAction
    data object LikeRecipe : EditedRecipeUiAction
    data object DislikeRecipe : EditedRecipeUiAction

    data object SendReview : EditedRecipeUiAction
    data class LikeReview(val id: Int) : EditedRecipeUiAction
    data class DislikeReview(val id: Int) : EditedRecipeUiAction
}
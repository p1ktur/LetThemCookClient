package com.letthemcook.recipe.domain.viewModels.editedRecipe

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.remote.WeightedProduct

sealed interface EditedRecipeUiAction {
    data object NavigateBack : EditedRecipeUiAction
    data object PopToProfile : EditedRecipeUiAction
    data object NavigateToHome : EditedRecipeUiAction
    data object NavigateToNewRecipe : EditedRecipeUiAction
    data object NavigateToProfile : EditedRecipeUiAction
    data class NavigateToOtherProfile(val userId: String) : EditedRecipeUiAction

    data object SaveChanges : EditedRecipeUiAction
    data class UpdateRecipeJson(val recipeJson: String?, val cookingTime: Long?) : EditedRecipeUiAction
    data object DeleteRecipe : EditedRecipeUiAction

    data class SelectMediaFile(val index: Int) : EditedRecipeUiAction
    data class ViewMediaFile(val file: File) : EditedRecipeUiAction
    data class AddFile(val file: File) : EditedRecipeUiAction
    data class DeleteFile(val index: Int) : EditedRecipeUiAction

    data class ViewRecipeBitmap(val bitmap: Bitmap) : EditedRecipeUiAction
    data class UpdateRecipeBitmap(val fileId: String, val bitmap: Bitmap) : EditedRecipeUiAction

    data object LoadCategories : EditedRecipeUiAction
    data class AddCategory(val index: Int) : EditedRecipeUiAction
    data class RemoveCategory(val index: Int) : EditedRecipeUiAction

    data object LoadProducts : EditedRecipeUiAction
    data class AddWeightedProduct(val weightedProduct: WeightedProduct) : EditedRecipeUiAction
    data class RemoveProduct(val index: Int) : EditedRecipeUiAction

    data object Publish : EditedRecipeUiAction
    data object Archive : EditedRecipeUiAction

    data object NavigateToTutorial : EditedRecipeUiAction
    data class EditCooking(val recipeJson: String?) : EditedRecipeUiAction
    data class Cook(val recipeJson: String): EditedRecipeUiAction
}
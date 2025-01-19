package com.letthemcook.recipe.domain.viewModels.settings

import androidx.lifecycle.ViewModel
import com.letthemcook.recipe.domain.model.data.WeightedProductItemData
import com.letthemcook.recipe.domain.viewModels.reviews.ReviewsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditedRecipeViewModel(
    recipeId: Int?
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditedRecipeUiState(recipeId ?: 0))
    val uiState = _uiState.asStateFlow()

    private val _reviewsUiState = MutableStateFlow(ReviewsUiState())
    val reviewsUiState = _reviewsUiState.asStateFlow()


    fun onUiAction(action: EditedRecipeUiAction) {
        when (action) {
            EditedRecipeUiAction.NavigateBack -> Unit
            EditedRecipeUiAction.NavigateToHome -> Unit
            EditedRecipeUiAction.NavigateToAddRecipe -> Unit
            EditedRecipeUiAction.NavigateToProfile -> Unit
            is EditedRecipeUiAction.NavigateToOtherProfile -> Unit
            // TODO save changes on navigation

            is EditedRecipeUiAction.AddWeightedProduct -> addWeightedProduct(action.data)
            is EditedRecipeUiAction.DeleteWeightedProduct -> deleteWeightedProduct(action.index)

            EditedRecipeUiAction.Publish -> publish()
            EditedRecipeUiAction.Archive -> archive()
            EditedRecipeUiAction.StartEditing -> Unit
            EditedRecipeUiAction.LikeRecipe -> likeRecipe()
            EditedRecipeUiAction.DislikeRecipe -> dislikeRecipe()

            EditedRecipeUiAction.SendReview -> sendReview()
            is EditedRecipeUiAction.LikeReview -> likeReview(action.id)
            is EditedRecipeUiAction.DislikeReview -> dislikeReview(action.id)
        }
    }

    private fun addWeightedProduct(data: WeightedProductItemData) {
        _uiState.update {
            it.copy(
                products = it.products + data
            )
        }
    }

    private fun deleteWeightedProduct(index: Int) {
        _uiState.update {
            it.copy(
                products = it.products.minus(it.products[index])
            )
        }
    }

    private fun publish() {

    }

    private fun archive() {

    }

    private fun likeRecipe() {

    }

    private fun dislikeRecipe() {

    }

    private fun sendReview() {

    }

    private fun likeReview(id: Int) {

    }

    private fun dislikeReview(id: Int) {

    }
}
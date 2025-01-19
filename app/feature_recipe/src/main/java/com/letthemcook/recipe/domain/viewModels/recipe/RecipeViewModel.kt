package com.letthemcook.recipe.domain.viewModels.recipe

import androidx.lifecycle.ViewModel
import com.letthemcook.recipe.domain.viewModels.reviews.ReviewsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeViewModel(
    recipeId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState = _uiState.asStateFlow()

    private val _reviewsUiState = MutableStateFlow(ReviewsUiState())
    val reviewsUiState = _reviewsUiState.asStateFlow()

    // TODO save recipe image

    fun onUiAction(action: RecipeUiAction) {
        when (action) {
            RecipeUiAction.NavigateBack -> Unit
            RecipeUiAction.NavigateToHome -> Unit
            RecipeUiAction.NavigateToAddRecipe -> Unit
            RecipeUiAction.NavigateToProfile -> Unit
            is RecipeUiAction.NavigateToOtherProfile -> Unit

            RecipeUiAction.Save -> save()
            RecipeUiAction.RemoveFromSaved -> removeFromSaved()
            RecipeUiAction.StartCooking -> Unit
            RecipeUiAction.LikeRecipe -> likeRecipe()
            RecipeUiAction.DislikeRecipe -> dislikeRecipe()

            RecipeUiAction.SendReview -> sendReview()
            is RecipeUiAction.LikeReview -> likeReview(action.id)
            is RecipeUiAction.DislikeReview -> dislikeReview(action.id)
        }
    }

    private fun save() {

    }

    private fun removeFromSaved() {

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
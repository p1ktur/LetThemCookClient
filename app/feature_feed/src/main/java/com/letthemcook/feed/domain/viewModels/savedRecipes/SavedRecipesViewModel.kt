package com.letthemcook.feed.domain.viewModels.savedRecipes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SavedRecipesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SavedRecipesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        //TODO load recipes from followed users
    }

    fun onUiAction(action: SavedRecipesUiAction) {
        when (action) {
            SavedRecipesUiAction.NavigateBack -> Unit
            SavedRecipesUiAction.NavigateToFeed -> Unit
            SavedRecipesUiAction.NavigateToAddRecipe -> Unit
            SavedRecipesUiAction.NavigateToProfile -> Unit
            is SavedRecipesUiAction.NavigateToRecipe -> Unit
        }
    }
}
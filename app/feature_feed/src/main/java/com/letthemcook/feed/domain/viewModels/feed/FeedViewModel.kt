package com.letthemcook.feed.domain.viewModels.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        //TODO load recipes from followed users
    }

    fun onUiAction(action: FeedUiAction) {
        when (action) {
            FeedUiAction.NavigateToProfile -> Unit
            FeedUiAction.NavigateToAddRecipe -> Unit
            FeedUiAction.NavigateToSearch -> Unit
            FeedUiAction.NavigateToSavedRecipes -> Unit
            is FeedUiAction.NavigateToRecipe -> Unit
        }
    }
}
package com.letthemcook.feed.domain.viewModels.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.remote.RecipeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val recipeManager: RecipeManager,
    localDataManager: LocalDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private var lastPage = 0
    private var allPagesReached = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            localDataManager.getFavoredRecipesAmount().collect { amount ->
                _uiState.update {
                    it.copy(
                        favoredRecipesAmount = amount
                    )
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val recipes = recipeManager.getFeedRecipes(lastPage)

            _uiState.update {
                it.copy(
                    recipes = recipes,
                    loading = false
                )
            }
        }
    }

    fun onUiAction(action: FeedUiAction) {
        when (action) {
            FeedUiAction.NavigateToProfile -> Unit
            FeedUiAction.NavigateToAddRecipe -> Unit
            FeedUiAction.NavigateToSearch -> Unit
            FeedUiAction.NavigateToSavedRecipes -> Unit
            is FeedUiAction.NavigateToRecipe -> Unit

            FeedUiAction.RefreshFeed -> refreshFeed()
            FeedUiAction.LoadNextRecipes -> loadNextRecipes()
        }
    }

    private fun refreshFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    loading = true
                )
            }

            allPagesReached = false
            lastPage = 0

            val recipes = recipeManager.getFeedRecipes(++lastPage)

            if (recipes.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        recipes = recipes,
                        loading = false
                    )
                }
            }
        }
    }

    private fun loadNextRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    loading = true
                )
            }

            if (!allPagesReached) {
                val recipes = recipeManager.getFeedRecipes(++lastPage)

                if (recipes.isEmpty()) {
                    allPagesReached = true
                } else {
                    _uiState.update {
                        it.copy(
                            recipes = (it.recipes + recipes).distinctBy { recipe -> recipe.id },
                            loading = false
                        )
                    }
                }
            }
        }
    }
}
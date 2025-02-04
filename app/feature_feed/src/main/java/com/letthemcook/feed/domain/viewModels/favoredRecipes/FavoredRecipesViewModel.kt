package com.letthemcook.feed.domain.viewModels.favoredRecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoredRecipesViewModel(
    localDataManager: LocalDataManager,
    localFileManager: LocalFileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoredRecipesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = localDataManager
                .getFavoredRecipes()
                .map { recipe ->
                    val bitmap = recipe.imageFile?.let {
                        localFileManager.getFileAsBitmap(it)
                    }

                    recipe.asItemData(bitmap)
                }

            _uiState.update {
                it.copy(
                    favoredRecipes = recipes
                )
            }
        }
    }

    fun onUiAction(action: FavoredRecipesUiAction) {
        when (action) {
            FavoredRecipesUiAction.NavigateBack -> Unit
            FavoredRecipesUiAction.NavigateToFeed -> Unit
            FavoredRecipesUiAction.NavigateToAddRecipe -> Unit
            FavoredRecipesUiAction.NavigateToProfile -> Unit
            is FavoredRecipesUiAction.NavigateToRecipe -> Unit
        }
    }
}
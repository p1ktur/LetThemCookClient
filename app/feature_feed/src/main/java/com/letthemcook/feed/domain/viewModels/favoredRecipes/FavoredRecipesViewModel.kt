package com.letthemcook.feed.domain.viewModels.favoredRecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.core.domain.model.remote.reactions.toLikeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoredRecipesViewModel(
    localDataManager: LocalDataManager,
    localFileManager: LocalFileManager,
    recipeManager: RecipeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoredRecipesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = mutableListOf<RecipeItemData>()
            localDataManager
                .getFavoredRecipes()
                .forEach { recipe ->
                    var currentRecipe = recipe
                    val remoteRecipe = recipeManager.getRecipe(recipe.id)?.apply {
                        isFavored = true
                    }

                    if (remoteRecipe != null && remoteRecipe.hashCode() != recipe.hashCode()) {
                        currentRecipe = remoteRecipe

                        localDataManager.saveRecipe(remoteRecipe)
                    } else if (remoteRecipe == null) {
                        currentRecipe.bitmapId?.let {
                            localFileManager.getFileByUid(it)?.let { file ->
                                localFileManager.deleteFile(file)
                            }
                        }

                        localDataManager.deleteRecipeById(currentRecipe.id)
                    }

                    if (remoteRecipe != null) {
                        val bitmap = currentRecipe.bitmapId?.let {
                            localFileManager.getFileByUid(it)?.let { file ->
                                localFileManager.getFileBytes(file)?.toBitmap()
                            }
                        }

                        val likeStatus = localDataManager.getRecipeReaction(recipe.id).toLikeStatus()

                        recipes.add(currentRecipe.asItemData(bitmap, likeStatus))
                    }
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
            FavoredRecipesUiAction.NavigateToNewRecipe -> Unit
            FavoredRecipesUiAction.NavigateToProfile -> Unit
            is FavoredRecipesUiAction.NavigateToUser -> Unit
            is FavoredRecipesUiAction.NavigateToRecipe -> Unit
        }
    }
}
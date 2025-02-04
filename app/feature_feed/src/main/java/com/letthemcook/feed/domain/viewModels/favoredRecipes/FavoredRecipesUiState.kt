package com.letthemcook.feed.domain.viewModels.favoredRecipes

import com.letthemcook.core.domain.model.items.RecipeItemData

data class FavoredRecipesUiState(
    val favoredRecipes: List<RecipeItemData> = emptyList()
)

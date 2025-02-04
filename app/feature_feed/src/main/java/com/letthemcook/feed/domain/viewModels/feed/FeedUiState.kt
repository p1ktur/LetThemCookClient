package com.letthemcook.feed.domain.viewModels.feed

import com.letthemcook.core.domain.model.items.RecipeItemData

data class FeedUiState(
    val favoredRecipesAmount: Int = 0,
    val recipes: List<RecipeItemData> = emptyList(),
    val loading: Boolean = true
)

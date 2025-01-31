package com.letthemcook.feed.domain.viewModels.feed

import com.letthemcook.feed.domain.model.data.RecipeItemData

data class FeedUiState(
    val savedRecipesAmount: Int = 0,
    val recipes: List<RecipeItemData> = listOf(
        RecipeItemData(
            name = "Recipe name",
            authorLogin = "@username",
            likesAmount = 123,
            dislikesAmount = 123,
            reviewsAmount = 123,
            preparationsAmount = 123,
            description = "French fries (or simply fries, also known as chips among other names) are batonnet or julienne-cut deep-fried potatoes of disputed origin from Belgium or France. They are prepared by cutting potatoes into even strips, drying them, and frying them, usually in a deep fryer. Pre-cut, blanched, and frozen russet potatoes are widely used, and sometimes baked in a regular or convection oven; air fryers are small convection ovens marketed for frying potatoes.",
            imageFile = null,
        )
    )
)

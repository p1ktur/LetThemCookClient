package com.letthemcook.recipe.domain.viewModels.reviews

import com.letthemcook.core.domain.model.items.ReviewItemData

data class ReviewsUiState(
    val reviews: List<ReviewItemData> = listOf(
        ReviewItemData(
            id = 0,
            authorId = 0,
            authorLogin = "@username",
            authorBitmap = null,
            text = "I really like it!",
            likesAmount = 123,
            isLiked = true
        )
    ),
)
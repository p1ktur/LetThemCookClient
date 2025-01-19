package com.letthemcook.recipe.domain.viewModels.reviews

import com.letthemcook.recipe.domain.model.data.ReviewData

data class ReviewsUiState(
    val reviews: List<ReviewData> = listOf(
        ReviewData(
            id = 0,
            authorId = 0,
            authorLogin = "@username",
            authorImage = null,
            text = "I really like it!",
            likesAmount = 123,
            isLiked = true
        )
    ),
)
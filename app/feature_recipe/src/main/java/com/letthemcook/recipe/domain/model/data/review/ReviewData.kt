package com.letthemcook.recipe.domain.model.data.review

import androidx.compose.ui.graphics.ImageBitmap

data class ReviewData(
    val id: Int,
    val authorId: Int,
    val authorLogin: String,
    val authorImage: ImageBitmap?,
    val text: String,
    val likesAmount: Int,
    val isLiked: Boolean,
)

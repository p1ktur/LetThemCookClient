package com.letthemcook.core.domain.model.remote

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.items.ReviewItemData
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String,
    val authorId: String,
    val recipeId: String,
    val reviewText: String,
    val likesAmount: Int
) {
    fun asItemData(authorLogin: String, bitmap: Bitmap?, isLiked: Boolean): ReviewItemData {
        return ReviewItemData(
            id = id,
            authorId = authorId,
            authorLogin = authorLogin,
            authorBitmap = bitmap,
            text = reviewText,
            likesAmount = likesAmount,
            isLiked = isLiked
        )
    }
}
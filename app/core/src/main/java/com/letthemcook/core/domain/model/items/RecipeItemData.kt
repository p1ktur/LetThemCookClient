package com.letthemcook.core.domain.model.items

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.status.LikeStatus
import java.time.LocalDateTime

data class RecipeItemData(
    val id: String,
    val name: String,
    val authorLogin: String,
    val likesAmount: Int,
    val dislikesAmount: Int,
    val reviewsAmount: Int,
    val preparationsAmount: Int,
    val viewsAmount: Int,
    val description: String,
    val likeStatus: LikeStatus,
    val publicationDate: LocalDateTime?,
    val bitmap: Bitmap? = null
)
package com.letthemcook.core.domain.model.items

import android.graphics.Bitmap
import java.time.LocalDateTime

data class RecipeItemData(
    val id: String,
    val name: String,
    val authorLogin: String,
    val likesAmount: Int,
    val dislikesAmount: Int,
    val reviewsAmount: Int,
    val preparationsAmount: Int,
    val description: String,
    val publicationDate: LocalDateTime,
    val bitmap: Bitmap? = null
)
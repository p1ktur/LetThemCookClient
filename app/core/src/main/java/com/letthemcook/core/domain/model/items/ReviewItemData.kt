package com.letthemcook.core.domain.model.items

import android.graphics.Bitmap

data class ReviewItemData(
    val id: String,
    val authorId: String,
    val authorLogin: String,
    val authorBitmap: Bitmap?,
    val text: String,
    var likesAmount: Int,
    val isLiked: Boolean,
)

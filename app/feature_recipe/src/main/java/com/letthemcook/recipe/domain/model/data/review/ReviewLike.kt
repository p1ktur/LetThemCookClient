package com.letthemcook.recipe.domain.model.data.review

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewLike(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reviewId: Int
)

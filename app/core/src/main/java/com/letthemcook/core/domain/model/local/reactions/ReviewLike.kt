package com.letthemcook.core.domain.model.local.reactions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewLike(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reviewId: String
)

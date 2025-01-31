package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.local.reactions.ReviewLike

@Dao
interface ReviewLikeDao {

    @Query("SELECT * FROM ReviewLike WHERE reviewId = :reviewId LIMIT 1")
    suspend fun getReviewLikeByReviewId(reviewId: String): ReviewLike?

    @Upsert
    suspend fun upsertReviewLike(reviewLike: ReviewLike)

    @Delete
    suspend fun deleteReviewLike(reviewLike: ReviewLike)
}
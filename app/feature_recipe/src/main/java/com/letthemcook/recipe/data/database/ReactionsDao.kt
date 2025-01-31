package com.letthemcook.recipe.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.recipe.domain.model.data.recipe.RecipeReaction
import com.letthemcook.recipe.domain.model.data.review.ReviewLike

@Dao
interface ReactionsDao {

    //Recipes
    @Query("SELECT * FROM RecipeReaction WHERE recipeId = :recipeId LIMIT 1")
    suspend fun getReactionByRecipeId(recipeId: Int): RecipeReaction?

    @Upsert
    suspend fun upsertRecipeReaction(recipeReaction: RecipeReaction)

    @Delete
    suspend fun deleteRecipeReaction(recipeReaction: RecipeReaction)

    //Reviews
    @Query("SELECT * FROM ReviewLike WHERE reviewId = :reviewId LIMIT 1")
    suspend fun getReviewLikeByReviewId(reviewId: Int): ReviewLike?

    @Upsert
    suspend fun upsertReviewLike(reviewLike: ReviewLike)

    @Delete
    suspend fun deleteReviewLike(reviewLike: ReviewLike)
}
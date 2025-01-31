package com.letthemcook.core.data.local

import com.letthemcook.core.domain.model.local.RecipeJson
import com.letthemcook.core.domain.model.local.FavoredRecipe
import com.letthemcook.core.domain.model.local.Following
import com.letthemcook.core.domain.model.local.reactions.RecipeReaction
import com.letthemcook.core.domain.model.local.reactions.ReviewLike

class LocalDataManager(
    private val favoredRecipeDao: FavoredRecipeDao,
    private val recipeJsonDao: RecipeJsonDao,
    private val recipeReactionDao: RecipeReactionDao,
    private val reviewLikeDao: ReviewLikeDao,
    private val followingDao: FollowingDao
) {
    //Favored Recipes
    suspend fun getFavoredRecipe(recipeId: String): FavoredRecipe? {
        return favoredRecipeDao.getFavoredRecipeById(recipeId)
    }

    suspend fun saveFavoredRecipe(favoredRecipe: FavoredRecipe) {
        favoredRecipeDao.upsertFavoredRecipe(favoredRecipe)
    }

    suspend fun deleteFavoredRecipe(favoredRecipe: FavoredRecipe) {
        favoredRecipeDao.deleteFavoredRecipe(favoredRecipe)
    }

    //Built Recipes
    suspend fun getRecipeJson(recipeId: String): String? {
        return recipeJsonDao.getRecipeJsonById(recipeId)?.jsonString
    }

    suspend fun saveRecipeJson(recipeId: String, jsonString: String) {
        recipeJsonDao.upsertRecipeJson(RecipeJson(recipeId, jsonString))
    }

    suspend fun deleteRecipeJson(recipeId: String, jsonString: String) {
        recipeJsonDao.deleteRecipeJson(RecipeJson(recipeId, jsonString))
    }

    //Recipe Reactions
    suspend fun getRecipeReaction(recipeId: String): RecipeReaction? {
        return recipeReactionDao.getRecipeReactionByRecipeId(recipeId)
    }

    suspend fun saveRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.upsertRecipeReaction(recipeReaction)
    }

    suspend fun deleteRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.deleteRecipeReaction(recipeReaction)
    }

    //Recipe Reviews
    suspend fun getReviewLike(reviewId: String): ReviewLike? {
        return reviewLikeDao.getReviewLikeByReviewId(reviewId)
    }

    suspend fun saveReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.upsertReviewLike(reviewLike)
    }

    suspend fun deleteReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.deleteReviewLike(reviewLike)
    }

    //User Followings
    suspend fun getFollowing(userId: String): Following? {
        return followingDao.getFollowingByUserId(userId)
    }

    suspend fun saveFollowing(following: Following) {
        followingDao.upsertFollowing(following)
    }

    suspend fun deleteFollowing(following: Following) {
        followingDao.deleteFollowing(following)
    }
}
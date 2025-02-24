package com.letthemcook.core.data.local

import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.domain.model.remote.Recipe
import com.letthemcook.core.domain.model.remote.reactions.RecipeReaction
import com.letthemcook.core.domain.model.remote.reactions.ReviewLike
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LocalDataManager(
    private val recipeDao: RecipeDao,
    private val recipeReactionDao: RecipeReactionDao,
    private val reviewLikeDao: ReviewLikeDao,
    private val authManager: AuthManager
) {
    // Recipes
    suspend fun getRecipeIds(): List<String> {
        val userId = authManager.getUser()?.id ?: return emptyList()
        return recipeDao.getRecipeIds(userId)
    }

    suspend fun getRecipes(): List<Recipe> {
        val userId = authManager.getUser()?.id ?: return emptyList()
        return recipeDao.getRecipes(userId)
    }

    suspend fun getRecipe(recipeId: String): Recipe? {
        val userId = authManager.getUser()?.id ?: return null
        return recipeDao.getRecipeById(recipeId, userId)
    }

    suspend fun getFavoredRecipes(): List<Recipe> {
        val userId = authManager.getUser()?.id ?: return emptyList()
        return recipeDao.getFavoredRecipes(userId)
    }

    fun getFavoredRecipesAmount(): Flow<Int> {
        val userId = authManager.getUser()?.id ?: return flowOf(0)
        return recipeDao.getFavoredRecipesAmount(userId)
    }

    suspend fun saveRecipe(recipe: Recipe) {
        recipeDao.upsertRecipe(recipe)
    }

    suspend fun deleteRecipeById(recipeId: String) {
        val userId = authManager.getUser()?.id ?: return
        recipeDao.deleteRecipeById(recipeId, userId)
    }

    // Recipe Reactions
    suspend fun getRecipeReaction(recipeId: String): RecipeReaction? {
        val userId = authManager.getUser()?.id ?: return null
        return recipeReactionDao.getRecipeReactionByRecipeId(recipeId, userId)
    }

    suspend fun saveRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.upsertRecipeReaction(recipeReaction)
    }

    suspend fun deleteRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.deleteRecipeReaction(recipeReaction)
    }

    // Recipe Reviews
    suspend fun getReviewLike(reviewId: String): ReviewLike? {
        val userId = authManager.getUser()?.id ?: return null
        return reviewLikeDao.getReviewLikeByReviewId(reviewId, userId)
    }

    suspend fun saveReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.upsertReviewLike(reviewLike)
    }

    suspend fun deleteReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.deleteReviewLike(reviewLike)
    }
}
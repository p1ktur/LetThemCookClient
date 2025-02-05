package com.letthemcook.core.data.local

import com.letthemcook.core.domain.model.recipe.reactions.RecipeReaction
import com.letthemcook.core.domain.model.recipe.reactions.ReviewLike
import com.letthemcook.core.domain.model.recipe.Recipe
import kotlinx.coroutines.flow.Flow

class LocalDataManager(
    private val recipeDao: RecipeDao,
    private val recipeReactionDao: RecipeReactionDao,
    private val reviewLikeDao: ReviewLikeDao
) {
    // Recipes
    suspend fun getRecipes(): List<Recipe> {
        return recipeDao.getRecipes()
    }

    suspend fun getRecipe(recipeId: String): Recipe? {
        return recipeDao.getRecipeById(recipeId)
    }

    suspend fun getFavoredRecipes(): List<Recipe> {
        return recipeDao.getFavoredRecipes()
    }

    suspend fun getFavoredRecipe(recipeId: String): Recipe? {
        return recipeDao.getFavoredRecipeById(recipeId)
    }

    fun getFavoredRecipesAmount(): Flow<Int> {
        return recipeDao.getFavoredRecipesAmount()
    }

    suspend fun saveRecipe(recipe: Recipe) {
        recipeDao.upsertRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    // Recipe Reactions
    suspend fun getRecipeReaction(recipeId: String): RecipeReaction? {
        return recipeReactionDao.getRecipeReactionByRecipeId(recipeId)
    }

    suspend fun saveRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.upsertRecipeReaction(recipeReaction)
    }

    suspend fun deleteRecipeReaction(recipeReaction: RecipeReaction) {
        recipeReactionDao.deleteRecipeReaction(recipeReaction)
    }

    // Recipe Reviews
    suspend fun getReviewLike(reviewId: String): ReviewLike? {
        return reviewLikeDao.getReviewLikeByReviewId(reviewId)
    }

    suspend fun saveReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.upsertReviewLike(reviewLike)
    }

    suspend fun deleteReviewLike(reviewLike: ReviewLike) {
        reviewLikeDao.deleteReviewLike(reviewLike)
    }
}
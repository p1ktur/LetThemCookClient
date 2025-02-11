package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.remote.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Default
    @Query("SELECT id FROM Recipe WHERE isFavored = 0 AND ownerId = :userId ORDER BY id DESC")
    suspend fun getRecipeIds(userId: String): List<String>

    @Query("SELECT * FROM Recipe WHERE isFavored = 0 AND ownerId = :userId ORDER BY id DESC")
    suspend fun getRecipes(userId: String): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id = :id AND (ownerId = :userId OR (isFavored = 1 AND ownerId != :userId)) LIMIT 1")
    suspend fun getRecipeById(id: String, userId: String): Recipe?

    // Favored
    @Query("SELECT * FROM Recipe WHERE isFavored = 1 AND ownerId != :userId ORDER BY id DESC")
    suspend fun getFavoredRecipes(userId: String): List<Recipe>

    @Query("SELECT COUNT(*) FROM Recipe WHERE isFavored = 1 AND ownerId != :userId")
    fun getFavoredRecipesAmount(userId: String): Flow<Int>

    // Common
    @Upsert
    suspend fun upsertRecipe(recipe: Recipe)

    @Query("DELETE FROM Recipe WHERE id = :recipeId AND (ownerId = :userId OR (ownerId != :userId AND isFavored = 1))")
    suspend fun deleteRecipeById(recipeId: String, userId: String)
}
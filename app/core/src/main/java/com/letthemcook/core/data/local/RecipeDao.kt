package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.remote.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Default
    @Query("SELECT id FROM Recipe WHERE isFavored = 0 ORDER BY id DESC")
    suspend fun getRecipeIds(): List<String>

    @Query("SELECT * FROM Recipe WHERE isFavored = 0 ORDER BY id DESC")
    suspend fun getRecipes(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: String): Recipe?

    // Favored
    @Query("SELECT * FROM Recipe WHERE isFavored = 1 ORDER BY id DESC")
    suspend fun getFavoredRecipes(): List<Recipe>

    @Query("SELECT COUNT(*) FROM Recipe WHERE isFavored = 1")
    fun getFavoredRecipesAmount(): Flow<Int>

    // Common
    @Upsert
    suspend fun upsertRecipe(recipe: Recipe)

    @Query("DELETE FROM Recipe WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: String)
}
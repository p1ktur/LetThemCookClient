package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.recipe.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Default
    @Query("SELECT * FROM Recipe ORDER BY id DESC")
    suspend fun getRecipes(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: String): Recipe?

    // Favored
    @Query("SELECT * FROM Recipe WHERE isFavored = 1 ORDER BY id DESC")
    suspend fun getFavoredRecipes(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id = :id AND isFavored = 1 LIMIT 1")
    suspend fun getFavoredRecipeById(id: String): Recipe?

    @Query("SELECT COUNT(*) FROM Recipe WHERE isFavored = 1")
    fun getFavoredRecipesAmount(): Flow<Int>

    // Common
    @Upsert
    suspend fun upsertRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}
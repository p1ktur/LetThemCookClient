package com.letthemcook.feed.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.feed.domain.model.data.RecipeItemData

@Dao
interface FavoredRecipeDao {

    @Query("SELECT * FROM RecipeItemData WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Int): RecipeItemData?

    @Upsert
    suspend fun upsertRecipe(recipeItemData: RecipeItemData)

    @Delete
    suspend fun deleteRecipe(recipeItemData: RecipeItemData)
}
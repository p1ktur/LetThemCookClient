package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.local.RecipeJson

@Dao
interface RecipeJsonDao {

    @Query("SELECT * FROM RecipeJson WHERE id = :id LIMIT 1")
    suspend fun getRecipeJsonById(id: String): RecipeJson?

    @Upsert
    suspend fun upsertRecipeJson(recipe: RecipeJson)

    @Delete
    suspend fun deleteRecipeJson(recipe: RecipeJson)
}
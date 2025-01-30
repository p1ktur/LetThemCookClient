package com.letthemcook.editor.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.editor.domain.model.data.BuiltRecipe

@Dao
interface BuiltRecipeDao {

    @Query("SELECT * FROM BuiltRecipe WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Int): BuiltRecipe

    @Upsert
    suspend fun upsertRecipe(recipe: BuiltRecipe)

    @Delete
    suspend fun deleteRecipe(recipe: BuiltRecipe)
}
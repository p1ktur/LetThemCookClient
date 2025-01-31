package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.local.FavoredRecipe

@Dao
interface FavoredRecipeDao {

    @Query("SELECT * FROM FavoredRecipe WHERE id = :id LIMIT 1")
    suspend fun getFavoredRecipeById(id: String): FavoredRecipe?

    @Upsert
    suspend fun upsertFavoredRecipe(favoredRecipe: FavoredRecipe)

    @Delete
    suspend fun deleteFavoredRecipe(favoredRecipe: FavoredRecipe)
}
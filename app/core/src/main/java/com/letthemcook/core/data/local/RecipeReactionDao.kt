package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.local.reactions.RecipeReaction

@Dao
interface RecipeReactionDao {

    @Query("SELECT * FROM RecipeReaction WHERE recipeId = :recipeId LIMIT 1")
    suspend fun getRecipeReactionByRecipeId(recipeId: String): RecipeReaction?

    @Upsert
    suspend fun upsertRecipeReaction(recipeReaction: RecipeReaction)

    @Delete
    suspend fun deleteRecipeReaction(recipeReaction: RecipeReaction)
}
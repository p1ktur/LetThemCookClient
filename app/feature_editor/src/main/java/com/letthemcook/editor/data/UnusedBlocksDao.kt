package com.letthemcook.editor.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent

@Dao
interface UnusedBlocksDao {

    @Query("SELECT * FROM UnusedBlockComponent WHERE recipeId = :recipeId")
    suspend fun getBlocksByRecipeId(recipeId: String): List<UnusedBlockComponent>

    @Upsert
    suspend fun upsertUnusedBlock(unusedBlockComponent: UnusedBlockComponent)

    @Query("DELETE FROM UnusedBlockComponent WHERE recipeId NOT IN (:actualRecipeIds)")
    suspend fun deleteLeftOnes(actualRecipeIds: List<String>)

    @Query("DELETE FROM UnusedBlockComponent WHERE id = :id")
    suspend fun deleteById(id: String)
}
package com.letthemcook.core.data.local

interface UnusedBlocksDaoDeleter {
    suspend fun deleteByRecipeId(recipeId: String)
}
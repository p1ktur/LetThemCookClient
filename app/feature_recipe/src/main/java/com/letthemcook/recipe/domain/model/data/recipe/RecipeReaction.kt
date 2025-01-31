package com.letthemcook.recipe.domain.model.data.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeReaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val liked: Boolean
)

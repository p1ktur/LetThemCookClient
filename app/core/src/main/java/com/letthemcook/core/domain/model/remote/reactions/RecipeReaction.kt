package com.letthemcook.core.domain.model.remote.reactions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeReaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: String,
    val liked: Boolean
)

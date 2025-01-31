package com.letthemcook.core.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeJson(
    @PrimaryKey(autoGenerate = false) val id: String,
    val jsonString: String
)

package com.letthemcook.editor.domain.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BuiltRecipe(
    @PrimaryKey() val id: Int,
    val jsonString: String
)

package com.letthemcook.core.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Following(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String
)

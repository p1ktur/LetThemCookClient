package com.letthemcook.core.domain.model.remote.reactions

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letthemcook.core.domain.model.status.LikeStatus

@Entity
data class RecipeReaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: String,
    val ownerId: String,
    val liked: Boolean
)

fun RecipeReaction?.toLikeStatus(): LikeStatus {
    if (this == null) return LikeStatus.NONE
    return if (liked) LikeStatus.LIKED else LikeStatus.DISLIKED
}

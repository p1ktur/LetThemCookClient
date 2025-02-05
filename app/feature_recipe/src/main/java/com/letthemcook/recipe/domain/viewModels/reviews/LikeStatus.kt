package com.letthemcook.recipe.domain.viewModels.reviews

enum class LikeStatus {
    NONE,
    LIKED,
    DISLIKED;

    fun toBoolean(): Boolean? {
        return when (this) {
            NONE -> null
            LIKED -> true
            DISLIKED -> false
        }
    }
}
package com.letthemcook.core.domain.model.status

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
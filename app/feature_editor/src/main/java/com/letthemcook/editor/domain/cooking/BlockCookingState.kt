package com.letthemcook.editor.domain.cooking

enum class BlockCookingState {
    NOT_REACHED,
    WAITING,
    COOKING,
    DONE;

    override fun toString(): String {
        return when (this) {
            NOT_REACHED -> "Not reached yet"
            WAITING -> "Waiting..."
            COOKING -> "Cooking..."
            DONE -> "Done"
        }
    }
}
package com.letthemcook.editor.domain.cooking

import android.content.Context
import com.letthemcook.editor.R

enum class BlockCookingState {
    NOT_REACHED,
    WAITING,
    COOKING,
    DONE;

    fun toLocalString(context: Context): String {
        return when (this) {
            NOT_REACHED -> context.getString(R.string.not_reached_yet)
            WAITING -> context.getString(R.string.waiting)
            COOKING -> context.getString(R.string.cooking)
            DONE -> context.getString(R.string.done)
        }
    }
}
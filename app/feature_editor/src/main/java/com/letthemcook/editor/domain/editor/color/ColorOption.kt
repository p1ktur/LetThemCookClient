package com.letthemcook.editor.domain.editor.color

import android.graphics.Color.BLACK
import android.graphics.Color.GRAY
import androidx.compose.ui.graphics.Color

enum class ColorOption(val color: Color) {
    WHITE(Color(0xFFFFFFFF)),
    PINK(Color(0xFFFFC0DB)),
    RED(Color(0xFFFF2233)),
    BLUE(Color(0xFF2244FF)),
    LIGHT_BLUE(Color(0xFFADD8E6)),
    GREEN(Color(0xFF00B000)),
    YELLOW(Color(0xFFFFFF00)),
    ORANGE(Color(0xFFFFA540));

    override fun toString(): String {
        return when (this) {
            WHITE -> "White"
            PINK -> "Pink"
            RED -> "Red"
            BLUE -> "Blue"
            LIGHT_BLUE -> "Light blue"
            GREEN -> "Green"
            YELLOW -> "Yellow"
            ORANGE -> "Orange"
        }
    }
}














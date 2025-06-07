package com.letthemcook.editor.domain.editor.color

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.letthemcook.editor.R

enum class ColorOption(val color: Color) {
    WHITE(Color(0xFFFFFFFF)),
    PINK(Color(0xFFFFC0DB)),
    RED(Color(0xFFFF2233)),
    BLUE(Color(0xFF2244FF)),
    LIGHT_BLUE(Color(0xFFADD8E6)),
    GREEN(Color(0xFF00B000)),
    YELLOW(Color(0xFFFFFF00)),
    ORANGE(Color(0xFFFFA540));

    fun toString(context: Context): String {
        return when (this) {
            WHITE -> context.getString(R.string.white)
            PINK -> context.getString(R.string.pink)
            RED -> context.getString(R.string.red)
            BLUE -> context.getString(R.string.blue)
            LIGHT_BLUE -> context.getString(R.string.light_blue)
            GREEN -> context.getString(R.string.green)
            YELLOW -> context.getString(R.string.yellow)
            ORANGE -> context.getString(R.string.orange)
        }
    }
}














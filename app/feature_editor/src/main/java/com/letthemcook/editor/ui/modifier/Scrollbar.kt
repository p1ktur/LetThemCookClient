package com.letthemcook.editor.ui.modifier

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun Modifier.rowScrollbar(
    scrollState: ScrollState,
    height: Dp = 4.dp,
    scrollBarColor: Color = LocalAppTheme.current.text,
    scrollBarCornerRadius: Float = 4f
): Modifier {
    return drawWithContent {
        drawContent()

        val viewportWidth = this.size.width
        val totalContentWidth = scrollState.maxValue.toFloat() + viewportWidth
        val scrollValue = scrollState.value.toFloat()

        val scrollBarWidth = (viewportWidth / totalContentWidth) * viewportWidth
        val scrollBarStartOffset = (scrollValue / totalContentWidth) * viewportWidth

        if (scrollBarWidth != viewportWidth) {
            drawRoundRect(
                cornerRadius = CornerRadius(scrollBarCornerRadius),
                color = scrollBarColor,
                topLeft = Offset(scrollBarStartOffset, 4f + this.size.height),
                size = Size(scrollBarWidth, height.toPx())
            )
        }
    }
}
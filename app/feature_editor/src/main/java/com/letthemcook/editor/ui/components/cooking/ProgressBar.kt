package com.letthemcook.editor.ui.components.cooking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    cookingProgress: Float
) {
    val trackColor = LocalAppTheme.current.screenOne
    val progressColor = LocalAppTheme.current.text

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .drawBehind {
                val progressWidth = size.width * cookingProgress

                drawRect(
                    color = trackColor
                )
                drawRect(
                    color = progressColor,
                    size = size.copy(width = progressWidth)
                )
            }
    )
}
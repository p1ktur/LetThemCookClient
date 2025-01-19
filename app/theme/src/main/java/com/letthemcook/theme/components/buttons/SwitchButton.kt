package com.letthemcook.theme.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    isLookingRight: Boolean,
    containerColor: Color = LocalAppTheme.current.text,
    contentColor: Color = LocalAppTheme.current.background,
    onClick: () -> Unit
) {
    var contentWidth by remember { mutableIntStateOf(64) }

    Box(
        modifier = modifier
            .defaultMinSize(64.dp, 40.dp)
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(4.dp)
            .onSizeChanged {
                contentWidth = it.width
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .absoluteOffset {
                    IntOffset(
                        x = if (isLookingRight) contentWidth / 4 else -contentWidth / 4,
                        y = 0
                    )
                }
                .clip(CircleShape)
                .background(contentColor)
        )
    }
}
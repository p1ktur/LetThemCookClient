package com.letthemcook.theme.components.buttons

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    containerColor: Color = LocalAppTheme.current.screenOne,
    contentColor: Color = LocalAppTheme.current.text,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(32.dp, 32.dp)
            .clip(CircleShape)
            .background(containerColor)
            .run {
                if (isOutlined) {
                    border(1.dp, contentColor, CircleShape)
                } else {
                    this
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = icon,
            contentDescription = "Icon Button Icon",
            tint = contentColor
        )
    }
}
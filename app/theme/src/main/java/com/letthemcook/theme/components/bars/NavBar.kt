package com.letthemcook.theme.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    color: Color = LocalAppTheme.current.background,
    onHomeClick: (() -> Unit)? = null,
    onNewRecipeClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    val contentColor = LocalAppTheme.current.text

    Row(
        modifier = modifier
            .height(36.dp)
            .background(color)
            .drawBehind {
                drawLine(
                    color = contentColor,
                    start = Offset.Zero,
                    end = Offset(size.width, 0f)
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .run {
                    onHomeClick?.let { this.clickable(onClick = onHomeClick) } ?: this
                },
            imageVector = Icons.Outlined.Home,
            contentDescription = "Home Button",
            tint = contentColor
        )
        Icon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .run {
                    onNewRecipeClick?.let { this.clickable(onClick = onNewRecipeClick) } ?: this
                },
            imageVector = Icons.Outlined.Add,
            contentDescription = "New Recipe Button",
            tint = contentColor
        )
        Icon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .run {
                    onProfileClick?.let { this.clickable(onClick = onProfileClick) } ?: this
                },
            imageVector = Icons.Outlined.Person,
            contentDescription = "Profile Button",
            tint = contentColor
        )
    }
}
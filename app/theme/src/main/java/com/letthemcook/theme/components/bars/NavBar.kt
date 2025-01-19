package com.letthemcook.theme.components.bars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val contentColor = LocalAppTheme.current.text

    Row(
        modifier = modifier
            .height(36.dp)
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
                    onAddClick?.let { this.clickable(onClick = onAddClick) } ?: this
                },
            imageVector = Icons.Outlined.Add,
            contentDescription = "Add Button",
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
package com.letthemcook.theme.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    barText: String? = null,
    onBackClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null
) {
    val contentColor = LocalAppTheme.current.text

    Row(
        modifier = modifier
            .height(48.dp)
            .background(LocalAppTheme.current.screenThree)
            .drawBehind {
                drawLine(
                    color = contentColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (onBackClick != null) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick)
                    .padding(6.dp),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back Button",
                tint = contentColor
            )
        } else if (onSearchClick != null) {
            Text(
                text = stringResource(com.letthemcook.theme.R.string.app_name),
                style = LocalAppTheme.current.typography.titleMedium,
                fontFamily = FontFamily(Font(com.letthemcook.theme.R.font.kaushan_script))
            )
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
        if (onSettingsClick != null) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSettingsClick)
                    .padding(6.dp),
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings Button",
                tint = contentColor
            )
        } else if (onSearchClick != null) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSearchClick)
                    .padding(6.dp),
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search Button",
                tint = contentColor
            )
        } else {
            Text(
                text = barText ?: stringResource(com.letthemcook.theme.R.string.app_name),
                style = LocalAppTheme.current.typography.titleMedium,
                fontFamily = if (barText == null) FontFamily(Font(com.letthemcook.theme.R.font.kaushan_script)) else null
            )
        }
    }
}
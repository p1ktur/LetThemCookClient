package com.letthemcook.theme.components.labels

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

enum class LabelIcon {
    NONE,
    REMOVE,
    EDIT
}

@Composable
fun LabelItem(
    modifier: Modifier = Modifier,
    text: String,
    icon: LabelIcon = LabelIcon.REMOVE,
    containerColor: Color = LocalAppTheme.current.text,
    contentColor: Color = LocalAppTheme.current.background,
    onClick: (() -> Unit)? = null,
    onIconClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .run {
                onClick?.let { clickable(onClick = it) } ?: this
            }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = LocalAppTheme.current.typography.bodySmall,
            color = contentColor,
            maxLines = 1
        )
        if (icon != LabelIcon.NONE) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .run {
                        onIconClick?.let { clickable(onClick = it) } ?: this
                    },
                imageVector = when (icon) {
                    LabelIcon.REMOVE -> Icons.Outlined.Clear
                    LabelIcon.EDIT -> Icons.Outlined.Edit
                    else -> Icons.AutoMirrored.Outlined.Label
                },
                contentDescription = "Label Icon",
                tint = contentColor
            )
        }
    }
}
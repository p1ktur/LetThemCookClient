package com.letthemcook.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.editor.domain.editor.color.ColorOption
import com.letthemcook.editor.ui.drawing.getBlockBodyBrush
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun BlockItem(
    modifier: Modifier = Modifier,
    name: String,
    time: Long,
    description: String,
    colorOption: ColorOption,
    containerColor: Color = LocalAppTheme.current.container,
    borderColor: Color = LocalAppTheme.current.text,
    contentColor: Color = LocalAppTheme.current.text,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current

    val trimmedName = remember(name) { if (name.length > 20) name.substring(0, 17) + "..." else name }
    val trimmedDescription = remember(description) { if (description.length > 20) description.substring(0, 17) + "..." else description }

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(getBlockBodyBrush(containerColor, colorOption.color), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(start = 16.dp, top = 8.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = trimmedName,
                style = LocalAppTheme.current.typography.bodyLarge,
                color = contentColor,
                maxLines = 1
            )
            Text(
                text = time.toShortTimeString(context),
                color = contentColor,
                style = LocalAppTheme.current.typography.bodySmall,
                maxLines = 1
            )
            if (trimmedDescription.isNotBlank()) {
                Text(
                    text = trimmedDescription,
                    style = LocalAppTheme.current.typography.bodyMedium,
                    color = contentColor,
                    maxLines = 1
                )
            }
        }
        Column(
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onEditClick)
                    .padding(4.dp),
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit Unused Block Button",
                tint = contentColor
            )
        }
    }
}
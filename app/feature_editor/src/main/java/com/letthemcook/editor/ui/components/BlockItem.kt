package com.letthemcook.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.labels.LabelIcon

@Composable
fun BlockItem(
    modifier: Modifier = Modifier,
    name: String,
    time: Long,
    description: String,
    containerColor: Color = LocalAppTheme.current.container,
    borderColor: Color = LocalAppTheme.current.text,
    contentColor: Color = LocalAppTheme.current.text,
    onClick: (() -> Unit)? = null
) {
    val trimmedName = remember(name) { if (name.length > 20) name.substring(0, 17) + "..." else name }
    val trimmedDescription = remember(description) { if (description.length > 20) description.substring(0, 17) + "..." else description }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .run {
                onClick?.let { clickable(onClick = it) } ?: this
            }
            .padding(vertical = 8.dp, horizontal = 16.dp),
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
            text = time.toShortTimeString(),
            color = contentColor,
            style = LocalAppTheme.current.typography.bodySmall,
            maxLines = 1
        )
        Text(
            text = trimmedDescription,
            style = LocalAppTheme.current.typography.bodyMedium,
            color = contentColor,
            maxLines = 1
        )
    }
}
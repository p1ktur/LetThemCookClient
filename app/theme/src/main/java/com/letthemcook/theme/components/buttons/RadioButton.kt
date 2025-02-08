package com.letthemcook.theme.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun RadioButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String,
    contentColor: Color = LocalAppTheme.current.text,
    labelStyle: TextStyle = LocalAppTheme.current.typography.bodyMedium,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = if (enabled) {
                Modifier
                    .size(24.dp)
                    .border(4.dp, contentColor, CircleShape)
                    .padding(8.dp)
                    .background(contentColor, CircleShape)
            } else {
                Modifier
                    .size(24.dp)
                    .border(2.dp, contentColor, CircleShape)
            }
        )
        Text(
            text = label,
            style = labelStyle
        )
    }
}
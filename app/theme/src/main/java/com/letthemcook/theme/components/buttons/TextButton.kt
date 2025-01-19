package com.letthemcook.theme.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyMedium,
    textColor: Color = LocalAppTheme.current.background,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(120.dp, 40.dp)
            .clip(RoundedCornerShape(20))
            .background(LocalAppTheme.current.text)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
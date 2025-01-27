package com.letthemcook.editor.ui.components.colorChooser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.editor.domain.editor.color.ColorOption
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ColorChooser(
    modifier: Modifier = Modifier,
    selectedOption: ColorOption,
    onOptionSelect: (ColorOption) -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ColorOption.entries.forEach { option ->
            Box(
                modifier = if (selectedOption == option) {
                    Modifier
                        .size(32.dp)
                        .border(2.dp, option.color, CircleShape)
                        .padding(6.dp)
                        .background(option.color, CircleShape)
                } else {
                    Modifier
                        .size(32.dp)
                        .background(option.color, CircleShape)
                }.clickable(
                    onClick = {
                        onOptionSelect(option)
                    }
                )
            )
        }
    }
}
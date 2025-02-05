package com.letthemcook.theme.components.textFields

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun BorderlessTextField(
    modifier: Modifier,
    state: TextFieldState,
    placeholderText: String,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    placeholderTextStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    textColor: Color = LocalAppTheme.current.text
) {
    Box(
        modifier = modifier.height((textStyle.lineHeight.value + 22).dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            state = state,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            lineLimits = TextFieldLineLimits.SingleLine
        )
        if (state.text.isEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = placeholderText,
                style = placeholderTextStyle,
                color = textColor
            )
        }
    }
}
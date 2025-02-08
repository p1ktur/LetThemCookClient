package com.letthemcook.theme.components.textFields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun DigitsTextField(
    modifier: Modifier,
    state: TextFieldState,
    labelText: String,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    labelTextStyle: TextStyle = LocalAppTheme.current.typography.labelLarge,
    textColor: Color = LocalAppTheme.current.text,
    backgroundColor: Color = LocalAppTheme.current.background
) {
    Box(
        modifier = modifier
            .height((textStyle.lineHeight.value + 22).dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .border(1.dp, textColor, RoundedCornerShape(8.dp))
                .padding(top = 10.dp, bottom = 6.dp)
                .padding(horizontal = 10.dp)
                .align(Alignment.BottomCenter),
            state = state,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            lineLimits = TextFieldLineLimits.SingleLine,
            inputTransformation = {
                replace(0, length, asCharSequence().filter { it.isDigit() })
            }
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(backgroundColor)
                .padding(horizontal = 4.dp)
                .align(Alignment.TopStart),
            text = labelText,
            style = labelTextStyle.copy(color = textColor)
        )
    }
}
package com.letthemcook.theme.components.textFields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun MultiLineTextField(
    modifier: Modifier,
    state: TextFieldState,
    labelText: String,
    onSendButtonClick: (() -> Unit)? = null,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    labelTextStyle: TextStyle = LocalAppTheme.current.typography.labelLarge,
    textColor: Color = LocalAppTheme.current.text,
    backgroundColor: Color = LocalAppTheme.current.background
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .border(1.dp, textColor, RoundedCornerShape(8.dp))
                .padding(top = 10.dp, bottom = 6.dp)
                .padding(start = 10.dp)
                .padding(end = if (onSendButtonClick != null) (textStyle.lineHeight.value + 12).dp else 10.dp)
                .align(Alignment.BottomCenter),
            state = state,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            lineLimits = TextFieldLineLimits.MultiLine()
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
        if (onSendButtonClick != null) {
            Icon(
                modifier = Modifier
                    .height((textStyle.lineHeight.value + 14).dp)
                    .padding(bottom = 4.dp, end = 4.dp)
                    .clip(CircleShape)
                    .clickable {
                        onSendButtonClick()
                    }
                    .padding(6.dp)
                    .align(Alignment.BottomEnd),
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Password Show Icon",
                tint = LocalAppTheme.current.text
            )
        }
    }
}
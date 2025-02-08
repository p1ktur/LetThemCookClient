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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun MultiLineTextField(
    modifier: Modifier,
    state: TextFieldState,
    labelText: String? = null,
    placeholderText: String? = null,
    onSendButtonClick: (() -> Unit)? = null,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    labelTextStyle: TextStyle = LocalAppTheme.current.typography.labelLarge,
    placeholderTextStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    textColor: Color = LocalAppTheme.current.text,
    backgroundColor: Color = LocalAppTheme.current.background
) {
    val stateText by snapshotFlow { state.text }.collectAsState("")
    val showPlaceholder by remember {
        derivedStateOf { stateText.isEmpty() }
    }

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
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 8)
        )
        labelText?.let {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(backgroundColor)
                    .padding(horizontal = 4.dp)
                    .align(Alignment.TopStart),
                text = it,
                style = labelTextStyle.copy(color = textColor)
            )
        }
        if (showPlaceholder) placeholderText?.let {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 14.dp, bottom = 6.dp)
                    .align(Alignment.CenterStart),
                text = it,
                style = placeholderTextStyle
            )
        }
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
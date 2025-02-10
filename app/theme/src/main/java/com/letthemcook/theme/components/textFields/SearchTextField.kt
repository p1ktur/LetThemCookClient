package com.letthemcook.theme.components.textFields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.R
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun SearchTextField(
    modifier: Modifier,
    state: TextFieldState,
    textStyle: TextStyle = LocalAppTheme.current.typography.bodyLarge,
    placeholderTextStyle: TextStyle = LocalAppTheme.current.typography.labelLarge,
    textColor: Color = LocalAppTheme.current.text,
    backgroundColor: Color = LocalAppTheme.current.background
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(backgroundColor, RoundedCornerShape(10.dp))
                .padding(vertical = 4.dp)
                .padding(start = 10.dp, end = (textStyle.lineHeight.value + 12).dp)
                .align(Alignment.Center),
            state = state,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            lineLimits = TextFieldLineLimits.SingleLine
        )
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (state.text.isEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(R.string.type_to_search),
                    style = placeholderTextStyle
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(6.dp),
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search Icon",
                tint = LocalAppTheme.current.text
            )
        }
    }
}
package com.letthemcook.theme.components.textFields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ValidatedTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    label: String,
    isPhoneNumber: Boolean = false,
    isPassword: Boolean = false,
    validationFunction: (CharSequence) -> String
) {
    var textWasChanged by remember { mutableStateOf(false) }
    val errorText = validationFunction(textFieldState.text)

    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.isNotBlank()) textWasChanged = true
    }

    SingleLineTextField(
        modifier = modifier,
        state = textFieldState,
        labelText = label,
        isPhoneNumber = isPhoneNumber,
        isPassword = isPassword
    )
    if (textWasChanged && errorText.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Default.Error,
                contentDescription = "Error Show Icon",
                tint = LocalAppTheme.current.errorText
            )
            Text(
                text = errorText,
                style = LocalAppTheme.current.typography.bodySmall,
                color = LocalAppTheme.current.errorText
            )
        }
    }
}
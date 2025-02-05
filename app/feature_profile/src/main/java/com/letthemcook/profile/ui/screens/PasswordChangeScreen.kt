package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiAction
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.textFields.ValidatedTextField
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun PasswordChangeScreen(
    uiState: PasswordChangeUiState,
    onUiAction: (PasswordChangeUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(PasswordChangeUiAction.NavigateBack) }

            setShowNavigationBar(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Change Password",
            style = LocalAppTheme.current.typography.titleMedium
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.passwordChangeResult == PasswordChangeResult.OldPasswordIsIncorrect) {
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
                        text = "Old password is incorrect.",
                        style = LocalAppTheme.current.typography.bodySmall,
                        color = LocalAppTheme.current.errorText
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.oldPassword,
                label = "Old Password",
                isPassword = true,
                validationFunction = { toValidateText ->
                    when (validatePassword(toValidateText)) {
                        PasswordValidationResult.OK -> ""
                        PasswordValidationResult.Empty -> "This field cannot be empty."
                        PasswordValidationResult.TooShort -> "Password it too short."
                        PasswordValidationResult.TooLong -> "Password it too long."
                        PasswordValidationResult.WrongFormat -> "Password must contain at least one capital letter, one small letter and one digit."
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.password,
                label = "New Password",
                isPassword = true,
                validationFunction = { toValidateText ->
                    when (validatePassword(toValidateText)) {
                        PasswordValidationResult.OK -> ""
                        PasswordValidationResult.Empty -> "This field cannot be empty."
                        PasswordValidationResult.TooShort -> "Password it too short."
                        PasswordValidationResult.TooLong -> "Password it too long."
                        PasswordValidationResult.WrongFormat -> "Password must contain at least one capital letter, one small letter and one digit."
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.repeatedPassword,
                label = "Repeat New Password",
                isPassword = true,
                validationFunction = { toValidateText ->
                    if (toValidateText != uiState.password.text) {
                        "Passwords does not match."
                    } else {
                        ""
                    }
                }
            )
        }
        TextButton(
            text = "Change",
            onClick = {
                onUiAction(PasswordChangeUiAction.ChangePassword)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
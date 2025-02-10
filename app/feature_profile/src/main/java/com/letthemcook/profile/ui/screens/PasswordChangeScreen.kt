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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.profile.R
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

    // Strings
    val fieldCannotBeEmpty = stringResource(R.string.field_cannot_be_empty)
    val passwordNumberTooShort = stringResource(R.string.password_too_short)
    val passwordNumberTooLong = stringResource(R.string.password_too_long)
    val passwordCondition = stringResource(R.string.password_condition)
    val passwordsDoNotMatch = stringResource(R.string.passwords_does_not_match)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.change_password),
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
                        text = stringResource(R.string.old_password_is_incorrect),
                        style = LocalAppTheme.current.typography.bodySmall,
                        color = LocalAppTheme.current.errorText
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.oldPassword,
                label = stringResource(R.string.old_password),
                isPassword = true,
                validationFunction = { toValidateText ->
                    when (validatePassword(toValidateText)) {
                        PasswordValidationResult.OK -> ""
                        PasswordValidationResult.Empty -> fieldCannotBeEmpty
                        PasswordValidationResult.TooShort -> passwordNumberTooShort
                        PasswordValidationResult.TooLong -> passwordNumberTooLong
                        PasswordValidationResult.WrongFormat -> passwordCondition
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.password,
                label = stringResource(R.string.new_password),
                isPassword = true,
                validationFunction = { toValidateText ->
                    when (validatePassword(toValidateText)) {
                        PasswordValidationResult.OK -> ""
                        PasswordValidationResult.Empty -> fieldCannotBeEmpty
                        PasswordValidationResult.TooShort -> passwordNumberTooShort
                        PasswordValidationResult.TooLong -> passwordNumberTooLong
                        PasswordValidationResult.WrongFormat -> passwordCondition
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.repeatedPassword,
                label = stringResource(R.string.repeat_new_password),
                isPassword = true,
                validationFunction = { toValidateText ->
                    if (toValidateText != uiState.password.text) {
                        passwordsDoNotMatch
                    } else {
                        ""
                    }
                }
            )
        }
        TextButton(
            text = stringResource(R.string.change),
            onClick = {
                onUiAction(PasswordChangeUiAction.ChangePassword)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
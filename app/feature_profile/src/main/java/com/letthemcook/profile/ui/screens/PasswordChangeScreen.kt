package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePhoneNumber
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiAction
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiState
import com.letthemcook.profile.domain.viewModels.settings.SettingsUiAction
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.base.Theme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.buttons.SwitchButton
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.components.textFields.SingleLineTextField
import com.letthemcook.theme.components.textFields.ValidatedTextField
import com.letthemcook.theme.language.Language

@Composable
fun PasswordChangeScreen(
    uiState: PasswordChangeUiState,
    onUiAction: (PasswordChangeUiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = {
                onUiAction(PasswordChangeUiAction.NavigateBack)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
        BottomInsetSpacer()
    }
}
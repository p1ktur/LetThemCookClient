package com.letthemcook.auth.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.auth.R
import com.letthemcook.auth.domain.viewModels.registration.RegistrationUiAction
import com.letthemcook.auth.domain.viewModels.registration.RegistrationUiState
import com.letthemcook.core.domain.model.auth.registration.RegistrationAuthResult
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateLogin
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.LoginValidationResult
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.components.textFields.ValidatedTextField
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun RegistrationScreen(
    uiState: RegistrationUiState,
    onUiAction: (RegistrationUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()
            setShowToolBar(false)
            setShowNavigationBar(false)
        }
    }

    Column {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(id = R.drawable.screen_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(com.letthemcook.theme.R.string.app_name),
                    style = LocalAppTheme.current.typography.titleLarge,
                    fontFamily = FontFamily(Font(com.letthemcook.theme.R.font.kaushan_script)),
                    color = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(LocalAppTheme.current.background)
                    .padding(16.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Registration",
                    style = LocalAppTheme.current.typography.titleMedium
                )
                RegistrationError(uiState.registrationResult)
                Spacer(modifier = Modifier.height(4.dp))
                ValidatedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = uiState.email,
                    label = "Email",
                    validationFunction = { toValidateText ->
                        when (validateEmail(toValidateText)) {
                            EmailValidationResult.OK -> ""
                            EmailValidationResult.Empty -> "This field cannot be empty."
                            EmailValidationResult.WrongFormat -> "Please follow the email format, such as example@email.com."
                        }
                    }
                )
                ValidatedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = uiState.login,
                    label = "Login",
                    validationFunction = { toValidateText ->
                        when (validateLogin(toValidateText)) {
                            LoginValidationResult.OK -> ""
                            LoginValidationResult.Empty -> "This field cannot be empty."
                            LoginValidationResult.OnlyLettersOrDigitsAllowed -> "Only letters or digits are allowed."
                        }
                    }
                )
                ValidatedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = uiState.password,
                    label = "Password",
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
                ValidatedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = uiState.repeatedPassword,
                    label = "Repeat password",
                    isPassword = true,
                    validationFunction = { toValidateText ->
                        if (toValidateText != uiState.password.text) {
                            "Passwords does not match."
                        } else {
                            ""
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    text = "Register",
                    onClick = {
                        onUiAction(RegistrationUiAction.Register)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onUiAction(RegistrationUiAction.NavigateToLogin)
                            }
                            .padding(6.dp),
                        text = buildAnnotatedString {
                            append("Already have an account? ")
                            withStyle(
                                SpanStyle(
                                    color = LocalAppTheme.current.highlightColor,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("Login")
                            }
                        },
                        style = LocalAppTheme.current.typography.bodyMedium,
                        color = LocalAppTheme.current.text
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun RegistrationError(result: RegistrationAuthResult?) {
    when (result) {
        RegistrationAuthResult.Successful -> Unit
        RegistrationAuthResult.Failed -> {
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
                    text = "Sorry, but registration failed.",
                    style = LocalAppTheme.current.typography.bodySmall,
                    color = LocalAppTheme.current.errorText
                )
            }
        }
        RegistrationAuthResult.UserAlreadyExists -> {
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
                    text = "User with such email or login already exists.",
                    style = LocalAppTheme.current.typography.bodySmall,
                    color = LocalAppTheme.current.errorText
                )
            }
        }
        null -> Unit
    }
}
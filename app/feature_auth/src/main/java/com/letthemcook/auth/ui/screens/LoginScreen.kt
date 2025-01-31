package com.letthemcook.auth.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.letthemcook.auth.R
import com.letthemcook.core.domain.model.auth.LoginAuthResult
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateLogin
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePhoneNumber
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.LoginValidationResult
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult
import com.letthemcook.auth.domain.viewModels.login.LoginUiAction
import com.letthemcook.auth.domain.viewModels.login.LoginUiState
import com.letthemcook.theme.components.textFields.ValidatedTextField
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer


@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUiAction: (LoginUiAction) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.loginResult) {
        uiState.loginResult?.let { result ->
            val toastText = when (result) {
                LoginAuthResult.Failed -> "Registration failed."
                LoginAuthResult.Successful -> "Registration successful."
                LoginAuthResult.UserDoesNotExist -> "User already exists."
                LoginAuthResult.WrongPassword -> "Wrong password."
            }

            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
        }
    }

    Column {
        TopInsetSpacer()
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
                modifier = Modifier,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(LocalAppTheme.current.screenThree)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LoginUiState.LoginOption.entries.forEachIndexed { index, option ->
                        Text(
                            modifier = Modifier
                                .width(72.dp)
                                .clickable {
                                    onUiAction(LoginUiAction.ToggleLoginOption(option))
                                }
                                .padding(8.dp),
                            text = when (option) {
                                LoginUiState.LoginOption.LOGIN -> "Login"
                                LoginUiState.LoginOption.EMAIL -> "Email"
                                LoginUiState.LoginOption.PHONE -> "Phone"
                            },
                            style = LocalAppTheme.current.typography.bodyMedium,
                            textDecoration = if (option == uiState.loginOption) {
                                TextDecoration.Underline
                            } else null,
                            fontWeight = if (option == uiState.loginOption) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            textAlign = TextAlign.Center
                        )
                        if (index != LoginUiState.LoginOption.entries.lastIndex) {
                            VerticalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = LocalAppTheme.current.text
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .clip(RoundedCornerShape(topStart = 24.dp))
                        .background(LocalAppTheme.current.screenThree)
                        .clip(RoundedCornerShape(topEnd = 24.dp))
                        .background(LocalAppTheme.current.background)
                        .padding(16.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Login",
                        style = LocalAppTheme.current.typography.titleMedium
                    )
                    if (uiState.loginResult == LoginAuthResult.UserDoesNotExist) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val loginOptionText = remember(uiState.loginOption) {
                                when (uiState.loginOption) {
                                    LoginUiState.LoginOption.LOGIN -> "login"
                                    LoginUiState.LoginOption.EMAIL -> "email"
                                    LoginUiState.LoginOption.PHONE -> "phone number"
                                }
                            }
                            Icon(
                                modifier = Modifier.size(12.dp),
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error Show Icon",
                                tint = LocalAppTheme.current.errorText
                            )
                            Text(
                                text = "User with such $loginOptionText does not exist.",
                                style = LocalAppTheme.current.typography.bodySmall,
                                color = LocalAppTheme.current.errorText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    when (uiState.loginOption) {
                        LoginUiState.LoginOption.LOGIN -> {
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
                        }
                        LoginUiState.LoginOption.EMAIL -> {
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
                        }
                        LoginUiState.LoginOption.PHONE -> {
                            ValidatedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                textFieldState = uiState.phoneNumber,
                                label = "Phone number",
                                isPhoneNumber = true,
                                validationFunction = { toValidateText ->
                                    when (validatePhoneNumber(toValidateText)) {
                                        PhoneNumberValidationResult.OK -> ""
                                        PhoneNumberValidationResult.Empty -> "This field cannot be empty."
                                        PhoneNumberValidationResult.TooShort -> "Phone number is too short."
                                        PhoneNumberValidationResult.TooLong -> "Phone number is too long."
                                        PhoneNumberValidationResult.OnlyNumbersAllowed -> "Only numbers are allowed."
                                    }
                                }
                            )
                        }
                    }
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
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        modifier = Modifier,
                        text = "Login",
                        onClick = {
                            onUiAction(LoginUiAction.Login)
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
                                    onUiAction(LoginUiAction.NavigateToRegistration)
                                }
                                .padding(6.dp),
                            text = buildAnnotatedString {
                                append("New to ")
                                withStyle(
                                    SpanStyle(
                                        fontFamily = FontFamily(Font(com.letthemcook.theme.R.font.kaushan_script))
                                    )
                                ) {
                                    append(stringResource(com.letthemcook.theme.R.string.app_name))
                                }
                                append("? ")
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
        BottomInsetSpacer()
    }
}
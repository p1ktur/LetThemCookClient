package com.letthemcook.auth.domain.viewModels.login

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.LoginAuthResult

data class LoginUiState(
    // Fields
    val email: TextFieldState = TextFieldState(),
    val phoneNumber: TextFieldState = TextFieldState(),
    val login: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    // Login
    val loginOption: LoginOption = LoginOption.LOGIN,
    val loginResult: LoginAuthResult? = null
) {
    enum class LoginOption {
        LOGIN,
        EMAIL,
        PHONE
    }
}

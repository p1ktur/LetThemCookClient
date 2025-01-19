package com.letthemcook.profile.domain.viewModels.passwordChange

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.profile.domain.passwordChange.result.PasswordChangeResult

data class PasswordChangeUiState(
    val oldPassword: TextFieldState = TextFieldState(),
    val oldPasswordErrorText: String = "",
    val password: TextFieldState = TextFieldState(),
    val repeatedPassword: TextFieldState = TextFieldState(),
    val passwordChangeResult: PasswordChangeResult? = null
)

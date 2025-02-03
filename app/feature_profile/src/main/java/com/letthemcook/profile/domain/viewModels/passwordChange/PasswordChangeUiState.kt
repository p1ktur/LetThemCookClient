package com.letthemcook.profile.domain.viewModels.passwordChange

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult

data class PasswordChangeUiState(
    val oldPassword: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val repeatedPassword: TextFieldState = TextFieldState(),
    val passwordChangeResult: PasswordChangeResult? = null
)

package com.letthemcook.auth.domain.viewModels.registration

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.RegistrationAuthResult

data class RegistrationUiState(
    // Fields
    val email: TextFieldState = TextFieldState(),
    val login: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val repeatedPassword: TextFieldState = TextFieldState(),
    // Registration
    val registrationResult: RegistrationAuthResult? = null
)

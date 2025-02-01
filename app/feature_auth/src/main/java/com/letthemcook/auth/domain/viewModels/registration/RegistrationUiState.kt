package com.letthemcook.auth.domain.viewModels.registration

import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.registration.RegistrationAuthResult

data class RegistrationUiState(
    // Fields
    val email: TextFieldState = TextFieldState("example@mail.com"),
    val login: TextFieldState = TextFieldState("userlogin"),
    val password: TextFieldState = TextFieldState("Dashka123"),
    val repeatedPassword: TextFieldState = TextFieldState("Dashka123"),
    // Registration
    val registrationResult: RegistrationAuthResult? = null
)

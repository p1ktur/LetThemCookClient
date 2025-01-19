package com.letthemcook.profile.domain.viewModels.profile

import androidx.compose.foundation.text.input.TextFieldState

data class ProfileUiState(
    // Edit Fields
    val name: TextFieldState = TextFieldState(),
    val surname: TextFieldState = TextFieldState(),
    val email: TextFieldState = TextFieldState(),
    val phoneNumber: TextFieldState = TextFieldState(),
)

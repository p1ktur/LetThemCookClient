package com.letthemcook.auth.domain.viewModels.registration

sealed interface RegistrationUiAction {
    data object NavigateToLogin : RegistrationUiAction

    data object Register : RegistrationUiAction
}
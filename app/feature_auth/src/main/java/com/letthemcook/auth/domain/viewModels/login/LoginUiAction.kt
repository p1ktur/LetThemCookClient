package com.letthemcook.auth.domain.viewModels.login

sealed interface LoginUiAction {
    data object NavigateToRegistration : LoginUiAction

    data class ToggleLoginOption(val loginOption: LoginUiState.LoginOption) : LoginUiAction
    data object Login : LoginUiAction
}
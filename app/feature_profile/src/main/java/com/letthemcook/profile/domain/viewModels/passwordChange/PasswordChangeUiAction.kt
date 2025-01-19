package com.letthemcook.profile.domain.viewModels.passwordChange

sealed interface PasswordChangeUiAction {
    data object NavigateBack : PasswordChangeUiAction

    data object ChangePassword : PasswordChangeUiAction
}
package com.letthemcook.profile.domain.viewModels.profile

sealed interface ProfileUiAction {
    data object NavigateBack : ProfileUiAction
    data object NavigateToHome : ProfileUiAction
    data object NavigateToAddRecipe : ProfileUiAction
    data object NavigateToSettings : ProfileUiAction
    data object ChangePassword : ProfileUiAction

    data object Login : ProfileUiAction
}
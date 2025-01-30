package com.letthemcook.profile.domain.viewModels.profile

import com.letthemcook.core.domain.model.data.file.File

sealed interface ProfileUiAction {
    data object NavigateBack : ProfileUiAction
    data object NavigateToHome : ProfileUiAction
    data object NavigateToAddRecipe : ProfileUiAction
    data object NavigateToSettings : ProfileUiAction

    data class ViewMediaFile(val file: File) : ProfileUiAction

    data object ChangePassword : ProfileUiAction

    data object Login : ProfileUiAction
}
package com.letthemcook.profile.domain.viewModels.settings

import com.letthemcook.theme.language.Language

sealed interface SettingsUiAction {
    data object NavigateBack : SettingsUiAction
    data object NavigateToHome : SettingsUiAction
    data object NavigateToAddRecipe : SettingsUiAction
    data object NavigateToProfile : SettingsUiAction

    data object ToggleTheme : SettingsUiAction
    data class SetLanguage(val language: Language) : SettingsUiAction
    data object LogOut : SettingsUiAction
}
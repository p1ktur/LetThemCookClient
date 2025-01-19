package com.letthemcook.profile.domain.viewModels.settings

import com.letthemcook.theme.base.Theme
import com.letthemcook.theme.language.Language

data class SettingsUiState(
    val currentTheme: Theme = Theme.LIGHT,
    val currentLanguage: Language = Language.ENGLISH
)

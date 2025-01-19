package com.letthemcook.profile.domain.viewModels.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.theme.base.Theme
import com.letthemcook.theme.language.Language
import com.letthemcook.theme.providers.LanguageStateProvider
import com.letthemcook.theme.providers.ThemeStateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeStateProvider: ThemeStateProvider,
    private val languageStateProvider: LanguageStateProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            themeStateProvider.getTheme().collect { theme ->
                _uiState.update {
                    it.copy(
                        currentTheme = theme
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            languageStateProvider.getLanguage().collect { language ->
                _uiState.update {
                    it.copy(
                        currentLanguage = language
                    )
                }
            }
        }
    }

    fun onUiAction(action: SettingsUiAction) {
        when (action) {
            SettingsUiAction.NavigateBack -> Unit
            SettingsUiAction.NavigateToHome -> Unit
            SettingsUiAction.NavigateToAddRecipe -> Unit
            SettingsUiAction.NavigateToProfile -> Unit

            SettingsUiAction.ToggleTheme -> toggleTheme()
            is SettingsUiAction.SetLanguage -> setLanguage(action.language)
            SettingsUiAction.LogOut -> logOut()
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            val oldTheme = themeStateProvider.getTheme().firstOrNull()
            themeStateProvider.setTheme(!(oldTheme ?: Theme.LIGHT))
        }
    }

    private fun setLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            languageStateProvider.setLanguage(language)
        }
    }

    private fun logOut() {

    }
}
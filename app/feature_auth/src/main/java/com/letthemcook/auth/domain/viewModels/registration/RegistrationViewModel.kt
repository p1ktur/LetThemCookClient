package com.letthemcook.auth.domain.viewModels.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.authorization.AuthManager
import com.letthemcook.core.domain.model.auth.registration.RegistrationData
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateLogin
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.LoginValidationResult
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: RegistrationUiAction) {
        when (action) {
            RegistrationUiAction.NavigateToLogin -> Unit
            RegistrationUiAction.Register -> register()
        }
    }

    private fun register() {
        if (validateEmail(uiState.value.email.text) != EmailValidationResult.OK) return
        if (validateLogin(uiState.value.login.text) != LoginValidationResult.OK) return
        if (validatePassword(uiState.value.password.text) != PasswordValidationResult.OK) return
        if (uiState.value.password.text != uiState.value.repeatedPassword.text) return

        viewModelScope.launch(Dispatchers.IO) {
            val registrationData = RegistrationData(
                login = uiState.value.login.text.toString(),
                email = uiState.value.email.text.toString(),
                password = uiState.value.repeatedPassword.text.toString()
            )

            val registrationResult = authManager.register(registrationData)

            _uiState.update {
                it.copy(
                    registrationResult = registrationResult
                )
            }
        }
    }
}
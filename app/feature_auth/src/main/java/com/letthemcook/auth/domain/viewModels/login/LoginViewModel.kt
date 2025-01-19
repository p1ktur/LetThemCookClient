package com.letthemcook.auth.domain.viewModels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.auth.domain.data.LoginData
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateLogin
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePhoneNumber
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.LoginValidationResult
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
//    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: LoginUiAction) {
        when (action) {
            LoginUiAction.NavigateToRegistration -> Unit
            is LoginUiAction.ToggleLoginOption -> toggleLoginOption(action.loginOption)
            LoginUiAction.Login -> login()
        }
    }

    private fun toggleLoginOption(loginOption: LoginUiState.LoginOption) {
        _uiState.update {
            it.copy(
                loginOption = loginOption
            )
        }
    }

    private fun login() {
        if (uiState.value.loginOption == LoginUiState.LoginOption.EMAIL &&
            validateLogin(uiState.value.login.text) != LoginValidationResult.OK) return
        if (uiState.value.loginOption == LoginUiState.LoginOption.LOGIN &&
            validateEmail(uiState.value.email.text) != EmailValidationResult.OK) return
        if (uiState.value.loginOption == LoginUiState.LoginOption.PHONE &&
            validatePhoneNumber(uiState.value.phoneNumber.text) != PhoneNumberValidationResult.OK) return
        if (validatePassword(uiState.value.password.text) != PasswordValidationResult.OK) return

        viewModelScope.launch(Dispatchers.IO) {
            val loginData = when (uiState.value.loginOption) {
                LoginUiState.LoginOption.LOGIN -> LoginData(
                    login = uiState.value.login.text.toString(),
                    password = uiState.value.password.text.toString()
                )
                LoginUiState.LoginOption.EMAIL -> LoginData(
                    email = uiState.value.email.text.toString(),
                    password = uiState.value.password.text.toString()
                )
                LoginUiState.LoginOption.PHONE -> LoginData(
                    phoneNumber = uiState.value.phoneNumber.text.toString(),
                    password = uiState.value.password.text.toString()
                )
            }

//            val loginResult = authorizationManager.loginUser(loginData)
//
//            _uiState.update {
//                it.copy(
//                    loginResult = loginResult
//                )
//            }
        }
    }
}
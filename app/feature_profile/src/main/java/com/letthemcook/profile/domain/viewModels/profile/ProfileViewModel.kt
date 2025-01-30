package com.letthemcook.profile.domain.viewModels.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
//    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // TODO save profile image

    fun onUiAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.NavigateToAddRecipe -> Unit
//            is ProfileUiAction.ToggleLoginOption -> toggleLoginOption(action.loginOption)
            ProfileUiAction.Login -> Unit
            ProfileUiAction.NavigateBack -> Unit
            ProfileUiAction.NavigateToHome -> Unit
            ProfileUiAction.NavigateToSettings -> Unit

            is ProfileUiAction.ViewMediaFile -> Unit

            ProfileUiAction.ChangePassword -> Unit
        }
    }

//    private fun login() {
//        if (uiState.value.loginOption == LoginOption.EMAIL && validateLogin(uiState.value.login.text) != LoginValidationResult.OK) return
//        if (uiState.value.loginOption == LoginOption.LOGIN && validateEmail(uiState.value.email.text) != EmailValidationResult.OK) return
//        if (uiState.value.loginOption == LoginOption.PHONE && validatePhoneNumber(uiState.value.phoneNumber.text) != PhoneNumberValidationResult.OK) return
//        if (validatePassword(uiState.value.password.text) != PasswordValidationResult.OK) return
//
//        viewModelScope.launch(Dispatchers.IO) {
//            val loginData = when (uiState.value.loginOption) {
//                LoginOption.LOGIN -> LoginData(
//                    login = uiState.value.login.text.toString(),
//                    password = uiState.value.password.text.toString()
//                )
//                LoginOption.EMAIL -> LoginData(
//                    email = uiState.value.email.text.toString(),
//                    password = uiState.value.password.text.toString()
//                )
//                LoginOption.PHONE -> LoginData(
//                    phoneNumber = uiState.value.phoneNumber.text.toString(),
//                    password = uiState.value.password.text.toString()
//                )
//            }
//
////            val loginResult = authorizationManager.loginUser(loginData)
////
////            _uiState.update {
////                it.copy(
////                    loginResult = loginResult
////                )
////            }
//        }
//    }
}
package com.letthemcook.profile.domain.viewModels.passwordChange

import androidx.lifecycle.ViewModel
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PasswordChangeViewModel(
//    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordChangeUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: PasswordChangeUiAction) {
        when (action) {
            PasswordChangeUiAction.NavigateBack -> Unit
            PasswordChangeUiAction.ChangePassword -> changePassword()
        }
    }

    private fun changePassword() {
        _uiState.update {
            it.copy(
                oldPasswordErrorText = ""
            )
        }

        // TODO VALIDATE OLD PASSWORD
        if (uiState.value.oldPassword.text != "") {
            _uiState.update {
                it.copy(
                    oldPasswordErrorText = "Not blank"
                )
            }
            return
        }
        if (validatePassword(uiState.value.password.text) != PasswordValidationResult.OK) return
        if (uiState.value.password.text == uiState.value.repeatedPassword.text) return


//
//        viewModelScope.launch(Dispatchers.IO) {
//            val registrationData = RegistrationData(
//                login = uiState.value.login.text.toString(),
//                email = uiState.value.email.text.toString(),
//                password = uiState.value.repeatedPassword.text.toString()
//            )

//            val registrationResult = authorizationManager.registerUser(registrationData)
//
//            _uiState.update {
//                it.copy(
//                    registrationResult = registrationResult
//                )
//            }
//        }
    }
}
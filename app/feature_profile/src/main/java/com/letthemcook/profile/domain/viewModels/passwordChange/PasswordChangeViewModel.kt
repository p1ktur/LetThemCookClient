package com.letthemcook.profile.domain.viewModels.passwordChange

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.remote.authorization.AuthManager
import com.letthemcook.core.data.remote.user.UserManager
import com.letthemcook.core.domain.model.auth.registration.RegistrationData
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePassword
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordChangeViewModel(
    private val authManager: AuthManager,
    private val userManager: UserManager
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
        if (validatePassword(uiState.value.oldPassword.text) != PasswordValidationResult.OK) return
        if (validatePassword(uiState.value.password.text) != PasswordValidationResult.OK) return
        if (uiState.value.password.text != uiState.value.repeatedPassword.text) return

        viewModelScope.launch(Dispatchers.IO) {
            authManager.getUser()?.id?.let { id ->
                val passwordChangeResult = userManager.updatePassword(
                    userId = id,
                    oldPassword = uiState.value.oldPassword.text.toString(),
                    password = uiState.value.password.text.toString()
                )

                _uiState.update {
                    it.copy(
                        passwordChangeResult = passwordChangeResult
                    )
                }
            }
        }
    }
}
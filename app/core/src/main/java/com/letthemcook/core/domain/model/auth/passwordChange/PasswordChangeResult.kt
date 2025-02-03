package com.letthemcook.core.domain.model.auth.passwordChange

sealed interface PasswordChangeResult {
    data object Failed : PasswordChangeResult
    data object OldPasswordIsIncorrect : PasswordChangeResult
    data object Successful : PasswordChangeResult
}
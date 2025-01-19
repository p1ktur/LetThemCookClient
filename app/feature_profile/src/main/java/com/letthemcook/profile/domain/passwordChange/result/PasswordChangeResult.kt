package com.letthemcook.profile.domain.passwordChange.result

sealed interface PasswordChangeResult {
    data object Failed : PasswordChangeResult
    data object Successful : PasswordChangeResult
}
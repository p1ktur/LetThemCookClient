package com.letthemcook.core.domain.model.auth.login

sealed interface LoginAuthResult {
    data object Failed : LoginAuthResult
    data object Successful : LoginAuthResult
    data object WrongPassword : LoginAuthResult
    data object UserDoesNotExist : LoginAuthResult
}
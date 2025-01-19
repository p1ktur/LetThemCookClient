package com.letthemcook.auth.domain.authorization.result

sealed interface LoginAuthResult {
    data object Failed : LoginAuthResult
    data object Successful : LoginAuthResult
    data object UserDoesNotExist : LoginAuthResult
}
package com.letthemcook.auth.domain.authorization.result

sealed interface RegistrationAuthResult {
    data object Failed : RegistrationAuthResult
    data object Successful : RegistrationAuthResult
    data object UserAlreadyExists : RegistrationAuthResult
}
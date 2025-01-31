package com.letthemcook.core.domain.model.auth

sealed interface RegistrationAuthResult {
    data object Failed : RegistrationAuthResult
    data object Successful : RegistrationAuthResult
    data object UserAlreadyExists : RegistrationAuthResult
}
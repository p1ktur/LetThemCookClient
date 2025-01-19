package com.letthemcook.core.domain.validation.result

sealed interface LoginValidationResult {
    data object OK : LoginValidationResult
    data object Empty : LoginValidationResult
    data object OnlyLettersOrDigitsAllowed : LoginValidationResult
}
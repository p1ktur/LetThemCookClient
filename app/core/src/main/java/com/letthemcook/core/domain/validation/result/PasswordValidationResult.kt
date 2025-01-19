package com.letthemcook.core.domain.validation.result

sealed interface PasswordValidationResult {
    data object OK : PasswordValidationResult
    data object Empty : PasswordValidationResult
    data object TooShort : PasswordValidationResult
    data object TooLong : PasswordValidationResult
    data object WrongFormat : PasswordValidationResult
}
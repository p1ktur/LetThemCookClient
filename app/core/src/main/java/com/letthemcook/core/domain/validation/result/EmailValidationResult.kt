package com.letthemcook.core.domain.validation.result

sealed interface EmailValidationResult {
    data object OK : EmailValidationResult
    data object Empty : EmailValidationResult
    data object WrongFormat : EmailValidationResult
}
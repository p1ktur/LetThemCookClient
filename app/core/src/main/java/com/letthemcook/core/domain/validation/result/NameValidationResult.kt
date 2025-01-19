package com.letthemcook.core.domain.validation.result

sealed interface NameValidationResult {
    data object OK : NameValidationResult
    data object OnlyLettersAllowed : NameValidationResult
}
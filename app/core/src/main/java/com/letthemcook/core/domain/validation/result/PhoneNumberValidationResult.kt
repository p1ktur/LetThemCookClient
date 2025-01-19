package com.letthemcook.core.domain.validation.result

sealed interface PhoneNumberValidationResult {
    data object OK : PhoneNumberValidationResult
    data object Empty : PhoneNumberValidationResult
    data object TooShort : PhoneNumberValidationResult
    data object TooLong : PhoneNumberValidationResult
    data object OnlyNumbersAllowed : PhoneNumberValidationResult
}
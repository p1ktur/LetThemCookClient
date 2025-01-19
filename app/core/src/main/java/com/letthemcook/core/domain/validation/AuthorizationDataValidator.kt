package com.letthemcook.core.domain.validation

import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.LoginValidationResult
import com.letthemcook.core.domain.validation.result.NameValidationResult
import com.letthemcook.core.domain.validation.result.PasswordValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult

object AuthorizationDataValidator {

    private const val EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
    private const val PASSWORD_FORMAT = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"

    fun validateName(name: CharSequence): NameValidationResult {
//        if (name.isBlank()) return NameValidationResult.Empty
        if (!name.all { it.isLetter() || it.isDigit() }) return NameValidationResult.OnlyLettersAllowed
        return NameValidationResult.OK
    }

    fun validateLogin(login: CharSequence): LoginValidationResult {
        if (login.isBlank()) return LoginValidationResult.Empty
        if (!login.all { it.isLetter() || it.isDigit() }) return LoginValidationResult.OnlyLettersOrDigitsAllowed
        return LoginValidationResult.OK
    }

    fun validateEmail(email: CharSequence): EmailValidationResult {
        if (email.isBlank()) return EmailValidationResult.Empty
        if (!EMAIL_FORMAT.toRegex().matches(email)) return EmailValidationResult.WrongFormat
        return EmailValidationResult.OK
    }

    fun validatePhoneNumber(phoneNumber: CharSequence): PhoneNumberValidationResult {
        if (phoneNumber.isBlank()) return PhoneNumberValidationResult.Empty
        if (phoneNumber.length < 7) return PhoneNumberValidationResult.TooShort
        if (phoneNumber.length > 13) return PhoneNumberValidationResult.TooLong
        if (!phoneNumber.all { it.isDigit() }) return PhoneNumberValidationResult.OnlyNumbersAllowed
        return PhoneNumberValidationResult.OK
    }

    fun validatePassword(password: CharSequence): PasswordValidationResult {
        if (password.isBlank()) return PasswordValidationResult.Empty
        if (password.length < 8) return PasswordValidationResult.TooShort
        if (password.length > 16) return PasswordValidationResult.TooLong
        if (!PASSWORD_FORMAT.toRegex().matches(password)) return PasswordValidationResult.WrongFormat
        return PasswordValidationResult.OK
    }
}
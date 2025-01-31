package com.letthemcook.core.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationData(
    val login: String,
    val email: String,
    val password: String
)

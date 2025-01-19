package com.letthemcook.auth.domain.data

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationData(
    val login: String,
    val email: String,
    val password: String
)

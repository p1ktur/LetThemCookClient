package com.letthemcook.core.domain.model.auth.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationData(
    val login: String,
    val email: String,
    val password: String
)

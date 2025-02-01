package com.letthemcook.core.domain.model.auth.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val login: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String
)

package com.letthemcook.auth.domain.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val login: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String
)

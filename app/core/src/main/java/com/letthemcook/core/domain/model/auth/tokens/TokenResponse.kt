package com.letthemcook.core.domain.model.auth.tokens

import com.letthemcook.core.domain.model.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

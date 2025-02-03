package com.letthemcook.core.domain.http

import kotlinx.serialization.Serializable

@Serializable
data class RequestPair(
    val first: String,
    val second: String
)

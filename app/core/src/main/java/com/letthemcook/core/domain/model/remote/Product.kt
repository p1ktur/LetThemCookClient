package com.letthemcook.core.domain.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String
)

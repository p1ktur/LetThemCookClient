package com.letthemcook.core.domain.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String
)

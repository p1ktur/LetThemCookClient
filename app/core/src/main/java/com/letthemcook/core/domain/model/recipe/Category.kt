package com.letthemcook.core.domain.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String
)

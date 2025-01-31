package com.letthemcook.core.domain.model.items

import kotlinx.serialization.Serializable

@Serializable
data class ProductItemData(
    val id: Int,
    val name: String
)

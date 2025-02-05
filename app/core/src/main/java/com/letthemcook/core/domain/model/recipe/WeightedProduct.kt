package com.letthemcook.core.domain.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class WeightedProduct(
    val data: Product,
    val weight: Int,
    val amount: Int
) {
    override fun toString(): String {
        return StringBuilder().apply {
            append(data.name)
            if (weight > 0) append(", ${weight}g")
            if (amount > 0) {
                val piecesSuffix = if (amount == 1) "pc" else "pcs"
                append(", $amount $piecesSuffix")
            }
        }.toString()
    }
}
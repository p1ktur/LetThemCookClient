package com.letthemcook.core.domain.model.remote

import com.letthemcook.core.domain.providers.GlobalLanguageProvider
import kotlinx.serialization.Serializable

@Serializable
data class WeightedProduct(
    val data: Product,
    val weight: Int,
    val amount: Int
) {
    override fun toString(): String {
        val gramsText = when (GlobalLanguageProvider.language) {
            "uk" -> "г"
            else -> "g"
        }
        val piecesText = when (GlobalLanguageProvider.language) {
            "uk" -> "шт"
            else -> if (amount == 1) "pc" else "pcs"
        }
        return StringBuilder().apply {
            append(data.name)
            if (weight > 0) append(", ${weight}$gramsText")
            if (amount > 0) {
                append(", $amount $piecesText")
            }
        }.toString()
    }
}
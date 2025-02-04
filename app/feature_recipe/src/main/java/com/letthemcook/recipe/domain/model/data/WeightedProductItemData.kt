package com.letthemcook.recipe.domain.model.data

import com.letthemcook.core.domain.model.recipe.Product

data class WeightedProductItemData(
    val data: Product,
    val weight: Int,
    val pieces: Int
) {
    override fun toString(): String {
        val piecesSuffix = if (pieces == 1) "pc" else "pcs"
        return "${data.name}, ${weight}g, $pieces $piecesSuffix"
    }
}
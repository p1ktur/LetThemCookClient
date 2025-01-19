package com.letthemcook.recipe.domain.model.data

data class WeightedProductItemData(
    val name: String,
    val weight: Int,
    val pieces: Int
) {
    override fun toString(): String {
        val piecesSuffix = if (pieces == 1) "pc" else "pcs"
        return "$name, ${weight}g, $pieces $piecesSuffix"
    }
}
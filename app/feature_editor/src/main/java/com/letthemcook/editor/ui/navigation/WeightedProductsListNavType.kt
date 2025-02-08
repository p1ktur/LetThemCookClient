package com.letthemcook.editor.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.letthemcook.core.domain.model.remote.WeightedProduct
import kotlinx.serialization.json.Json

object WeightedProductsListNavType : NavType<List<WeightedProduct>>(false) {

    val json = Json {
        encodeDefaults = true
    }

    override fun get(bundle: Bundle, key: String): List<WeightedProduct>? {
        return bundle.getString(key)?.let(json::decodeFromString)
    }

    override fun put(bundle: Bundle, key: String, value: List<WeightedProduct>) {
        bundle.putString(key, json.encodeToString(value))
    }

    override fun parseValue(value: String): List<WeightedProduct> {
        return json.decodeFromString(value)
    }

    override fun serializeAsValue(value: List<WeightedProduct>): String {
        return json.encodeToString(value)
    }
}

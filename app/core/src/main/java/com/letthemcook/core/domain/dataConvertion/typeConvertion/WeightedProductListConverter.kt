package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.recipe.WeightedProduct
import kotlinx.serialization.json.Json

class WeightedProductListConverter {

    private val json = Json {
        encodeDefaults = true
    }

    @TypeConverter
    fun listToString(list: List<WeightedProduct>): String = json.encodeToString(list)

    @TypeConverter
    fun stringToList(string: String): List<WeightedProduct> = json.decodeFromString(string)
}
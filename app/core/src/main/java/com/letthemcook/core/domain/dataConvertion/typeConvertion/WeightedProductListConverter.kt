package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.remote.WeightedProduct
import kotlinx.serialization.json.Json

class WeightedProductListConverter {

    private val json = Json {
        encodeDefaults = true
    }

    @TypeConverter
    fun listToString(list: List<WeightedProduct>): String = json.encodeToString(list)

    @TypeConverter
    fun stringToList(string: String): List<WeightedProduct> = json.decodeFromString(string)

    @TypeConverter
    fun mutableListToString(list: MutableList<WeightedProduct>): String = json.encodeToString(list)

    @TypeConverter
    fun stringToMutableList(string: String): MutableList<WeightedProduct> = json.decodeFromString(string)
}
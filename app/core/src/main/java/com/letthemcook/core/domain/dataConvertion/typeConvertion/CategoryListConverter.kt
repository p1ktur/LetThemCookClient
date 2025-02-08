package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.remote.Category
import kotlinx.serialization.json.Json

class CategoryListConverter {

    private val json = Json {
        encodeDefaults = true
    }

    @TypeConverter
    fun listToString(list: List<Category>): String = json.encodeToString(list)

    @TypeConverter
    fun stringToList(string: String): List<Category> = json.decodeFromString(string)
}
package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.file.File
import kotlinx.serialization.json.Json

class StringListConverter {

    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString()

    @TypeConverter
    fun stringToList(string: String): List<String> {
        if (string.isBlank()) return emptyList()
        return string.split(", ")
    }
}
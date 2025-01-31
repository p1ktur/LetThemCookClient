package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.file.File
import kotlinx.serialization.json.Json

class FileConverter {

    private val json = Json {
        encodeDefaults = true
    }

    @TypeConverter
    fun fileToString(file: File): String = json.encodeToString(file)

    @TypeConverter
    fun stringToFile(string: String): File = json.decodeFromString(string)
}
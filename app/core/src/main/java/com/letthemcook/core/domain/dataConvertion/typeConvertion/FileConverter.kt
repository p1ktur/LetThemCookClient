package com.letthemcook.core.domain.dataConvertion.typeConvertion

import android.net.Uri
import androidx.room.TypeConverter
import com.letthemcook.core.domain.model.data.file.File
import kotlinx.serialization.json.Json

class FileConverter {

    val json = Json {
        encodeDefaults = true
    }

    @TypeConverter
    fun fileToString(file: File): String = json.encodeToString(file)

    @TypeConverter
    fun stringToFile(string: String): File = json.decodeFromString(string)
}
package com.cooking.media.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.letthemcook.core.domain.model.data.file.File
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString

class FileNavType : NavType<File>(false) {

    private val json = Json {
        encodeDefaults = true
    }

    override fun get(bundle: Bundle, key: String): File? {
        return bundle.getString(key)?.let(json::decodeFromString)
    }

    override fun put(bundle: Bundle, key: String, value: File) {
        bundle.putString(key, json.encodeToString(value))
    }

    override fun parseValue(value: String): File {
        return json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: File): String {
        return Uri.encode(json.encodeToString(value))
    }
}

package com.letthemcook.media.ui.host

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.letthemcook.core.data.remote.RemoteFileManager
import kotlinx.serialization.json.Json

object RemoteFileManagerRequestParamsNavType : NavType<RemoteFileManager.RequestParams?>(true) {

    private val json = Json {
        encodeDefaults = true
    }

    override fun get(bundle: Bundle, key: String): RemoteFileManager.RequestParams? {
        return bundle.getString(key)?.let(json::decodeFromString)
    }

    override fun put(bundle: Bundle, key: String, value: RemoteFileManager.RequestParams?) {
        bundle.putString(key, json.encodeToString(value))
    }

    override fun parseValue(value: String): RemoteFileManager.RequestParams? {
        return json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: RemoteFileManager.RequestParams?): String {
        return Uri.encode(json.encodeToString(value))
    }
}

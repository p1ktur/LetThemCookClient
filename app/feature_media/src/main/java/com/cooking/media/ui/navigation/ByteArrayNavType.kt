package com.cooking.media.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import java.util.Base64

class ByteArrayNavType : NavType<ByteArray>(false) {

    override fun get(bundle: Bundle, key: String): ByteArray? {
        return bundle.getString(key)?.let(Base64.getDecoder()::decode)
    }

    override fun put(bundle: Bundle, key: String, value: ByteArray) {
        bundle.putString(key, Base64.getEncoder().encodeToString(value))
    }

    override fun parseValue(value: String): ByteArray {
        return Base64.getDecoder().decode(Uri.decode(value))
    }

    override fun serializeAsValue(value: ByteArray): String {
        return Uri.encode(Base64.getEncoder().encodeToString(value))
    }
}

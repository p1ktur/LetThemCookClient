package com.letthemcook.theme.language

import java.util.Locale

enum class Language {
    ENGLISH,
    UKRAINIAN;

    companion object {
        fun fromLocale(): Language {
            return when (Locale.getDefault().language) {
                "en" -> ENGLISH
                "ua" -> UKRAINIAN
                else -> ENGLISH
            }
        }
    }
}
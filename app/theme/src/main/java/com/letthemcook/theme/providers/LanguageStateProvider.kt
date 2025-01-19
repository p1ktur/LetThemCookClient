package com.letthemcook.theme.providers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.letthemcook.theme.language.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguageStateProvider(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("LanguageProvider")

    private val languageKey = intPreferencesKey("languageKey")

    fun getLanguage(): Flow<Language> {
        return context.dataStore.data.map { preferences ->
            preferences[languageKey].toTheme() ?: Language.ENGLISH
        }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.toInt()
        }
    }

    private fun Language.toInt() = when (this) {
        Language.ENGLISH -> 0
        Language.UKRAINIAN -> 1
    }

    private fun Int?.toTheme() = when (this) {
        0 -> Language.ENGLISH
        1 -> Language.UKRAINIAN
        else -> null
    }
}
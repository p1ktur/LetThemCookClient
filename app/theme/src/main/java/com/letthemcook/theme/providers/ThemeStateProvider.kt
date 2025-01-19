package com.letthemcook.theme.providers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.letthemcook.theme.base.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeStateProvider(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ThemeProvider")

    private val themeKey = intPreferencesKey("themeKey")

    fun getTheme(): Flow<Theme> {
        return context.dataStore.data.map { preferences ->
            preferences[themeKey].toTheme() ?: Theme.LIGHT
        }
    }

    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.toInt()
        }
    }

    private fun Theme.toInt() = when (this) {
        Theme.DARK -> 0
        Theme.LIGHT -> 1
    }

    private fun Int?.toTheme() = when (this) {
        0 -> Theme.DARK
        1 -> Theme.LIGHT
        else -> null
    }
}
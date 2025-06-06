package com.example.ratify.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ratify.core.model.PrimaryColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Stores user app preferences using Jetpack DataStore for persistent settings
class SettingsRepository(
    private val context: Context
) {
    private val settingsName = "app_settings"
    private val Context.dataStore by preferencesDataStore(settingsName)

    private object PreferencesKeys {
        val AUTO_SIGN_IN = booleanPreferencesKey("auto_sign_in")
        val SKIP_ON_RATE = booleanPreferencesKey("skip_on_rate")
        val QUEUE_SKIP = booleanPreferencesKey("queue_skip")
        val LIBRARY_IMAGE_URI = booleanPreferencesKey("library_image_uri")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val THEME_COLOR = intPreferencesKey("theme_color")
    }

    // Getters
    val autoSignIn: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.AUTO_SIGN_IN] ?: false }
    val skipOnRate: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.SKIP_ON_RATE] ?: false}
    val queueSkip: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.QUEUE_SKIP] ?: false}
    val libraryImageUri: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.LIBRARY_IMAGE_URI] ?: true }
    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.DARK_THEME] ?: true }
    val themeColor: Flow<Int> = context.dataStore.data
        .map { it[PreferencesKeys.THEME_COLOR] ?: PrimaryColor.DEFAULT.ordinal }

    // Setters
    suspend fun setAutoSignIn(autoSignIn: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_SIGN_IN] = autoSignIn }
    }
    suspend fun setSkipOnRate(skipOnRate: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SKIP_ON_RATE] = skipOnRate }
    }
    suspend fun setQueueSkip(queueSkip: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.QUEUE_SKIP] = queueSkip }
    }
    suspend fun setLibraryImageUri(libraryImageUri: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.LIBRARY_IMAGE_URI] = libraryImageUri }
    }
    suspend fun setDarkTheme(darkTheme: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_THEME] = darkTheme }
    }
    suspend fun setThemeColor(themeColor: Int) {
        context.dataStore.edit { it[PreferencesKeys.THEME_COLOR] = themeColor }
    }
}
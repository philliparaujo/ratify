package com.example.ratify.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ratify.core.model.PrimaryColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_NAME = "app_settings"
val Context.dataStore by preferencesDataStore(SETTINGS_NAME)

class SettingsManager(private val context: Context): ISettingsManager {
    private object PreferencesKeys {
        val AUTO_SIGN_IN = booleanPreferencesKey("auto_sign_in")
        val SKIP_ON_RATE = booleanPreferencesKey("skip_on_rate")
        val QUEUE_SKIP = booleanPreferencesKey("queue_skip")
        val LIBRARY_IMAGE_URI = booleanPreferencesKey("library_image_uri")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val THEME_COLOR = intPreferencesKey("theme_color")
    }

    override val autoSignIn: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.AUTO_SIGN_IN] ?: false }
    override val skipOnRate: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.SKIP_ON_RATE] ?: false}
    override val queueSkip: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.QUEUE_SKIP] ?: false}
    override val libraryImageUri: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.LIBRARY_IMAGE_URI] ?: true }
    override val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.DARK_THEME] ?: true }
    override val themeColor: Flow<Int> = context.dataStore.data
        .map { it[PreferencesKeys.THEME_COLOR] ?: PrimaryColor.DEFAULT.ordinal }

    override suspend fun setAutoSignIn(autoSignIn: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_SIGN_IN] = autoSignIn }
    }
    override suspend fun setSkipOnRate(skipOnRate: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SKIP_ON_RATE] = skipOnRate }
    }
    override suspend fun setQueueSkip(queueSkip: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.QUEUE_SKIP] = queueSkip }
    }
    override suspend fun setLibraryImageUri(libraryImageUri: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.LIBRARY_IMAGE_URI] = libraryImageUri }
    }
    override suspend fun setDarkTheme(darkTheme: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_THEME] = darkTheme }
    }
    override suspend fun setThemeColor(themeColor: Int) {
        context.dataStore.edit { it[PreferencesKeys.THEME_COLOR] = themeColor }
    }
}
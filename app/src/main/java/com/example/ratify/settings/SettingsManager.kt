package com.example.ratify.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_NAME = "app_settings"
val Context.dataStore by preferencesDataStore(SETTINGS_NAME)

class SettingsManager(private val context: Context) {
    private object PreferencesKeys {
        val AUTO_SIGN_IN = booleanPreferencesKey("auto_sign_in")
        val SKIP_ON_RATE = booleanPreferencesKey("skip_on_rate")
        val QUEUE_SKIP = booleanPreferencesKey("queue_skip")
    }

    val autoSignIn: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.AUTO_SIGN_IN] ?: true }
    val skipOnRate: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.SKIP_ON_RATE] ?: true}
    val queueSkip: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.QUEUE_SKIP] ?: true}

    suspend fun setAutoSignIn(autoSignIn: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_SIGN_IN] = autoSignIn }
    }
    suspend fun setSkipOnRate(skipOnRate: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SKIP_ON_RATE] = skipOnRate }
    }
    suspend fun setQueueSkip(queueSkip: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.QUEUE_SKIP] = queueSkip }
    }
}
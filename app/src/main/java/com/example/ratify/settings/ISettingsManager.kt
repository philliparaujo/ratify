package com.example.ratify.settings

import kotlinx.coroutines.flow.Flow

interface ISettingsManager {
    val autoSignIn: Flow<Boolean>
    val skipOnRate: Flow<Boolean>
    val queueSkip: Flow<Boolean>
    val libraryImageUri: Flow<Boolean>
    val darkTheme: Flow<Boolean>
    val themeColor: Flow<Int>

    suspend fun setAutoSignIn(autoSignIn: Boolean)
    suspend fun setSkipOnRate(skipOnRate: Boolean)
    suspend fun setQueueSkip(queueSkip: Boolean)
    suspend fun setLibraryImageUri(libraryImageUri: Boolean)
    suspend fun setDarkTheme(darkTheme: Boolean)
    suspend fun setThemeColor(themeColor: Int)
}
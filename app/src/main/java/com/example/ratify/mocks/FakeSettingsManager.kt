package com.example.ratify.mocks

import com.example.ratify.core.model.PrimaryColor
import com.example.ratify.settings.ISettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsManager : ISettingsManager {
    private val _autoSignIn = MutableStateFlow(false)
    private val _skipOnRate = MutableStateFlow(false)
    private val _queueSkip = MutableStateFlow(false)
    private val _libraryImageUri = MutableStateFlow(true)
    private val _darkTheme = MutableStateFlow(true)
    private val _themeColor = MutableStateFlow(PrimaryColor.DEFAULT.ordinal)

    override val autoSignIn: Flow<Boolean> = _autoSignIn
    override val skipOnRate: Flow<Boolean> = _skipOnRate
    override val queueSkip: Flow<Boolean> = _queueSkip
    override val libraryImageUri: Flow<Boolean> = _libraryImageUri
    override val darkTheme: Flow<Boolean> = _darkTheme
    override val themeColor: Flow<Int> = _themeColor

    override suspend fun setAutoSignIn(autoSignIn: Boolean) {
        _autoSignIn.value = autoSignIn
    }

    override suspend fun setSkipOnRate(skipOnRate: Boolean) {
        _skipOnRate.value = skipOnRate
    }

    override suspend fun setQueueSkip(queueSkip: Boolean) {
        _queueSkip.value = queueSkip
    }

    override suspend fun setLibraryImageUri(libraryImageUri: Boolean) {
        _libraryImageUri.value = libraryImageUri
    }

    override suspend fun setDarkTheme(darkTheme: Boolean) {
        _darkTheme.value = darkTheme
    }

    override suspend fun setThemeColor(themeColor: Int) {
        _themeColor.value = themeColor
    }
}

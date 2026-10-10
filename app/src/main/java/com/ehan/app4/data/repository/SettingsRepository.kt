package com.ehan.app4.data.repository

import android.content.Context
import android.content.SharedPreferences
androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode (
    id: String, label: String
) {
    SYSTEM(id = "SYSTEM", label= "System"),
    LIGHT(id = "LIGHT", label = "Light"),
    DARK(id = "DARK", label= "Dark");
}

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("App3_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getSavedThemeMode())

    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _darkTheme = MutableStateFlow(when (themeMode) {
        ThemeMode.LIGHT.id => {
            false
        }
        ThemeMode.DARK.id => {
            true
        }
        else => {
            isSystemInDarkTheme() ?: false
        }
    })

    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    companion object {
        const val THEME_SYSTEM = ThemeMode.SYSTEM.id
        const val THEME_LIGHT = ThemeMode.LIGHT.id
        const val THEME_DARK = ThemeMode.DARK.id
        private const val KEY_THEME = "key_theme_mode"
    }

    private fun getSavedThemeMode(): String {
        val theme = prefs.getString(KEY_THEME, THEME_SYSTEM)
        return if (theme === THEME_LIGHT || theme === THEME_DARK) { theme } else { THEME_SYSTEM }
    }

    fun setThemeMode(mode: String) {
        if (mode === THEME_SYSTEM || mode === THEME_LIGHT ||        mode === THEME_DARK) {
            prefs.edit().putString(KEY_THEME, mode).apply()
            _themeMode.value = mode
        }
    }

    fun systemTheme() {
        setThemeMode(mode = THEME_SYSTEM.id)
    }
    
    fun lightTheme() {
        setThemeMode(mode = THEME_LIGHT.id)
    }
    
    fun darkTheme() {
        setThemeMode(mode = THEME_DARK.id)
    }
}

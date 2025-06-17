package com.codeping.android_client.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class ThemeManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    var isDarkTheme by mutableStateOf(
        prefs.getBoolean("is_dark_theme", false) // Default to light theme
    )
        private set
    
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        prefs.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }
    
    fun setTheme(isDark: Boolean) {
        isDarkTheme = isDark
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }
}

@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager(context) }
}

package com.codeping.android_client.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CodePingDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    primaryContainer = EmeraldPrimaryVariantDark,
    secondary = EmeraldSecondary,
    secondaryContainer = EmeraldSecondaryVariant,
    tertiary = InfoColor,
    background = BackgroundPrimaryDark,
    surface = SurfaceColorDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    onSecondary = TextPrimaryDark,
    onSecondaryContainer = TextPrimaryDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorColor,
    onError = Color.White
)

private val CodePingLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    primaryContainer = EmeraldLight,
    secondary = EmeraldSecondary,
    secondaryContainer = EmeraldSecondaryVariant,
    tertiary = InfoColor,
    background = BackgroundPrimary,
    surface = SurfaceColor,
    surfaceVariant = SurfaceVariant,
    onPrimary = Color.White,
    onPrimaryContainer = EmeraldDark,
    onSecondary = Color.White,
    onSecondaryContainer = EmeraldDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    onError = Color.White,
    outline = TextTertiary,
    outlineVariant = Color(0xFFE5E7EB)
)

@Composable
fun CodePingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to maintain brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CodePingDarkColorScheme
        else -> CodePingLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CodePingTypography,
        content = content
    )
}

// Legacy theme name for compatibility
@Composable
fun CodePingAndroidClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CodePingTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
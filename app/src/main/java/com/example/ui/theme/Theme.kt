package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = EmeraldLight,
    secondary = AppleAccentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF102A43),
    onSecondaryContainer = Color(0xFFBEE3F8),
    tertiary = AppleAccentGold,
    onTertiary = Color.Black,
    background = PitchDarkBg,
    onBackground = TextPrimaryDark,
    surface = PitchDarkCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = TextSecondaryDark,
    outline = PitchBorderGlass,
    error = AppleAccentRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00897B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = AppleAccentBlue,
    onSecondary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = AppleAccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Football apps look best in high-contrast dark pitch glass mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = NeonBlueAccent,
    background = CyberDarkBg,
    surface = CyberDarkSurface,
    onPrimary = CyberDarkBg, // Contrast against primary cyan
    onSecondary = CyberDarkBg,
    onTertiary = CyberDarkBg,
    onBackground = TextLightMain,
    onSurface = TextLightMain,
    outline = CyberDarkBorder,
    surfaceVariant = CyberDarkSurface,
    onSurfaceVariant = TextLightSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = SecondaryBlue,
    secondary = PrimaryBlue,
    tertiary = NeonBlueAccent,
    background = MinimalLightBg,
    surface = MinimalLightSurface,
    onPrimary = MinimalLightSurface, // Contrast against deep blue
    onSecondary = MinimalLightSurface,
    onTertiary = MinimalLightSurface,
    onBackground = TextDarkMain,
    onSurface = TextDarkMain,
    outline = MinimalLightBorder,
    surfaceVariant = MinimalLightSurface,
    onSurfaceVariant = TextDarkSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

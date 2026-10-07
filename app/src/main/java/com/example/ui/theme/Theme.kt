package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = BrandPrimaryLight,
    secondary = BrandSecondary,
    onSecondary = Color.Black,
    secondaryContainer = BrandSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = BrandSecondaryLight,
    tertiary = FlameAccent,
    onTertiary = Color.White,
    tertiaryContainer = FlameAccent.copy(alpha = 0.2f),
    onTertiaryContainer = FlameAccentLight,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = RoseDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = BrandPrimary,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = BrandSecondary.copy(alpha = 0.12f),
    onSecondaryContainer = BrandSecondary,
    tertiary = FlameAccent,
    onTertiary = Color.White,
    tertiaryContainer = FlameAccent.copy(alpha = 0.12f),
    onTertiaryContainer = FlameAccent,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = RoseDanger,
    onError = Color.White
)

@Composable
fun StudyFlowTheme(
    darkTheme: Boolean = true, // Default to deep stylish dark mode for students
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

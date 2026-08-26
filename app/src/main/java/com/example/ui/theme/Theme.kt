package com.example.ui.theme

import android.app.Activity
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
    primary = DevPilotCyan,
    onPrimary = Color(0xFF090D16),
    primaryContainer = Color(0xFF0C3B4E),
    onPrimaryContainer = DevPilotCyanLight,
    secondary = DevPilotIndigo,
    onSecondary = Color(0xFF090D16),
    secondaryContainer = Color(0xFF282A5A),
    onSecondaryContainer = DevPilotIndigoLight,
    tertiary = DevPilotSuccess,
    onTertiary = Color(0xFF090D16),
    background = NeutralBgDark,
    onBackground = TextPrimaryDark,
    surface = NeutralSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = NeutralCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = NeutralBorderDark,
    outlineVariant = NeutralBorderSubtleDark,
    error = DevPilotDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DevPilotCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF083344),
    secondary = DevPilotIndigoDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF312E81),
    tertiary = DevPilotSuccess,
    onTertiary = Color.White,
    background = NeutralBgLight,
    onBackground = TextPrimaryLight,
    surface = NeutralSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = NeutralCardLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = NeutralBorderLight,
    outlineVariant = NeutralBorderSubtleLight,
    error = DevPilotDanger,
    onError = Color.White
)

@Composable
fun DevPilotTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

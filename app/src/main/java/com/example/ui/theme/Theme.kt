package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class MohaThemeStyle(val displayName: String, val description: String) {
    SIGNATURE("Moha Signature", "Electric Indigo & Velvet Obsidian"),
    SUNSET_GOLD("Moha Sunset Gold", "Warm Amber & Sunset Twilight"),
    CYBER_CYAN("Moha Cyber Cyan", "Neon Aqua & Deep Matrix Graphite")
}

fun getMohaDarkColorScheme(style: MohaThemeStyle): ColorScheme {
    val primaryColor = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaDarkPrimary
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldDarkPrimary
        MohaThemeStyle.CYBER_CYAN -> MohaCyanDarkPrimary
    }
    val primaryContainer = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaDarkPrimaryContainer
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldDarkPrimaryContainer
        MohaThemeStyle.CYBER_CYAN -> MohaCyanDarkPrimaryContainer
    }
    val onPrimaryContainer = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaDarkOnPrimaryContainer
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldDarkOnPrimaryContainer
        MohaThemeStyle.CYBER_CYAN -> MohaCyanDarkOnPrimaryContainer
    }

    return darkColorScheme(
        primary = primaryColor,
        onPrimary = MohaDarkOnPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = MohaDarkSecondary,
        tertiary = MohaDarkTertiary,
        background = MohaDarkBackground,
        surface = MohaDarkSurface,
        surfaceVariant = MohaDarkSurfaceVariant,
        onBackground = MohaDarkTextPrimary,
        onSurface = MohaDarkTextPrimary,
        onSurfaceVariant = MohaDarkTextSecondary,
        outline = MohaDarkBorder,
        outlineVariant = MohaDarkBorderSubtle
    )
}

fun getMohaLightColorScheme(style: MohaThemeStyle): ColorScheme {
    val primaryColor = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaLightPrimary
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldLightPrimary
        MohaThemeStyle.CYBER_CYAN -> MohaCyanLightPrimary
    }
    val primaryContainer = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaLightPrimaryContainer
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldLightPrimaryContainer
        MohaThemeStyle.CYBER_CYAN -> MohaCyanLightPrimaryContainer
    }
    val onPrimaryContainer = when (style) {
        MohaThemeStyle.SIGNATURE -> MohaLightOnPrimaryContainer
        MohaThemeStyle.SUNSET_GOLD -> MohaGoldLightOnPrimaryContainer
        MohaThemeStyle.CYBER_CYAN -> MohaCyanLightOnPrimaryContainer
    }

    return lightColorScheme(
        primary = primaryColor,
        onPrimary = MohaLightOnPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = MohaLightSecondary,
        tertiary = MohaLightTertiary,
        background = MohaLightBackground,
        surface = MohaLightSurface,
        surfaceVariant = MohaLightSurfaceVariant,
        onBackground = MohaLightTextPrimary,
        onSurface = MohaLightTextPrimary,
        onSurfaceVariant = MohaLightTextSecondary,
        outline = MohaLightBorder,
        outlineVariant = MohaLightBorderSubtle
    )
}

@Composable
fun VideoDownloaderTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    themeStyle: MohaThemeStyle = MohaThemeStyle.SIGNATURE,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) {
        getMohaDarkColorScheme(themeStyle)
    } else {
        getMohaLightColorScheme(themeStyle)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
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


package com.aarevalo.tasky.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppTypography
import com.example.ui.theme.LocalExtendedTypography

@Immutable
data class ExtendedColors(
    val colorSchema: ColorScheme,
    val secondary80: Color,
    val tertiary: Color,
    val tertiary80: Color,
    val supplementary: Color,
    val success: Color,
    val link: Color,
    val background50: Color,
    val surfaceHigher: Color,
    val surface60: Color,
    val onSurfaceVariant70: Color,
)

private val darkScheme = ExtendedColors(
    darkColorScheme(
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        onSurfaceVariant = onSurfaceVariantDark,
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        outline = outlineDark,
        error = errorDark,
        secondary = secondary,
    ),
    secondary80 = secondary80,
    tertiary = tertiary,
    tertiary80 = tertiary80,
    supplementary = supplementary,
    success = successDark,
    link = linkDark,
    background50 = background50Dark,
    surfaceHigher = surfaceHigherDark,
    surface60 = surface60Dark,
    onSurfaceVariant70 = onSurfaceVariant70Dark,
)

private val lightScheme = ExtendedColors(
    lightColorScheme(
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        onSurfaceVariant = onSurfaceVariantLight,
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        secondary = secondary,
        outline = outlineLight,
        error = errorLight,
    ),
    secondary80 = secondary80,
    tertiary = tertiary,
    tertiary80 = tertiary80,
    supplementary = supplementary,
    success = successLight,
    link = linkLight,
    background50 = background50Light,
    surfaceHigher = surfaceHigherLight,
    surface60 = surface60Light,
    onSurfaceVariant70 = onSurfaceVariant70Light,
)

val LocalExtendedColors = compositionLocalOf { lightScheme }

@Composable
fun TaskyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) darkScheme else lightScheme

    CompositionLocalProvider(
        LocalSpacing provides Dimensions(),
        LocalExtendedColors provides extendedColors,
        LocalExtendedTypography provides AppTypography
    ) {
        MaterialTheme(colorScheme = extendedColors.colorSchema, typography = AppTypography.base, content = content)
    }
}
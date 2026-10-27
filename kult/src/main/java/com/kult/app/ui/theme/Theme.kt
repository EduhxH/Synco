package com.kult.app.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSyncoColors = staticCompositionLocalOf { LightSyncoColors }

@Composable
fun SyncoTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkSyncoColors else LightSyncoColors
    // Material components (menus, pickers) pick their colors from here.
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            background = colors.background,
            onBackground = colors.ink,
            surface = colors.card,
            onSurface = colors.ink,
            surfaceContainer = colors.card,
            onSurfaceVariant = colors.secondary,
            outline = colors.border,
        )
    } else {
        lightColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            background = colors.background,
            onBackground = colors.ink,
            surface = colors.card,
            onSurface = colors.ink,
            surfaceContainer = colors.card,
            onSurfaceVariant = colors.secondary,
            outline = colors.border,
        )
    }
    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalSyncoColors provides colors,
            LocalContentColor provides colors.ink,
            content = content,
        )
    }
}

object SyncoTheme {
    val colors: SyncoColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSyncoColors.current
}

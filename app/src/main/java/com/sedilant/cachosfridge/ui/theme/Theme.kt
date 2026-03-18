package com.sedilant.cachosfridge.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Single dark colour scheme – dynamic colours disabled ──────────────────────
private val AppColorScheme = darkColorScheme(
    primary                = GreenPrimary,
    onPrimary              = BackgroundBlack,
    primaryContainer       = GreenDark,
    onPrimaryContainer     = GreenOnDark,

    secondary              = GreenDark,
    onSecondary            = GreenOnDark,
    secondaryContainer     = Color(0xFF1E3A12),
    onSecondaryContainer   = GreenOnDark,

    tertiary               = GreenPrimary,
    onTertiary             = BackgroundBlack,

    background             = BackgroundBlack,
    onBackground           = OnBackground,

    surface                = SurfaceDark,
    onSurface              = OnSurface,
    surfaceVariant         = SurfaceVariant,
    onSurfaceVariant       = OutlineColor,
    surfaceContainer       = SurfaceContainer,
    surfaceContainerLow    = SurfaceDark,
    surfaceContainerHigh   = SurfaceVariant,

    outline                = OutlineColor,

    error                  = ErrorRed,
    onError                = OnErrorRed,
)

@Composable
fun CachosFridgeTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}
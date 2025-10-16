package com.escom.gestordearchivos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Enumeración de temas disponibles
 */
enum class AppTheme {
    GUINDA_IPN,
    AZUL_ESCOM
}

// Esquema de colores para tema Guinda IPN (Modo claro)
private val GuindaLightColorScheme = lightColorScheme(
    primary = GuindaIPN,
    onPrimary = Color.White,
    primaryContainer = GuindaIPNLight,
    onPrimaryContainer = Color.White,
    secondary = GuindaIPNDark,
    onSecondary = Color.White,
    tertiary = GrisMedio,
    background = BackgroundLight,
    onBackground = GrisOscuro,
    surface = SurfaceLight,
    onSurface = GrisOscuro,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisMedio,
    error = ErrorRed,
    onError = Color.White
)

// Esquema de colores para tema Guinda IPN (Modo oscuro)
private val GuindaDarkColorScheme = darkColorScheme(
    primary = GuindaIPNLight,
    onPrimary = Color.White,
    primaryContainer = GuindaIPN,
    onPrimaryContainer = Color.White,
    secondary = GuindaIPNDark,
    onSecondary = Color.White,
    tertiary = GrisMedio,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = GrisClaro,
    error = ErrorRed,
    onError = Color.White
)

// Esquema de colores para tema Azul ESCOM (Modo claro)
private val AzulLightColorScheme = lightColorScheme(
    primary = AzulESCOM,
    onPrimary = Color.White,
    primaryContainer = AzulESCOMLight,
    onPrimaryContainer = Color.White,
    secondary = AzulESCOMDark,
    onSecondary = Color.White,
    tertiary = GrisMedio,
    background = BackgroundLight,
    onBackground = GrisOscuro,
    surface = SurfaceLight,
    onSurface = GrisOscuro,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisMedio,
    error = ErrorRed,
    onError = Color.White
)

// Esquema de colores para tema Azul ESCOM (Modo oscuro)
private val AzulDarkColorScheme = darkColorScheme(
    primary = AzulESCOMLight,
    onPrimary = Color.White,
    primaryContainer = AzulESCOM,
    onPrimaryContainer = Color.White,
    secondary = AzulESCOMDark,
    onSecondary = Color.White,
    tertiary = GrisMedio,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = GrisClaro,
    error = ErrorRed,
    onError = Color.White
)

/**
 * Tema principal de la aplicación
 * Soporta dos temas personalizados (IPN y ESCOM) con modo claro/oscuro
 */
@Composable
fun FileManagerTheme(
    selectedTheme: AppTheme = AppTheme.GUINDA_IPN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Deshabilitado por defecto para usar temas personalizados
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        selectedTheme == AppTheme.GUINDA_IPN -> {
            if (darkTheme) GuindaDarkColorScheme else GuindaLightColorScheme
        }
        selectedTheme == AppTheme.AZUL_ESCOM -> {
            if (darkTheme) AzulDarkColorScheme else AzulLightColorScheme
        }
        else -> {
            if (darkTheme) GuindaDarkColorScheme else GuindaLightColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

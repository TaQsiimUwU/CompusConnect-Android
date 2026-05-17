@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.theme

import android.os.Build
import androidx.compose.animation.core.animate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Theme (Extrapolated for contrast, inverting your dark palette)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E54C2), // Deepened from #8EBBFF for light mode contrast
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF00194A),
    secondary = Color(0xFF5B5D72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E2F9),
    onSecondaryContainer = Color(0xFF181A2C),
    tertiary = Color(0xFF76546F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD7F2),
    onTertiaryContainer = Color(0xFF2D1128),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF4F5FC), // Your provided Text color
    onBackground = Color(0xFF24293E), // Your provided BG color
    surface = Color(0xFFF4F5FC),
    onSurface = Color(0xFF24293E),
    surfaceVariant = Color(0xFFE2E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF757780),
    outlineVariant = Color(0xFFC5C6D0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF303036),
    inverseOnSurface = Color(0xFFF2F0F4),
    inversePrimary = Color(0xFF8EBBFF), // Your Accent
    surfaceTint = Color(0xFF1E54C2),
)

// Dark Theme (Directly applying your provided image colors)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8EBBFF), // Your Accent
    onPrimary = Color(0xFF002A78), // Dark contrast for text on primary
    primaryContainer = Color(0xFF003EA2),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = Color(0xFFCCCCCC), // Your Secondary
    onSecondary = Color(0xFF333333),
    secondaryContainer = Color(0xFF414659),
    onSecondaryContainer = Color(0xFFDDE1F9),
    tertiary = Color(0xFFE1BAD6),
    onTertiary = Color(0xFF43263E),
    tertiaryContainer = Color(0xFF5B3B55),
    onTertiaryContainer = Color(0xFFFFD7F2),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF24293E), // Your BG
    onBackground = Color(0xFFF4F5FC), // Your Text
    surface = Color(0xFF24293E), // Matching BG
    onSurface = Color(0xFFF4F5FC), // Matching Text
    surfaceVariant = Color(0xFF434754),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D919B),
    outlineVariant = Color(0xFF434754),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE3E2E9),
    inverseOnSurface = Color(0xFF2F3036),
    inversePrimary = Color(0xFF1E54C2),
    surfaceTint = Color(0xFF8EBBFF),
)

@Composable
fun CompusConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}

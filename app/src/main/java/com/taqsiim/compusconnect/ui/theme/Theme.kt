package com.taqsiim.compusconnect.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define your roles
enum class UserRole {
    Student,
    ClubManager
}

// --- Student Schemes ---
// Note: "Expressive" colors are just standard ColorSchemes used inside an Expressive Theme.
private val StudentDarkScheme = darkColorScheme(
    primary = StudentDarkPrimary,
    onPrimary = StudentDarkBackground,
    secondary = StudentDarkSecondary,
    onSecondary = StudentDarkBackground,
    tertiary = StudentDarkAccent,
    background = StudentDarkBackground,
    onBackground = StudentDarkText,
    surface = StudentDarkBackground,
    surfaceVariant = StudentDarkCard,
    onSurface = StudentDarkText
)

private val StudentLightScheme = lightColorScheme(
    primary = StudentLightPrimary,
    onPrimary = StudentLightBackground,
    secondary = StudentLightSecondary,
    onSecondary = StudentLightBackground,
    tertiary = StudentLightAccent,
    background = StudentLightBackground,
    onBackground = StudentLightText,
    surface = StudentLightBackground,
    onSurface = StudentLightText
)

// --- Manager Schemes ---
private val ClubManagerDarkScheme = darkColorScheme(
    primary = ClubManagerDarkPrimary,
    onPrimary = ClubManagerDarkBackground,
    secondary = ClubManagerDarkSecondary,
    onSecondary = ClubManagerDarkText,
    tertiary = ClubManagerDarkAccent,
    background = ClubManagerDarkBackground,
    onBackground = ClubManagerDarkText,
    surface = ClubManagerDarkBackground,
    surfaceVariant = ClubManagerDarkCard,
    onSurface = ClubManagerDarkText
)

private val ClubManagerLightScheme = lightColorScheme(
    primary = ClubManagerLightPrimary,
    onPrimary = ClubManagerLightBackground,
    secondary = ClubManagerLightSecondary,
    onSecondary = ClubManagerLightBackground,
    tertiary = ClubManagerLightAccent,
    background = ClubManagerLightBackground,
    onBackground = ClubManagerLightText,
    surface = ClubManagerLightBackground,
    onSurface = ClubManagerLightText
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CampusAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    userRole: UserRole = UserRole.Student,
    useExpressive: Boolean = true, // Enable Expressive by default
    content: @Composable () -> Unit
) {
    // 1. Determine the correct Color Scheme based on Role and Dark Mode
    val colorScheme = when (userRole) {
        UserRole.Student -> if (darkTheme) StudentDarkScheme else StudentLightScheme
        UserRole.ClubManager -> if (darkTheme) ClubManagerDarkScheme else ClubManagerLightScheme
    }

    // 2. Set Status Bar Colors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 3. Apply the Theme (Expressive or Standard)
    if (useExpressive) {
        // Expressive Theme (New shapes, motion, and typography handling)
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = Typography, // Ensure this object exists in your Type.kt
            content = content
        )
    } else {
        // Fallback to Standard Material Theme
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
package com.taqsiim.compusconnect.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define your roles
enum class UserRole {
    Student,
    ClubManager
}

// --- Student Schemes ---
private val StudentDarkColorScheme = darkColorScheme(
    primary = StudentDarkPrimary,
    onPrimary = StudentDarkBackground, // High contrast
    secondary = StudentDarkSecondary,
    onSecondary = StudentDarkBackground,
    tertiary = StudentDarkAccent,
    background = StudentDarkBackground,
    onBackground = StudentDarkText,
    surface = StudentDarkBackground,
    onSurface = StudentDarkText
)

private val StudentLightColorScheme = lightColorScheme(
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
private val ClubManagerDarkColorScheme = darkColorScheme(
    primary = ClubManagerDarkPrimary,
    onPrimary = ClubManagerDarkBackground,
    secondary = ClubManagerDarkSecondary,
    onSecondary = ClubManagerDarkText,
    tertiary = ClubManagerDarkAccent,
    background = ClubManagerDarkBackground,
    onBackground = ClubManagerDarkText,
    surface = ClubManagerDarkBackground,
    onSurface = ClubManagerDarkText
)

private val ClubManagerLightColorScheme = lightColorScheme(
    primary = ClubManagerLightPrimary,
    onPrimary = ClubManagerLightBackground, // White text might be better here depending on accessibility
    secondary = ClubManagerLightSecondary,
    onSecondary = ClubManagerLightBackground,
    tertiary = ClubManagerLightAccent,
    background = ClubManagerLightBackground,
    onBackground = ClubManagerLightText,
    surface = ClubManagerLightBackground,
    onSurface = ClubManagerLightText
)

@Composable
fun CampusAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    userRole: UserRole = UserRole.Student, // Default to Student
    dynamicColor: Boolean = false, // Set to false to force your custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Select scheme based on Role and Mode
        userRole == UserRole.Student && darkTheme -> StudentDarkColorScheme
        userRole == UserRole.Student && !darkTheme -> StudentLightColorScheme
        userRole == UserRole.ClubManager && darkTheme -> ClubManagerDarkColorScheme
        else -> ClubManagerLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match status bar to bg
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
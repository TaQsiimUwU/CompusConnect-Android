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
import com.taqsiim.compusconnect.data.model.UserRole

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
    surfaceVariant = StudentLightCard,
    onSurface = StudentLightText
)

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
    surfaceVariant = ClubManagerLightCard,
    surface = ClubManagerLightBackground,
    onSurface = ClubManagerLightText
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CampusAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    userRole: UserRole? = null,
    useExpressive: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (userRole) {
        UserRole.STUDENT -> if (darkTheme) StudentDarkScheme else StudentLightScheme
        UserRole.CLUB_MANAGER -> if (darkTheme) ClubManagerDarkScheme else ClubManagerLightScheme
        UserRole.STUDENT_MANAGER -> if (darkTheme) StudentDarkScheme else StudentLightScheme
        null -> if (darkTheme) StudentDarkScheme else StudentLightScheme
    }

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
package com.taqsiim.compusconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.taqsiim.compusconnect.R

// 1. Configure the Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// 2. Define the Roboto Flex Font Family
val RobotoFlex = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.Normal // Default weight
    ),
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.Medium // Medium weight for Expressive look
    ),
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.SemiBold // SemiBold for headers
    ),
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.Bold // Bold for display text
    )
)

// 3. Apply it to your Typography
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = RobotoFlex, // <--- Use the variable here
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    // Add other styles as needed...
)
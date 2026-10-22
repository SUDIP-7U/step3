package com.example.step3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Semantic, theme-aware colors used by every screen. */
@Immutable
data class TridoshaColors(
    val cream: Color,
    val cardBackground: Color,
    val ink: Color,
    val muted: Color,
    val sage: Color,
    val sageSoft: Color,
    val deepSage: Color,
    val onDeepSage: Color,
    val outline: Color,
    val vata: Color,
    val vataSoft: Color,
    val pitta: Color,
    val pittaSoft: Color,
    val kapha: Color,
    val kaphaSoft: Color,
    val isDark: Boolean,
)

val LightTridoshaColors = TridoshaColors(
    cream = LightCream,
    cardBackground = LightCardBackground,
    ink = LightInk,
    muted = LightMuted,
    sage = LightSage,
    sageSoft = LightSageSoft,
    deepSage = LightDeepSage,
    onDeepSage = LightOnDeepSage,
    outline = LightOutline,
    vata = LightVata,
    vataSoft = LightVataSoft,
    pitta = LightPitta,
    pittaSoft = LightPittaSoft,
    kapha = LightKapha,
    kaphaSoft = LightKaphaSoft,
    isDark = false,
)

val DarkTridoshaColors = TridoshaColors(
    cream = DarkCream,
    cardBackground = DarkCardBackground,
    ink = DarkInk,
    muted = DarkMuted,
    sage = DarkSage,
    sageSoft = DarkSageSoft,
    deepSage = DarkDeepSage,
    onDeepSage = DarkOnDeepSage,
    outline = DarkOutline,
    vata = DarkVata,
    vataSoft = DarkVataSoft,
    pitta = DarkPitta,
    pittaSoft = DarkPittaSoft,
    kapha = DarkKapha,
    kaphaSoft = DarkKaphaSoft,
    isDark = true,
)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = LightDeepSage,
    onPrimary = LightOnDeepSage,
    primaryContainer = LightSageSoft,
    onPrimaryContainer = LightInk,
    secondary = LightSage,
    onSecondary = LightInk,
    secondaryContainer = LightSageSoft,
    onSecondaryContainer = LightInk,
    tertiary = LightPitta,
    onTertiary = LightOnDeepSage,
    background = LightCream,
    onBackground = LightInk,
    surface = LightCardBackground,
    onSurface = LightInk,
    surfaceVariant = LightSageSoft,
    onSurfaceVariant = LightMuted,
    outline = LightOutline,
    outlineVariant = LightOutline,
    error = LightError,
    onError = LightOnError,
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DarkDeepSage,
    onPrimary = DarkOnDeepSage,
    primaryContainer = DarkSageSoft,
    onPrimaryContainer = DarkInk,
    secondary = DarkSage,
    onSecondary = DarkInk,
    secondaryContainer = DarkSageSoft,
    onSecondaryContainer = DarkInk,
    tertiary = DarkPitta,
    onTertiary = DarkOnDeepSage,
    background = DarkCream,
    onBackground = DarkInk,
    surface = DarkCardBackground,
    onSurface = DarkInk,
    surfaceVariant = DarkSageSoft,
    onSurfaceVariant = DarkMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DarkError,
    onError = DarkOnError,
)

val TridoshaTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 34.sp,
        lineHeight = 41.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 35.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 31.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 27.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

private val LocalTridoshaColors = staticCompositionLocalOf { LightTridoshaColors }

/** Entry point for reading theme-aware colors: `AppTheme.colors.cream`, `.ink`, `.sage`, ... */
object AppTheme {
    val colors: TridoshaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTridoshaColors.current
}

@Composable
fun TridoshaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val tridoshaColors = if (darkTheme) DarkTridoshaColors else LightTridoshaColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalTridoshaColors provides tridoshaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TridoshaTypography,
            content = content,
        )
    }
}

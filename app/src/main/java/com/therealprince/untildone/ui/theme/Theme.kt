package com.therealprince.untildone.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// --- Extended color roles for the app ---
@Immutable
data class UntilDoneColors(
    // Surfaces
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val elevatedSurface: Color,

    // Content
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    // Borders
    val border: Color,
    val borderSubtle: Color,

    // Accent
    val emerald: Color,
    val emeraldDark: Color,
    val emeraldLight: Color,
    val emeraldSurface: Color,
    val emeraldOnSurface: Color,
    val emeraldBorder: Color,

    // Interactive
    val buttonPrimary: Color,
    val buttonPrimaryContent: Color,
    val buttonSecondary: Color,
    val buttonSecondaryContent: Color,

    // Tags / chips
    val tagBackground: Color,
    val tagText: Color,

    // Progress
    val progressTrack: Color,
    val progressFill: Color,
    val progressComplete: Color,

    // Pill / Nav
    val navActiveBackground: Color,
    val navActiveContent: Color,
    val navInactiveContent: Color,

    // Profile
    val profileBackground: Color,
    val profileContent: Color,

    // Destructive
    val destructive: Color,
    val destructiveSurface: Color,

    // Focus card gradient
    val focusCardStart: Color,
    val focusCardEnd: Color,

    // Inverted (for analytics hero)
    val invertedBackground: Color,
    val invertedContent: Color,
    val invertedSecondary: Color,

    // Input
    val inputBackground: Color,

    // Shadow overlay
    val overlayBackground: Color,

    // Stepper buttons
    val stepperBackground: Color,
    val stepperBorder: Color,

    // Bottom nav
    val bottomNavBackground: Color,
    val bottomNavBorder: Color,

    // Dropdown
    val dropdownBackground: Color,
    val dropdownBorder: Color,

    // Chart bar
    val chartBarBackground: Color,
    val chartBarFill: Color,

    // Week streak
    val streakActive: Color,
    val streakActiveContent: Color,
    val streakInactive: Color,
    val streakInactiveContent: Color,
)

val LightUntilDoneColors = UntilDoneColors(
    background = Neutral50,
    surface = Color.White,
    cardBackground = Color.White,
    elevatedSurface = Color.White,

    textPrimary = Neutral900,
    textSecondary = Neutral600,
    textTertiary = Neutral500,

    border = Neutral200,
    borderSubtle = Neutral100,

    emerald = Emerald600,
    emeraldDark = Emerald700,
    emeraldLight = Emerald500,
    emeraldSurface = Emerald50,
    emeraldOnSurface = Emerald900,
    emeraldBorder = Emerald100,

    buttonPrimary = Neutral900,
    buttonPrimaryContent = Color.White,
    buttonSecondary = Color.White,
    buttonSecondaryContent = Neutral900,

    tagBackground = Neutral100,
    tagText = Neutral600,

    progressTrack = Neutral100,
    progressFill = Neutral900,
    progressComplete = Emerald500,

    navActiveBackground = Neutral100,
    navActiveContent = Neutral900,
    navInactiveContent = Neutral400,

    profileBackground = Neutral800,
    profileContent = Color.White,

    destructive = Red500.copy(alpha = 0.8f),
    destructiveSurface = Color(0xFFFEF2F2), // red-50

    focusCardStart = Neutral800,
    focusCardEnd = Neutral950,

    invertedBackground = Neutral900,
    invertedContent = Color.White,
    invertedSecondary = Neutral400,

    inputBackground = Neutral100,

    overlayBackground = Color.Black.copy(alpha = 0.6f),

    stepperBackground = Color.White,
    stepperBorder = Neutral100,

    bottomNavBackground = Color.White,
    bottomNavBorder = Neutral200,

    dropdownBackground = Color.White,
    dropdownBorder = Neutral100,

    chartBarBackground = Neutral100,
    chartBarFill = Neutral900,

    streakActive = Neutral900,
    streakActiveContent = Color.White,
    streakInactive = Neutral100,
    streakInactiveContent = Neutral400,
)

val DarkUntilDoneColors = UntilDoneColors(
    background = DarkSurface,
    surface = DarkCard,
    cardBackground = DarkCard,
    elevatedSurface = DarkElevated,

    textPrimary = Color.White,
    textSecondary = Neutral300,
    textTertiary = Neutral500,

    border = Neutral800,
    borderSubtle = Color(0xFF1E1E21),

    emerald = Emerald600,
    emeraldDark = Emerald500,
    emeraldLight = Emerald500,
    emeraldSurface = Color(0x1A10B981), // emerald-900/10
    emeraldOnSurface = Emerald100,
    emeraldBorder = Color(0x4D064E3B), // emerald-900/30

    buttonPrimary = Color.White,
    buttonPrimaryContent = Neutral900,
    buttonSecondary = DarkElevated,
    buttonSecondaryContent = Color.White,

    tagBackground = Color(0xCC262626), // neutral-800/80
    tagText = Neutral400,

    progressTrack = Neutral800,
    progressFill = Color.White,
    progressComplete = Emerald500,

    navActiveBackground = Color(0xCC262626),
    navActiveContent = Color.White,
    navInactiveContent = Neutral400,

    profileBackground = Neutral200,
    profileContent = Neutral900,

    destructive = Red500,
    destructiveSurface = Color(0x26EF4444), // red/15 — visible on dark bg

    focusCardStart = DarkElevated,
    focusCardEnd = Color(0xFF0F0F11),

    invertedBackground = Color.White,
    invertedContent = Neutral900,
    invertedSecondary = Neutral500,

    inputBackground = DarkSurface,

    overlayBackground = Color.Black.copy(alpha = 0.6f),

    stepperBackground = DarkElevated,
    stepperBorder = Neutral800,

    bottomNavBackground = DarkCard,
    bottomNavBorder = Color(0x99262626),

    dropdownBackground = DarkElevated,
    dropdownBorder = Neutral800,

    chartBarBackground = Neutral800,
    chartBarFill = Neutral500,

    streakActive = Color.White,
    streakActiveContent = Neutral900,
    streakInactive = Neutral800,
    streakInactiveContent = Neutral500,
)

val LocalUntilDoneColors = staticCompositionLocalOf { LightUntilDoneColors }

private val M3DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Neutral900,
    surface = DarkSurface,
    onSurface = Color.White,
    background = DarkSurface,
    onBackground = Color.White,
    surfaceVariant = DarkCard,
    outline = Neutral800,
)

private val M3LightColorScheme = lightColorScheme(
    primary = Neutral900,
    onPrimary = Color.White,
    surface = Neutral50,
    onSurface = Neutral900,
    background = Neutral50,
    onBackground = Neutral900,
    surfaceVariant = Color.White,
    outline = Neutral200,
)

@Composable
fun UntilDoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) M3DarkColorScheme else M3LightColorScheme
    val untilDoneColors = if (darkTheme) DarkUntilDoneColors else LightUntilDoneColors

    CompositionLocalProvider(LocalUntilDoneColors provides untilDoneColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extension for easy access
object UntilDoneTheme {
    val colors: UntilDoneColors
        @Composable
        get() = LocalUntilDoneColors.current
}
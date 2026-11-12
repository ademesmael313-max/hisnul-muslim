package com.hisnulmuslim.amharic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = Color(0xFF00281C),
    secondary = Color(0xFF7A5C13),
    onSecondary = Color.White,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = Color(0xFF261A00),
    tertiary = Gold,
    onTertiary = Color(0xFF261A00),
    background = Sand,
    onBackground = Ink,
    surface = SandSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE9E2D2),
    onSurfaceVariant = InkSoft,
    outline = Color(0xFF8C948D),
    outlineVariant = Color(0xFFD5CDBC),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Mint,
    onPrimary = MintDeep,
    primaryContainer = Color(0xFF0C4B3A),
    onPrimaryContainer = Color(0xFFB9EED8),
    secondary = Color(0xFFE8C87A),
    onSecondary = Color(0xFF3A2E00),
    secondaryContainer = Color(0xFF544419),
    onSecondaryContainer = Color(0xFFFFE9B0),
    tertiary = Gold,
    onTertiary = Color(0xFF261A00),
    background = NightBackground,
    onBackground = Cream,
    surface = NightSurface,
    onSurface = Cream,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = CreamSoft,
    outline = Color(0xFF7E8C84),
    outlineVariant = Color(0xFF32413A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun HisnulMuslimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}

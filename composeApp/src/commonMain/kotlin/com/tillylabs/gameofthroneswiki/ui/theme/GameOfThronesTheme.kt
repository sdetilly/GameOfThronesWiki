package com.tillylabs.gameofthroneswiki.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameOfThronesColors =
    darkColorScheme(
        primary = Color(0xFFD4AF37),
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = Color(0xFF8B7355),
        onPrimaryContainer = Color(0xFFF5E6D3),
        secondary = Color(0xFF8B0000),
        onSecondary = Color(0xFFF5F5F5),
        secondaryContainer = Color(0xFF5D0000),
        onSecondaryContainer = Color(0xFFFFCDD2),
        tertiary = Color(0xFF483C32),
        onTertiary = Color(0xFFF5F5F5),
        tertiaryContainer = Color(0xFF5D4037),
        onTertiaryContainer = Color(0xFFD7CCC8),
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE8E3D3),
        surface = Color(0xFF161B22),
        onSurface = Color(0xFFE8E3D3),
        surfaceVariant = Color(0xFF21262D),
        onSurfaceVariant = Color(0xFFD0C7B8),
        surfaceTint = Color(0xFFD4AF37),
        inverseSurface = Color(0xFFE8E3D3),
        inverseOnSurface = Color(0xFF161B22),
        error = Color(0xFFCF6679),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFFB3261E),
        onErrorContainer = Color(0xFFF9DEDC),
        outline = Color(0xFF938F99),
        outlineVariant = Color(0xFF49454F),
    )

@Composable
fun GameOfThronesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameOfThronesColors,
        content = content,
    )
}

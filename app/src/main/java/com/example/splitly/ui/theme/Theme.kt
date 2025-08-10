package com.example.splitly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    background = md_theme_dark_background,
    tertiary = md_theme_dark_tertiary
)

@Composable
fun SplitlyTheme(content: @Composable () -> Unit) {
    // forcely use dark scheme
    MaterialTheme(
        colorScheme = DarkColors,
        typography = SplitlyTypography,
        shapes = Shapes(),
        content = content
    )
}
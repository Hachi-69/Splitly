/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable

/**
 * Defines the dark color scheme for the application.
 * This color scheme is used when the app is in dark mode.
 */
private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    background = md_theme_dark_background,
    tertiary = md_theme_dark_tertiary
)

/**
 * Custom Material Theme for the Splitly application.
 *
 * This theme enforces a dark color scheme and applies custom typography and shapes.
 *
 * @param content The composable content to be themed.
 */
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
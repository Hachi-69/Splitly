/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.example.splitly.ExpenseViewModel
import com.example.splitly.Screen
import com.example.splitly.ui.screens.HomeScreen
import com.example.splitly.ui.screens.InputScreen
import com.example.splitly.ui.screens.ResultScreen

/**
 * The main composable function for the ExpenseApp.
 * It uses AnimatedContent to handle transitions between different screens (Home, Input, Result).
 * The animation direction (slide in/out from left/right) is determined by the navigation flow.
 *
 * @param vm The [ExpenseViewModel] that holds the application's state and logic.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpenseApp(vm: ExpenseViewModel) {
    AnimatedContent(
        targetState = vm.screen,
        transitionSpec = {
            val goingForward = when {
                initialState is Screen.Home && targetState is Screen.Input -> true
                initialState is Screen.Input && targetState is Screen.Result -> true
                else -> false
            }

            if (goingForward) {
                slideInHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> fullWidth } togetherWith
                        slideOutHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> -fullWidth }
            } else {
                slideInHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> -fullWidth } togetherWith
                        slideOutHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> fullWidth }
            }
        },
        label = "ScreenTransition"
    ) { targetScreen ->
        when (targetScreen) {
            is Screen.Home -> HomeScreen(vm)
            is Screen.Input -> InputScreen(vm)
            is Screen.Result -> ResultScreen(vm)
        }
    }
}

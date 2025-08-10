package com.example.splitly.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.example.splitly.ExpenseViewModel
import com.example.splitly.Screen
import com.example.splitly.ui.screens.HomeScreen
import com.example.splitly.ui.screens.InputScreen
import com.example.splitly.ui.screens.ResultScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpenseApp(vm: ExpenseViewModel) {
    AnimatedContent(
        targetState = vm.screen,
        transitionSpec = {
            // Determina la direzione dell'animazione in base alla transizione tra schermate
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

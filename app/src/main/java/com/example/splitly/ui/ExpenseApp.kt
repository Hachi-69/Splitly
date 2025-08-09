package com.example.splitly.ui

import androidx.compose.runtime.Composable
import com.example.splitly.ExpenseViewModel
import com.example.splitly.Screen
import com.example.splitly.ui.screens.HomeScreen
import com.example.splitly.ui.screens.InputScreen
import com.example.splitly.ui.screens.ResultScreen

@Composable
fun ExpenseApp(vm: ExpenseViewModel) {
    when (vm.screen) {
        is Screen.Home -> HomeScreen(vm)
        is Screen.Input -> InputScreen(vm)
        is Screen.Result -> ResultScreen(vm)
    }
}

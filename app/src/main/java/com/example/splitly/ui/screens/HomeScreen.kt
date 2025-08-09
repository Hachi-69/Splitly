package com.example.splitly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitly.ExpenseViewModel

@Composable
fun HomeScreen(vm: ExpenseViewModel) {
    val temp = remember { mutableStateOf(vm.numberOfPeople.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Splitly", modifier = Modifier.padding(bottom = 12.dp))

        OutlinedTextField(
            value = temp.value,
            onValueChange = { new -> temp.value = new.filter { it.isDigit() } },
            label = { Text("Number of people") }
        )

        Row(modifier = Modifier.padding(top = 12.dp)) {
            Button(onClick = {
                val n = temp.value.toIntOrNull() ?: 0
                if (n >= 1) {
                    vm.updateNumberOfPeople(n)
                    vm.initPersons()
                }
            }) {
                Text("Start")
            }
        }
    }
}

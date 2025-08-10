package com.example.splitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.splitly.ExpenseViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import com.example.splitly.ui.theme.BlizzardBlue


@Composable
fun HomeScreen(vm: ExpenseViewModel) {
    val temp = remember { mutableStateOf("3") }

    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "money", tint = BlizzardBlue, modifier = Modifier.padding(12.dp))
            }
            Column {
                Text(text = "Splitly", style = MaterialTheme.typography.headlineSmall)
            }
        }

        Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "How many people?")
                OutlinedTextField(
                    value = temp.value,
                    onValueChange = { new -> temp.value = new.filter { it.isDigit() } },
                    label = { Text("Number of people") },
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        val n = temp.value.toIntOrNull() ?: 0
                        if (n >= 1) {
                            vm.updateNumberOfPeople(n)
                            vm.initPersons()
                        }
                    }) {
                        Text(text = " Start split ")
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "start")
                    }
                }
            }
        }
    }
}

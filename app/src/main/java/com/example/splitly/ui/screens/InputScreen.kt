package com.example.splitly.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Person
import com.example.splitly.ExpenseViewModel
import com.example.splitly.ui.utils.parseAmountInput

@Composable
fun InputScreen(vm: ExpenseViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Enter names and amounts (e.g. 12.50)")

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(vm.persons) { person ->
                PersonRow(
                    person = person,
                    onNameChange = { vm.updatePersonName(person.id, it) },
                    onAmountChange = { cents -> vm.updatePersonAmountCents(person.id, cents) }
                )
            }
        }

        Row(modifier = Modifier.padding(top = 12.dp)) {
            Button(onClick = { vm.backToHome() }) {
                Text("Back")
            }
            Button(onClick = { vm.calculateAndShowResult() }, modifier = Modifier.padding(start = 12.dp)) {
                Text("Calculate")
            }
        }
    }
}

@Composable
fun PersonRow(person: Person, onNameChange: (String) -> Unit, onAmountChange: (Long) -> Unit) {
    val amtText = remember { mutableStateOf(centsToEditable(person.amountCents)) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = person.name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amtText.value,
            onValueChange = { newText ->
                val filtered = newText.filter { it.isDigit() || it == '.' }
                amtText.value = filtered
                onAmountChange(parseAmountInput(filtered))
            },
            label = { Text("Amount (e.g. 12.50)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}

fun centsToEditable(cents: Long): String {
    val euros = cents / 100
    val rem = (cents % 100).toInt()
    return "%d.%02d".format(euros, rem)
}

package com.example.splitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Person
import com.example.splitly.ExpenseViewModel
import com.example.splitly.ui.utils.parseAmountInput
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronLeft
import com.example.splitly.ui.theme.BlizzardBlue


@Composable
fun InputScreen(vm: ExpenseViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Enter names and amounts (es. 62.1)", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(vm.persons) { person ->
                PersonRow(person = person, onNameChange = { vm.updatePersonName(person.id, it) }, onAmountChange = { cents -> vm.updatePersonAmountCents(person.id, cents) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Modificato per allineare il bottone "Back" a sinistra
        ) {
            Button(onClick = { vm.backToHome() }) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "start")
                Text("Back")
            }
            Button(onClick = { vm.calculateAndShowResult() }, modifier = Modifier.padding(start = 12.dp)) {
                Text("Calculate ")
                Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculate", modifier = Modifier.padding(end = 4.dp))
            }
        }
    }
}

@Composable
fun PersonRow(person: Person, onNameChange: (String) -> Unit, onAmountChange: (Long) -> Unit) {
    val amtText = remember { mutableStateOf(if (person.amountCents == 0L) "" else centsToEditable(person.amountCents)) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // avatar circle with initial
            val initial = if (person.name.isBlank() || person.name.startsWith("Person ")) "?" else person.name.trim().first().uppercase()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlizzardBlue)
            ) {
                Text(text = initial, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                OutlinedTextField(
                    value = if (person.name == "Person ${person.id + 1}") "" else person.name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amtText.value,
                    onValueChange = { newText ->
                        val filtered = newText.filter { it.isDigit() || it == '.' }
                        amtText.value = filtered
                        onAmountChange(parseAmountInput(filtered))
                    },
                    label = { Text("Amount (es. 62.1)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "amount") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}

fun centsToEditable(cents: Long): String {
    if (cents == 0L) {
        return ""
    }
    val euros = cents / 100
    val rem = (cents % 100).toInt()
    return "%d.%02d".format(euros, rem)
}
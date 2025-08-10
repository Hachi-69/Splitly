package com.example.splitly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Transaction
import com.example.splitly.ExpenseViewModel
import com.example.splitly.ui.utils.centsToDisplay
import com.example.splitly.ui.theme.BlizzardBlue


@Composable
fun ResultScreen(vm: ExpenseViewModel) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "All balanced 🎉", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(vm.transactions) { t ->
                TransactionCard(t, vm)
            }
        }

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { vm.backToInput() }) {
                Text("Back")
            }
        }
    }
}

@Composable
fun TransactionCard(t: Transaction, vm: ExpenseViewModel) {
    val fromPerson = vm.persons.find { it.id == t.fromId }
    val fromPersonName = if (fromPerson != null && fromPerson.name.isNotBlank()) {
        fromPerson.name
    } else {
        "Person ${t.fromId + 1}"
    }
    val toPerson = vm.persons.find { it.id == t.toId }
    val toPersonName = if (toPerson != null && toPerson.name.isNotBlank()) { toPerson.name } else { "Person ${t.toId + 1}" }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "$fromPersonName  → "+"$toPersonName ", color = Color.Black)
            Text(text = " " + centsToDisplay(t.amountCents), style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        }
    }
}

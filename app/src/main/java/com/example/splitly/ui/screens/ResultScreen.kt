package com.example.splitly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
    // total in cents
    val totalCents = vm.persons.sumOf { it.amountCents }
    val numPersons = vm.persons.size

    Column(modifier = Modifier
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header: title + total spent
        Text(
            text = "All balanced 🎉",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Total spent card / label
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total spent", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                Text(text = centsToDisplay(totalCents), color = Color.Black, style = MaterialTheme.typography.titleMedium)
            }
        }

        // Average spent per person card / label
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Average per person", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                Text(text = centsToDisplay(totalCents / numPersons), color = Color.Black, style = MaterialTheme.typography.titleMedium)
            }
        }

        // Transactions list
        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(vm.transactions) { t ->
                TransactionCard(t, vm)
            }
        }

        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { vm.backToInput() }) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "start")
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
            Text(text = "$fromPersonName  ➔  $toPersonName  ", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
            Text(text = centsToDisplay(t.amountCents), style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        }
    }
}

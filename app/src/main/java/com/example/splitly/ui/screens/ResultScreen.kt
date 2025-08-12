/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Transaction
import com.example.splitly.ExpenseViewModel
import com.example.splitly.R
import com.example.splitly.ui.utils.centsToDisplay
import com.example.splitly.ui.theme.BlizzardBlue

/**
 * Displays the result screen after calculating expenses.
 *
 * This screen shows:
 * - A header with the app logo and title.
 * - A "All balanced" message.
 * - A card displaying the total amount spent.
 * - A card displaying the average amount spent per person.
 * - A list of transactions required to balance the expenses.
 * - A "Back" button to return to the input screen.
 * - A "Print to PDF" button to generate a PDF of the transactions.
 *
 * @param vm The [ExpenseViewModel] containing the data and logic for the screen.
 */
@Composable
fun ResultScreen(vm: ExpenseViewModel) {
    val context = LocalContext.current
    // total in cents
    val totalCents = vm.persons.sumOf { it.amountCents }
    val numPersons = vm.persons.size

    Column(modifier = Modifier
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                Image(
                    painter = painterResource(id = R.drawable.logo4),
                    contentDescription = "money",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column {
                Text(text = "Splitly", style = MaterialTheme.typography.headlineSmall)
            }
        }
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween, // Changed to SpaceBetween
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { vm.backToInput() }) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Back to input")
                Text("Back")
            }
            Button(onClick = { vm.printToPdf(context) }) {
                Icon(imageVector = Icons.Default.Print, contentDescription = "Print to PDF")
                Text(" Print PDF")
            }
        }
    }
}

/**
 * Composable function to display a single transaction as a card.
 * It shows who pays whom and the amount of the transaction.
 *
 * @param t The [Transaction] object to display.
 * @param vm The [ExpenseViewModel] used to fetch person details.
 */
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

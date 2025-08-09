package com.example.splitly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Transaction
import com.example.splitly.ExpenseViewModel
import com.example.splitly.ui.utils.centsToDisplay

@Composable
fun ResultScreen(vm: ExpenseViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Result")

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(vm.transactions) { t ->
                TransactionRow(t)
            }
        }

        Button(onClick = { vm.backToInput() }, modifier = Modifier.padding(top = 12.dp)) {
            Text("Back")
        }
    }
}

@Composable
fun TransactionRow(t: Transaction) {
    Text(text = "Person ${t.fromId + 1} pays Person ${t.toId + 1}: ${centsToDisplay(t.amountCents)}")
}

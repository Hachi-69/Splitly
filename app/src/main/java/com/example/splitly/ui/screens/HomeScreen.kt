/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitly.ExpenseViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import com.example.splitly.R


/**
 * Composable function for the home screen of the Splitly app.
 *
 * This screen allows the user to input the number of people to split expenses with.
 * It features the app logo, a title, a card for input, and a button to proceed.
 *
 * @param vm The [ExpenseViewModel] used to manage and update expense-related data.
 */
@Composable
fun HomeScreen(vm: ExpenseViewModel) {
    val temp = remember { mutableStateOf("3") }

    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(modifier = Modifier.size(5.dp))
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
        Spacer(modifier = Modifier.size(10.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "How many people?")
                OutlinedTextField(
                    value = temp.value,
                    onValueChange = { new -> temp.value = new.filter { it.isDigit() } },
                    label = { Text("Number of people") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

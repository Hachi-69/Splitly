package com.example.splitly

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.splitly.data.Person
import com.example.splitly.data.Transaction
import com.example.splitly.domain.calculateTransactions

sealed class Screen {
    object Home : Screen()
    object Input : Screen()
    object Result : Screen()
}

class ExpenseViewModel : ViewModel() {
    var screen by mutableStateOf<Screen>(Screen.Home)
        private set

    var numberOfPeople by mutableStateOf(2)
        private set

    var persons by mutableStateOf<List<Person>>(emptyList())
        private set

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    fun updateNumberOfPeople(n: Int) {
        if (n < 1) return
        numberOfPeople = n
    }

    fun initPersons() {
        val list = (0 until numberOfPeople).map { Person(id = it, name = "Person ${it + 1}") }
        persons = list
        screen = Screen.Input
    }

    fun updatePersonName(id: Int, name: String) {
        persons = persons.map { if (it.id == id) it.copy(name = name) else it }
    }

    fun updatePersonAmountCents(id: Int, cents: Long) {
        persons = persons.map { if (it.id == id) it.copy(amountCents = cents) else it }
    }

    fun calculateAndShowResult() {
        transactions = calculateTransactions(persons)
        screen = Screen.Result
    }

    fun backToHome() {
        screen = Screen.Home
    }

    fun backToInput() {
        screen = Screen.Input
    }
}
/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.splitly.data.Person
import com.example.splitly.data.Transaction
import com.example.splitly.domain.calculateTransactions

/**
 * Represents the different screens in the application.
 * This is a sealed class, meaning all possible subclasses are defined within this file.
 */
sealed class Screen {
    object Home : Screen()
    object Input : Screen()
    object Result : Screen()
}

/**
 * ViewModel for managing expense splitting logic and UI state.
 *
 * This ViewModel handles the flow of the application, from setting the number of people
 * involved in an expense, to inputting individual amounts, calculating transactions,
 * and displaying the results.
 *
 * It utilizes Compose's `mutableStateOf` to manage UI state, ensuring that changes
 * to data automatically trigger recomposition of relevant UI elements.
 *
 * Key responsibilities:
 * - Managing the current screen (`Screen.Home`, `Screen.Input`, `Screen.Result`).
 * - Storing and updating the number of people involved.
 * - Storing and updating a list of `Person` objects, each representing an individual
 *   and their contribution.
 * - Storing and updating a list of `Transaction` objects, representing the calculated
 *   payments needed to settle debts.
 * - Providing functions to update person details (name, amount).
 * - Triggering the calculation of transactions and navigating to the result screen.
 * - Providing navigation functions to move between screens.
 */
class ExpenseViewModel : ViewModel() {
    var screen by mutableStateOf<Screen>(Screen.Home)
        private set

    var numberOfPeople by mutableStateOf(2)
        private set

    var persons by mutableStateOf<List<Person>>(emptyList())
        private set

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    /**
     * Updates the number of people involved in the expense splitting.
     *
     * The number of people must be at least 1. If a value less than 1 is provided,
     * the function will return without making any changes.
     *
     * @param n The new number of people.
     */
    fun updateNumberOfPeople(n: Int) {
        if (n < 1) return
        numberOfPeople = n
    }

    /**
     * Initializes the list of persons based on the current [numberOfPeople].
     * Each person is assigned a unique ID and a default name like "Person 1", "Person 2", etc.
     * After initialization, the screen is set to [Screen.Input] to allow for further input.
     */
    fun initPersons() {
        val list = (0 until numberOfPeople).map { Person(id = it, name = "Person ${it + 1}") }
        persons = list
        screen = Screen.Input
    }

    /**
     * Updates the name of a person in the list.
     *
     * @param id The ID of the person to update.
     * @param name The new name for the person.
     */
    fun updatePersonName(id: Int, name: String) {
        persons = persons.map { if (it.id == id) it.copy(name = name) else it }
    }

    /**
     * Updates the amount paid by a person.
     *
     * @param id The ID of the person.
     * @param cents The new amount paid by the person in cents.
     */
    fun updatePersonAmountCents(id: Int, cents: Long) {
        persons = persons.map { if (it.id == id) it.copy(amountCents = cents) else it }
    }

    /**
     * Calculates the transactions based on the current list of persons and updates the screen to
     * display the results. This function triggers the calculation of how debts should be settled
     * among the persons and then navigates the UI to the result screen.
     */
    fun calculateAndShowResult() {
        transactions = calculateTransactions(persons)
        screen = Screen.Result
    }

    /**
     * Navigates back to the home screen.
     */
    fun backToHome() {
        screen = Screen.Home
    }

    /**
     * Navigates back to the input screen.
     * This function is typically used when the user wants to modify the input data
     * after viewing the results or from another part of the application.
     */
    fun backToInput() {
        screen = Screen.Input
    }

    /**
     * Generates a PDF document summarizing the transactions and saves it to the device's
     * downloads directory.
     * This function will be called when the "Print PDF" button is clicked.
     * @param context The application context, needed for accessing storage.
     */
    fun printToPdf(context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()

        var yPosition = 40f
        paint.textSize = 16f
        canvas.drawText("Splitly - Transaction Summary", 40f, yPosition, paint)
        yPosition += 30f

        paint.textSize = 12f
        persons.forEach { person ->
            canvas.drawText("${person.name} paid: ${"%.2f".format(person.amountCents / 100.0)}", 40f, yPosition, paint)
            yPosition += 20f
        }

        yPosition += 20f
        canvas.drawText("Transactions:", 40f, yPosition, paint)
        yPosition += 20f

        transactions.forEach { transaction ->
            val fromPersonName = persons.find { it.id == transaction.fromId }?.name ?: "Unknown"
            val toPersonName = persons.find { it.id == transaction.toId }?.name ?: "Unknown"
            canvas.drawText("$fromPersonName pays $toPersonName: ${"%.2f".format(transaction.amountCents / 100.0)}", 40f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)

        val downloadsDir = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOWNLOADS).firstOrNull()
        if (downloadsDir == null) {
            Log.e("PdfGeneration", "Downloads directory not found")
            pdfDocument.close()
            return
        }

        val filePath = downloadsDir.absolutePath + "/Splitly_Summary.pdf"
        val file = java.io.File(filePath)
        try {
            pdfDocument.writeTo(java.io.FileOutputStream(file))
            Log.d("PdfGeneration", "PDF saved to $filePath")
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            Log.e("PdfGeneration", "Error writing PDF: ${e.message}")
        }
        pdfDocument.close()
    }
}
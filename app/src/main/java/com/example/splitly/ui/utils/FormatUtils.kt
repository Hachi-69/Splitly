package com.example.splitly.ui.utils

/** Parse user input like "12.50" into cents (Long). Non-parsable -> 0L */
fun parseAmountInput(input: String): Long {
    if (input.isBlank()) return 0L
    val cleaned = input.trim().replace(',', '.')
    return try {
        val d = cleaned.toDouble()
        (d * 100.0).toLong()
    } catch (e: Exception) {
        0L
    }
}

fun centsToDisplay(cents: Long): String {
    val euros = cents / 100
    val rem = (cents % 100).toInt()
    return "%d.%02d €".format(euros, rem)
}
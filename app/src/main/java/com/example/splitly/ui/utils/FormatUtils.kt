/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui.utils

/**
 * Parses a user input string representing an amount and converts it to cents (Long).
 *
 * This function handles various input formats:
 * - Empty or blank strings are parsed as 0L.
 * - Commas are treated as decimal separators (e.g., "12,50" is parsed as 12.50).
 * - Leading and trailing whitespace is ignored.
 * - If the input cannot be parsed as a valid number, it returns 0L.
 *
 * @param input The string to parse.
 * @return The amount in cents as a Long, or 0L if parsing fails or the input is blank.
 */
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

/** Formats cents (Long) into a displayable string like "12.50 €". */
fun centsToDisplay(cents: Long): String {
    val euros = cents / 100
    val rem = (cents % 100).toInt()
    return "%d.%02d €".format(euros, rem)
}
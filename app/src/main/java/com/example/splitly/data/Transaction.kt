/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.data

/**
 * Represents a financial transaction between two users.
 *
 * @property fromId The ID of the user sending the money.
 * @property toId The ID of the user receiving the money.
 * @property amountCents The amount of money transferred, in cents.
 */
data class Transaction(
    val fromId: Int,
    val toId: Int,
    val amountCents: Long
)
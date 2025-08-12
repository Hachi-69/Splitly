/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.data

/**
 * Represents a person involved in a transaction.
 *
 * This data class stores amounts internally as cents (Long) to avoid floating point issues.
 *
 * @property id The unique identifier for the person.
 * @property name The name of the person. Defaults to an empty string.
 * @property amountCents The amount associated with this person, stored in cents. Defaults to 0.
 */
data class Person(
    val id: Int,
    val name: String = "",
    val amountCents: Long = 0L
)
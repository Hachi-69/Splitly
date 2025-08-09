package com.example.splitly.data

/**
 * Person stores amounts internally as cents (Long) to avoid floating point issues.
 */
data class Person(
    val id: Int,
    val name: String = "",
    val amountCents: Long = 0L
)
package com.example.splitly.data

data class Transaction(
    val fromId: Int,
    val toId: Int,
    val amountCents: Long
)
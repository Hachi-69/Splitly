package com.example.splitly.domain

import com.example.splitly.data.Person
import com.example.splitly.data.Transaction

/**
 * Compute a simple set of transactions that settle debts using a greedy algorithm.
 * All arithmetic uses cents (Long).
 */
fun calculateTransactions(persons: List<Person>): List<Transaction> {
    val n = persons.size
    if (n == 0) return emptyList()

    val total = persons.sumOf { it.amountCents }
    val baseQuota = total / n
    val remainder = (total % n).toInt() // 0..n-1 cents to distribute

    // build quotas according to list order: first `remainder` persons get +1 cent
    val quotas = LongArray(n) { baseQuota }
    for (i in 0 until remainder) quotas[i] = quotas[i] + 1

    // balances: positive => should receive, negative => should pay
    data class Balance(val id: Int, var amount: Long)

    val balances = persons.mapIndexed { idx, p -> Balance(p.id, p.amountCents - quotas[idx]) }.toMutableList()

    val creditors = balances.filter { it.amount > 0 }.sortedByDescending { it.amount }.toMutableList()
    val debtors = balances.filter { it.amount < 0 }.sortedBy { it.amount }.toMutableList() // most negative first

    val result = mutableListOf<Transaction>()

    var ci = 0
    var di = 0

    while (ci < creditors.size && di < debtors.size) {
        val credit = creditors[ci]
        val debt = debtors[di]

        val creditAmt = credit.amount
        val debtAmt = -debt.amount // positive

        val transfer = minOf(creditAmt, debtAmt)
        if (transfer > 0) {
            result.add(Transaction(fromId = debt.id, toId = credit.id, amountCents = transfer))

            credit.amount -= transfer
            debt.amount += transfer // debt.amount is negative
        }

        if (credit.amount == 0L) ci++
        if (debt.amount == 0L) di++
    }

    return result
}
/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.domain

import com.example.splitly.data.Person
import com.example.splitly.data.Transaction

/**
 * Compute a simple set of transactions that settle debts using a greedy algorithm.
 * All arithmetic uses cents (Long).
 *
 * The algorithm works as follows:
 * 1. Calculate the total amount paid by all persons.
 * 2. Calculate the base quota for each person (total amount / number of persons).
 * 3. Distribute any remaining cents to the first few persons in the list (one cent per person).
 * 4. Calculate the balance for each person (amount paid - quota). A positive balance means the person is owed money,
 *    while a negative balance means the person owes money.
 * 5. Create two lists: one for creditors (persons with positive balances) and one for debtors (persons with negative balances).
 * 6. Sort creditors in descending order of their balance and debtors in ascending order of their balance (most negative first).
 * 7. Iterate through the creditors and debtors, creating transactions to settle the debts.
 *    In each step, transfer the minimum of the creditor's balance and the absolute value of the debtor's balance.
 * 8. Update the balances of the creditor and debtor.
 * 9. If a creditor's balance becomes zero, move to the next creditor.
 * 10. If a debtor's balance becomes zero, move to the next debtor.
 * 11. Repeat steps 7-10 until all debts are settled.
 *
 * @param persons A list of [Person] objects, each representing a person and the amount they paid.
 * @return A list of [Transaction] objects representing the transactions required to settle the debts.
 *         Returns an empty list if the input list of persons is empty.
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
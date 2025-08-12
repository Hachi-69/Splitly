# Splitly — Expense splitting app (Android, Jetpack Compose)

A compact, dependency-light Android app written in Kotlin + Jetpack Compose that helps a group of people split expenses and computes the minimal set of payments required to settle balances.
This README documents the project structure, core algorithm, how to build/run the app, example usage, limitations, and the license (Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International).

---

## Table of contents

* [Project overview](#project-overview)
* [Why Splitly?](#why-splitly)
* [Highlights & key features](#highlights--key-features)
* [Project structure](#project-structure-important-files)
* [Core algorithm (debt settlement)](#core-algorithm-debt-settlement)
* [Public API (important classes & functions )](#public-api-important-classes--functions)
* [Build & run](#build--run)
* [Usage / UX flow](#usage--ux-flow)
* [Examples](#examples)
* [Limitations & notes](#limitations--notes)
* [Contributing & license considerations](#contributing--license-considerations)
* [License](#license)

---

## Project overview

Splitly is a small Android application that:

* Lets users specify a number of people, enter each person’s name and how much they paid (amounts are entered in euros but handled internally as **cents**).
* Computes who owes whom using a greedy settlement algorithm and displays the list of transactions to balance expenses.
* Uses Jetpack Compose for the UI and a simple `ViewModel`-based state management.

The app aims to be straightforward, robust to floating-point issues (stores money as `Long` cents), and easy to inspect and extend.

---

## Why Splitly?

While many expense-splitting apps exist, Splitly focuses on:
*   **Simplicity and Speed:** Get to the settlement transactions with minimal fuss.
*   **Offline First:** Operates entirely locally without requiring accounts or internet connectivity.
*   **Transparency:** The algorithm used is straightforward, ensuring users can understand how debts are settled.
*   **Modern Android Development Showcase:** Utilizes the latest Android Jetpack libraries and best practices, making it a good learning resource.

---

## Highlights & key features

* Monetary values are represented internally in **cents** (`Long`) to avoid floating point rounding issues.
* Greedy debt-settling algorithm (`calculateTransactions`) that:

  * computes per-person quota (evenly split) and distributes leftover cents deterministically,
  * separates creditors and debtors and settles amounts by pairwise transfers.
* Clean Compose screens with `Home`, `Input`, and `Result` flows, animated transitions, and simple Material theming.
* Minimal dependencies: Kotlin, AndroidX / Jetpack Compose, `ViewModel`.

---

## Project structure (important files)

```
com.example.splitly
├─ MainActivity.kt            // app entry: sets Compose content
├─ Screen.kt                 // Screen sealed class
├─ ExpenseViewModel.kt       // ViewModel and app state
├─ domain/
│   └─ calculateTransactions // debt-settling algorithm
├─ data/
│   ├─ Person.kt
│   └─ Transaction.kt
├─ ui/
│   ├─ ExpenseApp.kt          // root composable + AnimatedContent
│   ├─ screens/
│   │   ├─ HomeScreen.kt
│   │   ├─ InputScreen.kt
│   │   └─ ResultScreen.kt
│   ├─ utils/
│   │   ├─ parseAmountInput.kt // parsing + formatting helpers
│   │   └─ centsToDisplay.kt
│   └─ theme/
│       ├─ Colors.kt
│       ├─ Theme.kt
│       └─ Typography.kt
```

---

## Core algorithm (debt settlement)

`calculateTransactions(persons: List<Person>): List<Transaction>`

High-level summary:

1. Convert all person amounts to cents (already stored as `Long` cents).
2. Compute the total paid across everyone.
3. Compute the integer base quota per person: `baseQuota = total / n`.
4. Distribute the integer remainder (`total % n`) deterministically to the first `remainder` people (one extra cent each). This ensures quotas sum exactly to `total`.
5. For each person compute: `balance = paid - quota`.

   * `balance > 0` → the person should **receive** money (creditor).
   * `balance < 0` → the person must **pay** money (debtor).
6. Sort creditors in descending order of balance and debtors in ascending order (most negative first).
7. Walk creditors and debtors with two indexes and create transactions where each transaction transfers `min(creditorBalance, -debtorBalance)`.
8. Continue until all balances reach zero (or no further matches possible).

Complexity: dominated by sorting creditors/debtors → roughly `O(n log n)` where `n` is number of persons.

Design choices / guarantees:

* All arithmetic uses integer cents; no floating point rounding errors.
* Quota remainder is assigned to the first persons in the list (deterministic; preserves total).
* The algorithm produces a correct settlement set such that after applying all transactions each person's net equals the quota.
* The algorithm is greedy and produces a correct settlement; it does not attempt to optimize for minimum number of transactions beyond the typical greedy approach.

---

## Public API (important classes & functions)

* `data class Person(val id: Int, val name: String = "", val amountCents: Long = 0L)`
* `data class Transaction(val fromId: Int, val toId: Int, val amountCents: Long)`
* `fun calculateTransactions(persons: List<Person>): List<Transaction>` — detailed above.
* `ExpenseViewModel`:

  * `var screen` — one of `Screen.Home | Screen.Input | Screen.Result`
  * `var numberOfPeople`
  * `var persons` — list of `Person`
  * `var transactions` — list of `Transaction`
  * `fun updateNumberOfPeople(n: Int)`
  * `fun initPersons()`
  * `fun updatePersonName(id: Int, name: String)`
  * `fun updatePersonAmountCents(id: Int, cents: Long)`
  * `fun calculateAndShowResult()` — computes transactions and navigates to result screen
  * `fun backToHome()`, `fun backToInput()`

Utility functions:

* `fun parseAmountInput(input: String): Long` — parses user input like `62.1` or `12,50` into cents (`Long`).
* `fun centsToDisplay(cents: Long): String` — formats cents into a display string like `12.50 €`.
* `fun centsToEditable(cents: Long): String` — formats cents into `12.50` for input fields.

---

## Build & run

Prerequisites

* Android Studio (latest stable recommended) with Android SDK and an emulator or device.
* JDK 11+ (as required by your Android Gradle Plugin / Kotlin setup).

Typical steps

1. Clone the repository:

   ```bash
   gh repo clone Hachi-69/Splitly
   cd splitly
   ```
2. Open the project in Android Studio (`File -> Open`).
3. Let Gradle sync, then run the app on an emulator or device via the Run button.

Command-line (Gradle wrapper)

```bash
# build debug apk
./gradlew assembleDebug

# install on a connected device
./gradlew installDebug
```

(Windows: use `gradlew.bat` instead of `./gradlew`.)

Notes:

* If you see errors about Android SDK location, open the project in Android Studio and install the required SDK and build tools.
* The project uses Jetpack Compose and AndroidX ViewModel; make sure your Android Gradle Plugin and Kotlin plugin in the project are set accordingly.

---

## Usage / UX flow

1. **Home** — choose number of people (numeric input). Tap **Start split**.
2. **Input** — enter each person's name and the amount they paid (examples in the UI: `62.1` or `12,50`). Values are parsed to cents internally.

   * The UI validates numeric input and parses decimal separators (`.` or `,`).
   * Tap **Calculate** to compute the result; tap **Back** to return to Home.
3. **Result** — shows:

   * Total spent and average per person (displayed in euros).
   * A list of transactions like `Person A ➔ Person B — 12.50 €` describing who should pay whom.
   * A **Back** button to return to the input screen for adjustments.

---

## Examples

### Example 1 — Simple

Input:

* Person 1: `62.10` (€62.10)
* Person 2: `0.00`
* Person 3: `0.00`

Internal (cents):

* `6210`, `0`, `0` → `total = 6210` cents

Quota:

* `quota = total / 3 = 2070` cents (no remainder)

Balances:

* Person 1: `6210 - 2070 = 4140` (creditor)
* Person 2: `0 - 2070 = -2070` (debtor)
* Person 3: `0 - 2070 = -2070` (debtor)

Transactions produced:

* Person 2 → Person 1 : `2070` cents (`20.70 €`)
* Person 3 → Person 1 : `2070` cents (`20.70 €`)

### Example 2 — Remainder (demonstrating remainder distribution)

Input amounts (euros): `10.00`, `10.00`, `10.01` → cents: `1000`, `1000`, `1001` → `total = 3001`

* `baseQuota = 3001 / 3 = 1000`, `remainder = 1` → the first person (index 0) gets +1 cent quota.
* quotas = `[1001, 1000, 1000]`
* balances = `[-1, 0, 1]` → transactions: Person 1 pays 1 cent to Person 3.

---

## Limitations & notes

* Algorithm: greedy approach that produces a correct settlement. It is not guaranteed to minimize the number of transactions in every mathematically possible settlement, but it is efficient and deterministic.
* Quota remainder assignment is deterministic by index order. If you want different remainder-distribution heuristics (e.g., by name or by who paid), modify quota assignment logic.
* Currency formatting is EUR in the UI (`€`) — you can adapt `centsToDisplay` for other locales or currency symbols.
* License implications (see below) affect how others may reuse or modify this code.

---

## Contributing & License Considerations

This repository is licensed under **CARL BY, NC-PA 1.0**  
*(Commercial Authorization & Restricted License — Attribution required; Non-Commercial with Paid Authorization)*.  
See [`LICENSE`](LICENSE) for the full terms.

### Key points for contributors and downstream users

- **Modifications & Derivative Works (Non-Commercial Only)**  
  You **may** modify the project and publicly distribute modified or derivative versions **only** for **non-commercial** purposes.  
  Any public distribution of modified code must comply with the license conditions:
  - attribution to the original author,
  - inclusion of the license notice,
  - no removal/obscuring of credits.  
  Standard fork/PR workflows that result in publicly published modified code are allowed **only** if the resulting release is **non-commercial** and meets these requirements.

- **NonCommercial**  
  You may **not** use the project or any derivatives for commercial purposes unless you have obtained **prior written authorization** from the repository owner **and** agreed on compensation (fees, royalties, or other consideration).  
  Unauthorized commercial use is prohibited and constitutes copyright infringement.

- **Attribution**  
  Any permitted sharing (including distribution of modified non-commercial versions) must:
  - clearly credit the original author, and
  - include a copy of, or link to, [`LICENSE`](LICENSE).  
  Original credits and copyright notices must **not** be removed, hidden, or obscured.

Because commercial use and certain sublicensing are restricted under this license, please contact the repository owner if you would like to:

- propose changes,
- contribute code (contributions should be provided under the project license or otherwise cleared with the owner),
- obtain permission to create publicly available derivatives for other purposes,
- request re-licensing, or
- use the project in a commercial product.

**Contact:** ` Luca Turillo — turilloluca2005@gmail.com`


Bug reports and issues are welcome; open an issue describing the problem or requested enhancement. The repository owner can accept patches internally and decide how to integrate or re-release them.

---

## License

This project is distributed under the **Commercial Authorization & Restricted License attribution required non-commercial 1.0** license.

**Short summary:** © 2025 Luca Turillo. Licensed under CARL BY, NC-PA 1.0.
Use and modification allowed for NON-COMMERCIAL purposes only.
Commercial use permitted only with prior written authorization and agreed compensation.
See [`LICENSE`](LICENSE) for details.

Last updated: 2025-08-12

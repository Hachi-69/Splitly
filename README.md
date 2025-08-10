Licensed under CC BY-NC-ND 4.0 — © Luca Turillo 2025

# Splitly

**Splitly** — Android app to split group expenses quickly and fairly. This repository contains a Kotlin + Jetpack Compose (Material 3) implementation that computes minimal transactions to settle debts between N participants. The UI is optimized for a dark, playful look but the codebase and architecture are production-oriented and easy to extend.

---

## Table of Contents

* [Project Overview](#project-overview)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Architecture & Design](#architecture--design)
* [Project Structure](#project-structure)
* [Getting Started](#getting-started)

  * [Prerequisites](#prerequisites)
  * [Clone & Open](#clone--open)
  * [Build & Run](#build--run)
* [Usage](#usage)
* [Data Model & Algorithm](#data-model--algorithm)
* [UI & Theming Notes](#ui--theming-notes)
* [Assets & Licensing](#assets--licensing)
* [Testing](#testing)
* [Contributing](#contributing)
* [Security & Privacy Notes](#security--privacy-notes)
* [Release / Play Store Checklist](#release--play-store-checklist)
* [Troubleshooting](#troubleshooting)
* [Contact](#contact)
* [License](#license)

---

## Project Overview

Splitly is a compact Android application that helps groups of people divide shared expenses. Users enter the number of participants, each person’s spent amount, and Splitly calculates who owes whom and suggests a minimal set of transactions to settle the debt.

The implementation emphasizes correctness (currency handling in cents), simplicity of UI using Jetpack Compose with Material 3, and an MVVM-inspired separation of concerns.

---

## Features

* Specify arbitrary number of participants
* Enter names and amounts (currency-aware internal representation in cents)
* Greedy algorithm to compute a compact set of settlement transactions
* Dark-first playful UI with cards, icons and lighthearted copy
* Local state management via `ViewModel` + Compose `mutableStateOf`
* Modular codebase easy to extend (algorithm separate from UI)

---

## Tech Stack

* Kotlin (JVM)
* Android SDK (minSdk 28, targetSdk 36 in the current code)
* Jetpack Compose (Material 3)
* AndroidX Lifecycle / ViewModel
* Gradle Kotlin DSL (`build.gradle.kts`)

---

## Architecture & Design

The project follows a simple, pragmatic separation:

* **UI**: Jetpack Compose screens and composables (package `com.example.splitly.ui` and `ui.screens`).
* **ViewModel**: `ExpenseViewModel` (package root) holds UI state (screen, list of persons, transactions) and orchestrates operations.
* **Domain**: `calculateTransactions` (package `domain`) contains pure logic to compute settlements — fully unit-testable.
* **Data**: `Person` and `Transaction` models (package `data`).

This separation makes it easy to replace the storage layer, add persistence (Room / DataStore), or expose the domain logic as a library.

---

## Project Structure

```
app/
 └─ src/main/java/com/example/splitly/
    ├─ MainActivity.kt
    ├─ ExpenseViewModel.kt
    ├─ data/
    │  ├─ Person.kt
    │  └─ Transaction.kt
    ├─ domain/
    │  └─ Calculator.kt
    ├─ ui/
    │  ├─ ExpenseApp.kt
    │  ├─ ui.screens/
    │  │  ├─ HomeScreen.kt
    │  │  ├─ InputScreen.kt
    │  │  └─ ResultScreen.kt
    │  ├─ theme/
    │  │  ├─ Color.kt
    │  │  ├─ Type.kt
    │  │  └─ Theme.kt
    │  └─ utils/
    │     └─ FormatUtils.kt
    └─ res/
       └─ drawable/ (vector assets, mascots, stickers)
```

---

## Getting Started

### Prerequisites

* Android Studio (Arctic Fox or later recommended; use latest stable for best Compose support)
* JDK 11
* Android SDK with an emulator or a physical Android device (minSdk 28 in the current configuration)

### Clone & Open

```bash
git clone https://github.com/<your-username>/splitly.git
cd splitly
# Open the folder in Android Studio (File -> Open)
```

If you maintain a different package id locally (for example `com.example.splitly`), ensure your AndroidManifest and Gradle `namespace` match the package structure.

### Build & Run

* In Android Studio: **Build → Clean Project**, then **Build → Rebuild Project**. Fix any missing imports suggested by the IDE.
* Run on emulator: create an Android Virtual Device (AVD) and press the green **Run** button.

Command-line (Gradle wrapper):

```bash
./gradlew assembleDebug
./gradlew installDebug
```

---

## Usage

High-level flow of the app:

1. **Home** — set number of people and press Start.
2. **Input** — provide a name (optional) and an amount for each participant (amounts are parsed and stored as cents internally).
3. **Calculate** — app computes the fair share and displays the list of transactions required to settle debts.
4. **Result** — review suggested transfers and optionally return to input.

The UI uses an MVVM pattern; all actions are dispatched from the `ExpenseViewModel`.

---

## Data Model & Algorithm

### Data models

* `Person(id: Int, name: String, amountCents: Long)` — amounts in cents to avoid floating point rounding.
* `Transaction(fromId: Int, toId: Int, amountCents: Long)` — represents a single payment from one participant to another.

### Calculation algorithm

`calculateTransactions(persons: List<Person>): List<Transaction>`

The implementation uses a deterministic greedy algorithm:

1. Compute `total = sum(amountCents)`.
2. Compute `baseQuota = total / N` and distribute the remainder cents to the first `remainder` participants to maintain integer arithmetic correctness.
3. Build creditor (positive balance) and debtor (negative balance) lists.
4. Iteratively match largest creditor vs largest debtor, transferring `min(credit, debt)` until balances zero.

This algorithm is O(N log N) due to sorting and produces a compact set of transactions that is usually minimal for practical inputs.

---

## UI & Theming Notes

* **Material 3** is used. The `SplitlyTheme` in `ui.theme.Theme.kt` forces a dark color scheme as the default UI.
* Typography is defined in `Type.kt` and color tokens in `Color.kt`.
* Icons use Material icons; if you add extra icons, ensure you include the `material-icons-extended` dependency or use vector drawables in `res/drawable`.
* The UI emphasizes cards, rounded corners and playful copy; the theme is dark-by-default but can be extended with a toggle.

---

## Assets & Licensing

This project purposely contains playful references and is friendly to memes. Important legal notes for the repository:

* **Do not add copyrighted artwork** (e.g. official Shrek, Toothless) without written permission. Using trademarked characters may lead to takedown requests or Play Store rejection.
* Prefer **original artwork**, **public-domain** assets, or **CC0/CC-BY** resources. When using third-party assets, always include attribution and license text in `ASSETS.md` or the `README`.
* Place drawable assets under `app/src/main/res/drawable/`. Prefer **vector drawables** (`.xml`) for scalability and small size.

Suggested file names in `res/drawable/`:

* `ic_mascot_goose.xml` — stylized goose mascot (vector)
* `sticker_goose_party.png` — small PNG sticker (optional)

When publishing, include a short `CREDITS` or `ASSETS` section listing sources and licenses for third-party images or icons.

---

## Testing

* Unit-test the domain logic (`calculateTransactions`) with JUnit: assert balance conservation and correctness for corner cases (2 people, zero values, large sums, remainder distribution).
* UI tests: Compose UI testing or Espresso for integration flows.

Example unit test sketch:

```kotlin
@Test
fun calculateTransactions_balancesToZero() {
  val persons = listOf(Person(0, "A", 10000), Person(1, "B", 0))
  val tx = calculateTransactions(persons)
  // validate transactions sum equals difference and final nets are zero
}
```

---

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Fork the repository and create a feature branch.
2. Keep the code style consistent (Kotlin idiomatic, no unused imports, format with IDE defaults).
3. Add unit tests for any behavior changes.
4. Submit a Pull Request describing the change and include screenshots for UI updates.

Consider opening an issue first if the change is non-trivial.

---

## Security & Privacy Notes

* Splitly runs locally and does not ship any telemetry or analytics by default.
* If you add cloud sync or backups later, clearly document what is uploaded and ensure users consent.

---

## Release / Play Store Checklist

* Ensure targetSdk and compileSdk are up-to-date with Play Store requirements.
* Provide a privacy policy URL if the app collects or transmits user data.
* Provide high-quality screenshots and an icon. Ensure you own or have licenses for artwork used.
* Sign the release AAB with an appropriate key and keep the keystore secure.

---

## Troubleshooting

**Common build issues**

* *Unresolved MaterialTheme / Surface*: ensure `implementation("androidx.compose.material3:material3:<version>")` is present and you import `androidx.compose.material3.*`.
* *Icons unresolved*: add `implementation("androidx.compose.material:material-icons-extended:<version>")` or use `painterResource` with local vector drawables.
* *Gradle version / BOM mismatches*: prefer the Compose BOM or align versions used in `build.gradle.kts`.

If you run into build errors, run `./gradlew assembleDebug --stacktrace` and inspect the error; often the IDE Quick-Fix will suggest the missing imports.

---

## Contact

If you have questions, feature requests or want to collaborate, contact:

* Author: **Luca Turillo** — `turilloluca2005@gmail.com`

---

## License

This project is licensed under the
[Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License](https://creativecommons.org/licenses/by-nc-nd/4.0/).  
© Luca Turillo 2025. See the `LICENSE` file for full text.

---

*Last updated: 2025-08-10*

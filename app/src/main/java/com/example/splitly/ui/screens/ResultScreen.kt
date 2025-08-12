/**
 * © 2025 Luca Turillo — Splitly
 * Licensed under CARL BY, NC-PA 1.0
 * Use and modification allowed ONLY for NON-COMMERCIAL purposes.
 * Commercial use permitted only with prior written authorization and agreed compensation.
 * Attribution to the author must be preserved. See LICENSE. Contact: turilloluca2005@gmail.com
 */

package com.example.splitly.ui.screens

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.splitly.data.Transaction
import com.example.splitly.ExpenseViewModel
import com.example.splitly.R
import com.example.splitly.ui.theme.BlizzardBlue
import com.example.splitly.ui.utils.centsToDisplay
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Displays the result screen after calculating expenses.
 *
 * This screen shows:
 * - A header with the app logo and title "Splitly".
 * - A success message "All balanced 🎉".
 * - A card displaying the total amount spent by all participants.
 * - A card displaying the average amount spent per person.
 * - A scrollable list of [TransactionCard]s, each detailing a required payment
 *   between participants to settle the expenses.
 * - A "Back" button (left chevron icon) to navigate back to the input screen.
 * - A "Print PDF" button (print icon and text) to generate and share a PDF summary
 *   of the transactions.
 * - A footer with copyright information "© 2025 Luca Turillo — Splitly".
 *
 * The layout uses a [Box] to fill the screen and centers content vertically.
 * Amounts are displayed using the [centsToDisplay] utility function.
 *
 * @param vm The [ExpenseViewModel] instance that holds the state and business logic,
 *           including the list of persons, their expenses, and the calculated transactions.
 */
@Composable
fun ResultScreen(vm: ExpenseViewModel) {
    val context = LocalContext.current
    // total in cents
    val totalCents = vm.persons.sumOf { it.amountCents }
    val numPersons = vm.persons.size

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.TopCenter)
    ) {
            Spacer(modifier = Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                Image(
                    painter = painterResource(id = R.drawable.logo4),
                    contentDescription = "money",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column {
                Text(text = "Splitly", style = MaterialTheme.typography.headlineSmall)
            }
        }
        Text(
            text = "All balanced 🎉",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total spent", color = androidx.compose.ui.graphics.Color.Black, style = MaterialTheme.typography.titleMedium)
                Text(text = centsToDisplay(totalCents), color = androidx.compose.ui.graphics.Color.Black, style = MaterialTheme.typography.titleMedium)
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Average per person", color = androidx.compose.ui.graphics.Color.Black, style = MaterialTheme.typography.titleMedium)
                Text(text = centsToDisplay(totalCents / numPersons), color = androidx.compose.ui.graphics.Color.Black, style = MaterialTheme.typography.titleMedium)
            }
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(bottom = 70.dp)) {
            items(vm.transactions) { t ->
                TransactionCard(t, vm)
            }
        }
        if (vm.transactions.isNotEmpty()) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 0.dp, bottom = 8.dp))
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        ) {
            Button(onClick = { vm.backToInput() }) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Back to input")
            }
            Button(onClick = { printTransactionsToPdf(context, vm.transactions, vm) }) {
                Icon(imageVector = Icons.Default.Print, contentDescription = "Print to PDF")
                Text(" Print PDF")
            }
        }
        Text(
            text = "© 2025 Luca Turillo — Splitly",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
        )
        }
    }


/**
 * Initiates the printing process for the list of transactions to a PDF file.
 *
 * This function utilizes the Android `PrintManager` to create a print job.
 * It employs a [TransactionsPrintDocumentAdapter] to format the transaction data
 * for printing as a PDF document. The generated PDF will include a summary of
 * total expenses, average expense per person, individual expenses, and the list of
 * balancing transactions.
 *
 * @param context The current [Context], used to access system services like `PrintManager`.
 * @param transactions The list of [Transaction] objects to be printed.
 * @param vm The [ExpenseViewModel] containing relevant data for printing, such as person names and overall expense details.
 */
private fun printTransactionsToPdf(context: Context, transactions: List<Transaction>, vm: ExpenseViewModel) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "${context.getString(R.string.app_name)} Transactions"

    printManager.print(jobName, TransactionsPrintDocumentAdapter(context, transactions, vm), null)
}
/**
 * A [PrintDocumentAdapter] for generating a PDF document of transactions.
 *
 * This adapter handles the layout and writing of transaction data onto a PDF page.
 * It formats the transactions into a readable summary, including sender, receiver, and amount.
 * The PDF also includes a title, date/time stamp, a summary of total and average expenses,
 * individual expenses per person, and page numbers.
 *
 * @property context The application context.
 * @property transactions The list of [Transaction] objects to be printed.
 * @property vm The [ExpenseViewModel] providing access to person data and overall expense summary.
 */
private class TransactionsPrintDocumentAdapter(
    private val context: Context,
    private val transactions: List<Transaction>,
    private val vm: ExpenseViewModel
) : PrintDocumentAdapter() {

    private var pageHeight: Int = 0
    private var pageWidth: Int = 0
    private var myPdfDocument: PdfDocument? = null
    private var totalPages: Int = 1

    /**
     * Called when the layout of the document changes.
     *
     * This method is responsible for:
     * - Initializing a new [PdfDocument] instance for the print job.
     * - Calculating the page height and width in points (1/72 of an inch) based on the
     *   `newAttributes` provided by the print system. The media size is typically obtained in mils
     *   (1/1000 of an inch).
     * - Checking if a cancellation has been signaled via `cancellationSignal`. If so, it invokes
     *   `onLayoutCancelled` on the `callback` and returns.
     * - Setting the total number of pages for the document. Currently, this is fixed at 1.
     * - Creating a [PrintDocumentInfo] object. This object contains metadata about the document,
     *   such as its name ("splitly_transactions.pdf"), content type (document), and the total
     *   page count.
     * - Invoking the `onLayoutFinished` method on the `callback`, passing the created
     *   `PrintDocumentInfo` and a boolean indicating that the content has changed (true in this case,
     *   as layout implies potential changes).
     *
     * @param oldAttributes The previous print attributes. This can be null if this is the first layout pass.
     * @param newAttributes The new print attributes, which dictate the media size and other print settings.
     * @param cancellationSignal A signal to detect cancellation requests from the user or system.
     * @param callback A callback to report the layout result (success, failure, or cancellation).
     * @param extras Additional options, not currently used by this implementation.
     */
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        myPdfDocument = PdfDocument()

        pageHeight = newAttributes.mediaSize!!.heightMils * 72 / 1000
        pageWidth = newAttributes.mediaSize!!.widthMils * 72 / 1000

        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        totalPages = 1

        val info = PrintDocumentInfo.Builder("splitly_transactions.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(totalPages)
            .build()
        callback?.onLayoutFinished(info, true)
    }

    /**
     * Called when the system needs to write the PDF content.
     *
     * This method iterates through the pages requested by the system and draws the content
     * for each page. It handles cancellation requests and reports success or failure
     * to the system via the [WriteResultCallback].
     *
     * The method ensures that only the requested pages are generated and written to the
     * provided [ParcelFileDescriptor]. If a cancellation is signaled, the operation
     * is aborted, and any partially created PDF document is closed and discarded.
     *
     * Upon successful completion of writing all requested pages, the PDF document is
     * finalized and written to the output stream. If any `IOException` occurs during
     * this process, the failure is reported. Finally, the PDF document is closed,
     * and resources are released.
     *
     * @param pages An array of [PageRange] objects specifying which pages to write.
     *              If null, it's assumed all pages are requested.
     * @param destination The [ParcelFileDescriptor] where the PDF content should be written.
     *                    This should not be null for a successful write operation.
     * @param cancellationSignal A [CancellationSignal] to monitor for cancellation requests.
     *                           If null, cancellation is not monitored.
     * @param callback A [WriteResultCallback] to report the outcome of the write operation.
     *                 This should not be null to receive status updates.
     */
    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        for (i in 0 until totalPages) {
            if (pageInRange(pages, i)) {
                val newPage = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create()
                val page = myPdfDocument?.startPage(newPage)

                if (cancellationSignal?.isCanceled == true) {
                    callback?.onWriteCancelled()
                    myPdfDocument?.close()
                    myPdfDocument = null
                    return
                }
                drawPage(page, i)
                myPdfDocument?.finishPage(page)
            }
        }

        try {
            myPdfDocument?.writeTo(FileOutputStream(destination!!.fileDescriptor))
        } catch (e: IOException) {
            callback?.onWriteFailed(e.toString())
            return
        } finally {
            myPdfDocument?.close()
            myPdfDocument = null
        }
        callback?.onWriteFinished(pages)
    }

    /**
     * Checks if a given page number falls within any of the specified page ranges.
     *
     * This function is used by the [onWrite] method to determine if a particular page
     * should be included in the PDF output.
     *
     * @param pageRanges An array of [PageRange] objects. If null (which means all pages
     * are requested), this function will always return `true`.
     * @param page The page number to check (0-indexed).
     * @return `true` if the page is within any of the specified ranges or if `pageRanges` is null,
     * `false` otherwise.
     */
    private fun pageInRange(pageRanges: Array<out PageRange>?, page: Int): Boolean {
        if (pageRanges == null) return true // If null, all pages are considered in range
        for (range in pageRanges) {
            if (page >= range.start && page <= range.end) return true
        }
        return false
    }

    /**
     * Draws the content of a single page in the PDF document.
     *
     * This function is responsible for rendering the transaction summary onto a given PDF page canvas.
     * It includes:
     * - A title "Splitly - Transaction Summary".
     * - The current date and time.
     * - A summary section displaying:
     *     - Total amount spent by all persons.
     *     - Average amount spent per person.
     *     - Amount spent by each individual person. If a person's name is not set, it defaults to "Person X" where X is their ID + 1.
     * - If there are transactions:
     *     - A horizontal line separator.
     *     - Headers for "From", "To", and "Amount".
     *     - Another horizontal line separator.
     *     - A list of transactions, displaying the sender, receiver, and amount for each.
     *     - If a person's name involved in a transaction is not set, it defaults to "Person X" where X is their ID + 1.
     *     - Transactions are added until the page is full (considering a bottom margin).
     * - A footer containing copyright information and the current page number.
     *
     * @param page The [PdfDocument.Page] object representing the page to draw on. If null, the function returns.
     * @param pagenumber The current page number (0-indexed).
     */
    private fun drawPage(page: PdfDocument.Page?, pagenumber: Int) {
        val canvas = page?.canvas ?: return

        var yPosition = 80

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText("Splitly - Transaction Summary", 40f, yPosition.toFloat(), titlePaint)
        yPosition += 40

        val dateTimePaint = Paint().apply { color = Color.GRAY; textSize = 10f }
        val currentDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(Date())
        val dateTimeTextWidth = dateTimePaint.measureText(currentDateTime)
        canvas.drawText(currentDateTime, pageWidth - 40f - dateTimeTextWidth, yPosition - 20f, dateTimePaint)

        val summaryPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        val totalSpent = vm.persons.sumOf { it.amountCents }
        val numPersons = vm.persons.size
        val averagePerPerson = if (numPersons > 0) totalSpent / numPersons else 0

        canvas.drawText("Total spent by all: ${centsToDisplay(totalSpent)}", 40f, yPosition.toFloat(), summaryPaint)
        yPosition += 20

        if (numPersons > 0) {
            canvas.drawText("Average per person: ${centsToDisplay(averagePerPerson)}", 40f, yPosition.toFloat(), summaryPaint)
            yPosition += 20
        }
        yPosition += 5
        vm.persons.forEach { person ->
            val personName = person.name.takeIf { it.isNotBlank() } ?: "Person ${person.id + 1}"
            val personSpentText = "$personName spent: ${centsToDisplay(person.amountCents)}"
            canvas.drawText(personSpentText, 40f, yPosition.toFloat(), summaryPaint)
            yPosition += 20
        }
        yPosition += 10

        if (transactions.isNotEmpty()) {
            canvas.drawLine(40f, yPosition.toFloat(), pageWidth - 40f, yPosition.toFloat(), Paint().apply { color = Color.GRAY })
            yPosition += 20
        val headerPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isFakeBoldText = true
        }
        canvas.drawText("From", 40f, yPosition.toFloat(), headerPaint)
        canvas.drawText("To", 200f, yPosition.toFloat(), headerPaint)
        val amountHeaderText = "Amount"
        val amountHeaderTextWidth = headerPaint.measureText(amountHeaderText)
        canvas.drawText(amountHeaderText, pageWidth - 40f - amountHeaderTextWidth, yPosition.toFloat(), headerPaint)
        yPosition += 25
        canvas.drawLine(40f, yPosition.toFloat(), pageWidth - 40f, yPosition.toFloat(), Paint().apply { color = Color.GRAY })
        }
        yPosition += 20

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        transactions.forEach { transaction ->
            if (yPosition > pageHeight - 60) {
                return@forEach
            }
            val fromPerson = vm.persons.find { it.id == transaction.fromId }
            val fromName = fromPerson?.name?.takeIf { it.isNotBlank() } ?: "Person ${transaction.fromId + 1}"

            val toPerson = vm.persons.find { it.id == transaction.toId }
            val toName = toPerson?.name?.takeIf { it.isNotBlank() } ?: "Person ${transaction.toId + 1}"

            val amountText = centsToDisplay(transaction.amountCents)
            val amountTextWidth = textPaint.measureText(amountText)

            canvas.drawText(fromName, 40f, yPosition.toFloat(), textPaint)
            canvas.drawText(toName, 200f, yPosition.toFloat(), textPaint)
            canvas.drawText(amountText, pageWidth - 40f - amountTextWidth, yPosition.toFloat(), textPaint)
            yPosition += 20
        }

        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
        }
        val copyrightText = "© 2025 Luca Turillo - Splitly"
        canvas.drawText(copyrightText, 40f, pageHeight - 30f, footerPaint)

        val pageNumText = "Page ${pagenumber + 1}"
        val pageNumTextWidth = footerPaint.measureText(pageNumText)
        canvas.drawText(pageNumText, pageWidth - 40f - pageNumTextWidth, pageHeight - 30f, footerPaint)
    }
}

/**
 * Composable function to display a single transaction as a card.
 *
 * This card shows the name of the person paying (or "Person X" if the name is blank),
 * an arrow indicating the direction of payment, the name of the person receiving
 * (or "Person Y" if the name is blank), and the transaction amount.
 * The card has a slight elevation and a specific background color.
 *
 * @param t The [Transaction] object containing the details of the transaction to display.
 *          This includes the `fromId`, `toId`, and `amountCents`.
 * @param vm The [ExpenseViewModel] used to fetch the names of the persons involved in the
 *           transaction based on their IDs.
 */
@Composable
fun TransactionCard(t: Transaction, vm: ExpenseViewModel) {
    val fromPerson = vm.persons.find { it.id == t.fromId }
    val fromPersonName = if (fromPerson != null && fromPerson.name.isNotBlank()) {
        fromPerson.name
    } else {
        "Person ${t.fromId + 1}"
    }
    val toPerson = vm.persons.find { it.id == t.toId }
    val toPersonName = if (toPerson != null && toPerson.name.isNotBlank()) { toPerson.name } else { "Person ${t.toId + 1}" }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlizzardBlue)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$fromPersonName  ➔  $toPersonName", color = androidx.compose.ui.graphics.Color.Black, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f, fill = false))
            Text(text = centsToDisplay(t.amountCents), style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color.Black)
        }
    }
}

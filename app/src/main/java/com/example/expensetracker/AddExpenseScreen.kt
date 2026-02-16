package com.example.expensetracker

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.transaction.TransactionViewModel
import java.util.Date

// Constants to avoid "Magic Strings"
private const val DEFAULT_CATEGORY = "Food"
private const val DEFAULT_PAYMENT = "Cash"
private const val NOTE_PLACEHOLDER = "" // Better to use empty than "Optional"

@Composable
fun AddExpenseScreen(
    viewModel: TransactionViewModel,
    onReset: () -> Unit,
    categories: Set<String> = setOf("Food", "Transport", "Shopping", "Entertainment", "Bills", "Others"),
    onAddCategory: (String) -> Unit = {}
) {
    var amount by remember { mutableStateOf(0.0) }
    val maxAmount = 1e9
    var selectedCategory by remember { mutableStateOf(DEFAULT_CATEGORY) }
    var paymentMethod by remember { mutableStateOf(DEFAULT_PAYMENT) }
    var date by remember { mutableStateOf(Date()) }
    var note by remember { mutableStateOf(NOTE_PLACEHOLDER) }

    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

    fun resetFormAfterSave() {
        amount = 0.0
        selectedCategory = DEFAULT_CATEGORY
        date = Date()
        paymentMethod = DEFAULT_PAYMENT
        note = NOTE_PLACEHOLDER
        onReset()
    }

    fun saveExpenseAction() {
        if (amount <= 0) {
            alertTitle = "Invalid amount"
            alertMessage = "Please enter an amount greater than zero."
            showAlert = true
            return
        }
        try {
            viewModel.addTransaction(
                amount = amount,
                category = selectedCategory,
                note = note.ifBlank { null },
                date = date,
                paymentMethod = paymentMethod ?: "Cash"
            )
            alertTitle = "Saved"
            alertMessage = "Expense saved successfully."
            resetFormAfterSave()
        } catch (e: Exception) {
            alertTitle = "Save Failed"
            alertMessage = e.localizedMessage ?: "Unknown error"
        }
        showAlert = true
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { resetFormAfterSave() }
                ) {
                    Text("Reset")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { saveExpenseAction() }
                ) {
                    Text("Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Divider()

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AmountField(
                    amount = amount,
                    onAmountChange = { amount = it },
                    maxAmount = maxAmount
                )

                CategoryPicker(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = categories.sorted(),
                    onAddCategory = onAddCategory
                )

                DateRow(
                    date = date,
                    onDateChange = { date = it },
                    label = "Date" // Fixed the TODO() here
                )

                PaymentMethodPicker(
                    paymentMethod = paymentMethod,
                    onPaymentChange = { paymentMethod = it ?: DEFAULT_PAYMENT }
                )

                NoteRow(note = note, onNoteChange = { note = it })
            }
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            confirmButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text("OK")
                }
            },
            title = { Text(text = alertTitle) },
            text = { Text(text = alertMessage) }
        )
    }
}

// --- Helper Components ---
@Composable
fun AmountField(amount: Double, onAmountChange: (Double) -> Unit, maxAmount: Double) {
    OutlinedTextField(
        value = if (amount == 0.0) "" else amount.toString(),
        onValueChange = { newValue ->
            val parsed = newValue.toDoubleOrNull() ?: 0.0
            if (parsed <= maxAmount) onAmountChange(parsed)
        },
        label = { Text("Amount") },
        prefix = { Text("$") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPicker(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    onAddCategory: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("+ Add New Category", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                    showAddDialog = true
                }
            )
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank() && newCategoryName !in categories) {
                            onAddCategory(newCategoryName.trim())
                            onCategorySelected(newCategoryName.trim())
                            newCategoryName = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newCategoryName.isNotBlank() && newCategoryName !in categories
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRow(date: Date, onDateChange: (Date) -> Unit, label: String) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = null, indication = null) {
                showDatePicker = true
            }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Text(
            text = date.toString(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateChange(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodPicker(paymentMethod: String?, onPaymentChange: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val methods = listOf("None", "Cash", "Card", "UPI")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = paymentMethod ?: "Cash",
            onValueChange = {},
            readOnly = true,
            label = { Text("Payment Method") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            methods.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method) },
                    onClick = {
                        onPaymentChange(if (method == "None") null else method)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun NoteRow(note: String, onNoteChange: (String) -> Unit) {
    OutlinedTextField(
        value = note,
        onValueChange = { onNoteChange(it) },
        label = { Text("Note (Optional)") },
        placeholder = { Text("Enter details...") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3
    )
}



// for preview

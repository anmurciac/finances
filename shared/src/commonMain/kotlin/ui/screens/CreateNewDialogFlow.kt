package ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class DialogState {
    NONE, SELECT_TYPE, CREATE_ACCOUNT, CREATE_CATEGORY, CREATE_INCOME, CREATE_OUTGOING, LIST_CATEGORIES
}

@Composable
fun DialogFlowScreen(
    cuentaViewModel: CuentaViewModel,
    transaccionViewModel: TransaccionesViewModel,
    categoriaViewModel: CategoriaViewModel
) {
    val cuentaState by cuentaViewModel.state.collectAsState()
    val categoriaState by categoriaViewModel.state.collectAsState()

    var currentDialog by remember { mutableStateOf(DialogState.SELECT_TYPE) }
    var previousDialog by remember { mutableStateOf(DialogState.NONE) }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        if (!cuentaState.isLoaded) cuentaViewModel.loadCuentas()
        if (!categoriaState.isLoaded) categoriaViewModel.loadCategorias()
    }

    when (currentDialog) {
        DialogState.SELECT_TYPE -> CreateNewDialog(
            onDismiss = { currentDialog = DialogState.NONE },
            onSelect = { type ->
                previousDialog = DialogState.SELECT_TYPE
                when (type) {
                    "Account" -> currentDialog = DialogState.CREATE_ACCOUNT
                    "Category" -> currentDialog = DialogState.CREATE_CATEGORY
                    "Income" -> currentDialog = DialogState.CREATE_INCOME
                    "Outgoing" -> currentDialog = DialogState.CREATE_OUTGOING
                }
            }
        )
        DialogState.CREATE_ACCOUNT -> CreateAccountDialog(
            onDismiss = { currentDialog = DialogState.NONE },
            onBack = { currentDialog = previousDialog },
            onCreate = { nombre, saldo ->
                cuentaViewModel.createCuenta(nombre, saldo)
                currentDialog = DialogState.NONE
            }
        )
        DialogState.CREATE_CATEGORY -> CreateCategoryDialog(
            onDismiss = { currentDialog = DialogState.NONE },
            onBack = { currentDialog = previousDialog },
            onCreate = { nombre, tipo ->
                categoriaViewModel.createCategoria(nombre, tipo)
                currentDialog = DialogState.NONE
            },
            onShowCategories = {
                previousDialog = DialogState.CREATE_CATEGORY
                currentDialog = DialogState.LIST_CATEGORIES
            }
        )
        DialogState.CREATE_INCOME -> CreateTransactionDialog(
            isIncome = true,
            cuentaState = cuentaState,
            categoriaState = categoriaState,
            onDismiss = { currentDialog = DialogState.NONE },
            onBack = { currentDialog = previousDialog },
            onCreate = { cuentaId, monto, descripcion, categoriaId, fecha ->
                transaccionViewModel.registrarIngreso(cuentaId, monto, descripcion, categoriaId, fecha)
                currentDialog = DialogState.NONE
            }
        )
        DialogState.CREATE_OUTGOING -> CreateTransactionDialog(
            isIncome = false,
            cuentaState = cuentaState,
            categoriaState = categoriaState,
            onDismiss = { currentDialog = DialogState.NONE },
            onBack = { currentDialog = previousDialog },
            onCreate = { cuentaId, monto, descripcion, categoriaId, fecha ->
                transaccionViewModel.registrarGasto(cuentaId, monto, descripcion, categoriaId, fecha)
                currentDialog = DialogState.NONE
            }
        )
        DialogState.LIST_CATEGORIES -> ListCategoriesDialog(
            categoriaState = categoriaState,
            onDismiss = { currentDialog = DialogState.NONE },
            onBack = { currentDialog = previousDialog }
        )
        DialogState.NONE -> {}
    }
}

@Composable
fun CreateNewDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf("Income") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create new:") },
        text = {
            Column {
                RadioButtonRow("Income", selectedOption) { selectedOption = it }
                RadioButtonRow("Outgoing", selectedOption) { selectedOption = it }
                RadioButtonRow("Category", selectedOption) { selectedOption = it }
                RadioButtonRow("Account", selectedOption) { selectedOption = it }
            }
        },
        confirmButton = {
            Button(onClick = { onSelect(selectedOption) }) {
                Text("Next")
            }
        }
    )
}

@Composable
fun RadioButtonRow(option: String, selectedOption: String, onSelect: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedOption == option,
            onClick = { onSelect(option) }
        )
        Text(option)
    }
}

@Composable
fun CreateAccountDialog(onDismiss: () -> Unit, onBack: () -> Unit, onCreate: (String, BigDecimal) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var saldoInicial by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create new account:") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = saldoInicial,
                    onValueChange = { saldoInicial = it },
                    label = { Text("Initial money on account") }
                )
                Text("This field is optional", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(nombre, saldoInicial.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                    nombre = ""
                    saldoInicial = ""
                },
                enabled = nombre.isNotBlank()
            ) {
                Text("END")
            }
        },
        dismissButton = {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    )
}

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    onCreate: (String, String) -> Unit,
    onShowCategories: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("INGRESO") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create new category:") },
        text = {
            Column {
                Text(
                    "You have %d category already created. Click to see more detail".format(0),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { onShowCategories() }
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Name") }
                )
                Row {
                    RadioButton(
                        selected = tipo == "INGRESO",
                        onClick = { tipo = "INGRESO" }
                    )
                    Text("Income")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = tipo == "GASTO",
                        onClick = { tipo = "GASTO" }
                    )
                    Text("Outgoing")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(nombre, tipo)
                    nombre = ""
                },
                enabled = nombre.isNotBlank()
            ) {
                Text("END")
            }
        },
        dismissButton = {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionDialog(
    isIncome: Boolean,
    cuentaState: CuentaViewState,
    categoriaState: CategoriaViewState,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    onCreate: (String, BigDecimal, String, String, LocalDateTime) -> Unit
) {
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedCuentaId by remember { mutableStateOf(cuentaState.cuentas.firstOrNull()?.id) }
    var selectedCategoriaId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDateTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val filteredCategorias = categoriaState.categorias.filter { it.tipo == if (isIncome) "INGRESO" else "GASTO" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create new ${if (isIncome) "income" else "outgoing"}:") },
        text = {
            Column {
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Amount") }
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    onValueChange = { /* Read-only */ },
                    label = { Text("Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                        }
                    }
                )
                var expandedCategoria by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expandedCategoria = true }) {
                        Text(selectedCategoriaId?.let { "Category: ${filteredCategorias.find { it.id == selectedCategoriaId }?.nombre}" } ?: "Select a category")
                    }
                    DropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        filteredCategorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    selectedCategoriaId = categoria.id
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }
                var expandedCuenta by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expandedCuenta = true }) {
                        Text(selectedCuentaId?.let { "Account: ${cuentaState.cuentas.find { it.id == selectedCuentaId }?.name}" } ?: "Select an account")
                    }
                    DropdownMenu(
                        expanded = expandedCuenta,
                        onDismissRequest = { expandedCuenta = false }
                    ) {
                        cuentaState.cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = { Text(cuenta.name) },
                                onClick = {
                                    selectedCuentaId = cuenta.id
                                    expandedCuenta = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCuentaId != null && selectedCategoriaId != null) {
                        onCreate(
                            selectedCuentaId!!,
                            monto.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                            descripcion,
                            selectedCategoriaId!!,
                            selectedDate
                        )
                        monto = ""
                        descripcion = ""
                        selectedCategoriaId = null
                    }
                },
                enabled = monto.isNotBlank() && selectedCuentaId != null && selectedCategoriaId != null
            ) {
                Text("END")
            }
        },
        dismissButton = {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = LocalDate.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
                        selectedDate = localDate.atStartOfDay() // Medianoche por defecto
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

@Composable
fun ListCategoriesDialog(categoriaState: CategoriaViewState, onDismiss: () -> Unit, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Incomes") }
    val filteredCategorias = categoriaState.categorias.filter { it.tipo == if (selectedTab == "Incomes") "INGRESO" else "GASTO" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("List of category") },
        text = {
            Column {
                Row {
                    TextButton(onClick = { selectedTab = "Incomes" }, enabled = selectedTab != "Incomes") {
                        Text("Incomes")
                    }
                    TextButton(onClick = { selectedTab = "Outgoings" }, enabled = selectedTab != "Outgoings") {
                        Text("Outgoings")
                    }
                }
                Column {
                    filteredCategorias.forEach { categoria ->
                        Text(categoria.nombre)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    )
}

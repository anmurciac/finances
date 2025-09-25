package ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.TransactionCard
import ui.components.TransactionData
import viewmodel.CategoriaViewState
import viewmodel.CuentaViewState
import viewmodel.TransaccionViewState
import viewmodel.TransaccionesViewModel

//Pass if cuentas.isNotEmpty as existData
@Composable
fun HomeScreen(existData: Boolean, modifier: Modifier = Modifier) {
    if(!existData) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "To start, click on +",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    } else {
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            Text(
                "Welcome to Mis Finanzas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun TransactionsScreen(
    type: String,
    cuentaState: CuentaViewState,
    transaccionsState: TransaccionViewState,
    selectedCuentaId: String?,
    onCuentaSelected: (String) -> Unit,
    transaccionViewModel: TransaccionesViewModel,
    categoriaState: CategoriaViewState,
    existData: Boolean,
    modifier: Modifier = Modifier
) {
    var filterType by remember { mutableStateOf("Date") }
    var expandedFilter by remember { mutableStateOf(false) }
    var expandedCuenta by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDeleteId by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (existData) {
            Text(
                text = "There are not available accounts. Create one first",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Selector de cuentas
            Box {
                OutlinedTextField(
                    value = selectedCuentaId?.let { "Account: ${cuentaState.cuentas.find { it.id == selectedCuentaId }?.name}" } ?: "Select Account",
                    onValueChange = {},
                    label = { Text("Account") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { expandedCuenta = true })
                                   },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCuenta = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                DropdownMenu(
                    expanded = expandedCuenta,
                    onDismissRequest = { expandedCuenta = false }
                ) {
                    cuentaState.cuentas.forEach { cuenta ->
                        DropdownMenuItem(
                            text = { Text(cuenta.name) },
                            onClick = {
                                onCuentaSelected(cuenta.id)
                                expandedCuenta = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de filtro
            Box {
                OutlinedTextField(
                    value = filterType,
                    onValueChange = {},
                    label = { Text("Filter by") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expandedFilter = true }) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFilter = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                DropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { expandedFilter = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Date") },
                        onClick = {
                            filterType = "Date"
                            expandedFilter = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Category") },
                        onClick = {
                            filterType = "Category"
                            expandedFilter = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredTransactions = transaccionsState.transacciones.filter { transaction ->
                transaction.tipo == type
            }
            val sortedTransactions = if (filterType == "Date") {
                filteredTransactions.sortedByDescending { it.fecha }
            } else {
                filteredTransactions.sortedBy { it.categoria }
            }


            LazyColumn {
                items(sortedTransactions, key = { it.id }) { transaction ->
                    TransactionCard(
                        transaction = TransactionData(
                            id = transaction.id,
                            amount = transaction.monto,
                            date = transaction.fecha,
                            category = transaction.categoria,
                            description = transaction.descripcion
                        ),
                        onClickEdit = { /* ... */ },
                        onRequestDelete = {
                            transactionToDeleteId = transaction.id
                            showDeleteDialog = true
                        },
                        showDeleteDialog = showDeleteDialog && transactionToDeleteId == transaction.id,
                        onConfirmDelete = {
                            showDeleteDialog = false
                            transactionToDeleteId?.let {
                                transaccionViewModel.eliminarTransaccion(it)
                                transaccionViewModel.loadTransacciones(cuentaId = selectedCuentaId ?: "")
                            }
                            transactionToDeleteId = null
                        },
                        onDismissDelete = {
                            showDeleteDialog = false
                            transactionToDeleteId = null
                        }
                    )
                }
            }
        }
    }
}
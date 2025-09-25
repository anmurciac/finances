package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.AppTheme
import viewmodel.AuthViewModel
import viewmodel.CategoriaViewModel
import viewmodel.CuentaViewModel
import viewmodel.TransaccionesViewModel

data class NavItem(val icon: ImageVector, val label: String, val route: String)


val navItems = listOf(
    NavItem(Icons.Default.Home, "Home", "home"),
    NavItem(Icons.Default.ArrowUpward, "Incomes", "incomes"),
    NavItem(Icons.Default.ArrowDownward, "Outgoings", "outgoings"),
    NavItem(Icons.Default.Add, "Create new...", "create_new")
)

@Composable
fun TestApp() {
    val authViewModel = remember { AuthViewModel() }
    val authState by authViewModel.state.collectAsState()
    val cuentaViewModel = remember { CuentaViewModel() }
    val transaccionViewModel = remember { TransaccionesViewModel() }
    val categoriaViewModel = remember { CategoriaViewModel() }

    AppTheme {
        if(authState.isAuthenticated) {
            MainScreen(cuentaViewModel, transaccionViewModel, categoriaViewModel)
        } else {
            LoginScreen(authViewModel, onAuthenticated = {})
        }
    }
}


//Remake
@Composable
fun AppNavigationRail(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    NavigationRail(
        modifier = modifier
            .width(if (expanded) 180.dp else 72.dp) // cambia el ancho cuando está expandido
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        header = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = if (expanded) "Cerrar menú" else "Abrir menú",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) {
        navItems.forEach { item ->
            NavigationRailItem(
                selected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = if (expanded) {
                    { Text(item.label, color = MaterialTheme.colorScheme.onSurface) }
                } else null
            )
        }
    }
}

@Composable
fun MainContent(
    cuentaViewModel: CuentaViewModel,
    transaccionViewModel: TransaccionesViewModel,
    categoriaViewModel: CategoriaViewModel,
    selectedItem: String,
    modifier: Modifier = Modifier
) {
    val cuentaState by cuentaViewModel.state.collectAsState()
    val transaccionsState by transaccionViewModel.state.collectAsState()
    val categoriaState by categoriaViewModel.state.collectAsState()

    var selectedCuentaId by remember { mutableStateOf<String?>(null) }

    // Carga las cuentas sólo una vez
    LaunchedEffect(Unit) {
        if (!cuentaState.isLoaded) {
            cuentaViewModel.loadCuentas()
        }
    }

    LaunchedEffect(cuentaState.cuentas) {
        if (cuentaState.cuentas.isNotEmpty()) {
            if (selectedCuentaId == null) {
                selectedCuentaId = cuentaState.cuentas.firstOrNull()?.id
                selectedCuentaId?.let { transaccionViewModel.loadTransacciones(it) }
            }
        }
    }

    val existData = cuentaState.cuentas.isEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when (selectedItem) {
            "home" -> HomeScreen(existData)
            "incomes" -> TransactionsScreen(
                type = "INGRESO",
                cuentaState = cuentaState,
                transaccionsState = transaccionsState,
                selectedCuentaId = selectedCuentaId,
                onCuentaSelected = { selectedCuentaId = it; transaccionViewModel.loadTransacciones(it) },
                transaccionViewModel = transaccionViewModel,
                categoriaState = categoriaState,
                existData = existData
            )
            "outgoings" -> TransactionsScreen(
                type = "GASTO",
                cuentaState = cuentaState,
                transaccionsState = transaccionsState,
                selectedCuentaId = selectedCuentaId,
                onCuentaSelected = { selectedCuentaId = it; transaccionViewModel.loadTransacciones(it) },
                transaccionViewModel = transaccionViewModel,
                categoriaState = categoriaState,
                existData = existData
            )
            //TODO: Revisar por qué se crean dos gastos.
            "create_new" -> DialogFlowScreen(cuentaViewModel, transaccionViewModel, categoriaViewModel)
        }
    }
}


@Composable
fun MainScreen(
    cuentaViewModel: CuentaViewModel,
    transaccionesViewModel: TransaccionesViewModel,
    categoriaViewModel: CategoriaViewModel
) {
    var selectedItem by remember { mutableStateOf("home") }

    Row(Modifier.fillMaxSize()){
        AppNavigationRail(
            selectedItem = selectedItem,
            onItemSelected = {selectedItem = it}
        )
        MainContent(
            cuentaViewModel,
            transaccionesViewModel,
            categoriaViewModel,
            selectedItem,
            modifier = Modifier.weight(1f)
        )
    }
}
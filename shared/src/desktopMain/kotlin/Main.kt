
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.screens.TestApp
import ui.theme.AppTheme
import viewmodel.AuthManager
import viewmodel.AuthViewModel
import viewmodel.CategoriaViewModel


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Finances") {
        AppTheme {
            TestApp()
        }
    }
}


@Composable
fun App() {
    val authViewModel = remember { AuthViewModel() }
    val categoriaViewModel = remember { CategoriaViewModel() }
    val isAuthenticated by AuthManager.isAuthenticated.collectAsState()

    if(isAuthenticated) {
        CategoriaScreen(categoriaViewModel)  {
            authViewModel.logout()
        }
    } else {
//        LoginScreen(authViewModel) {
//            email, password ->
//            authViewModel.login(email, password)
//        }
    }
}

@Composable
fun CategoriaScreen(viewModel: CategoriaViewModel, onLogout: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Column {
        if (state.isLoading) {

            CircularProgressIndicator()
        } else if (state.errorMessage != null) {
            Text("Error: ${state.errorMessage}")
        } else {
            LazyColumn {
                viewModel.loadCategorias()
                items(state.categorias) { categoria ->
                    Text("${categoria.nombre} - ${categoria.tipo}")
                }
            }
            Button(onClick = onLogout) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLogin: ((String, String) -> Unit)) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.errorMessage != null) {
            Text("Error: ${state.errorMessage}")
        }
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Button(onClick = { onLogin(email, password) }) {
            Text("Iniciar Sesión")
        }
    }
}
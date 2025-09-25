package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.CreateNewUser
import ui.components.Login
import viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var isLoginVisible by remember { mutableStateOf(true) }

    // Reiniciar campos al cambiar de componente
    LaunchedEffect(isLoginVisible) {
        if (isLoginVisible) {
            viewModel.email = ""
            viewModel.password = ""
        } else {
            viewModel.name = ""
            viewModel.email = ""
            viewModel.password = ""
        }
    }

    // Navegar si se autentica
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthenticated()
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else if (isLoginVisible) {
                Login(
                    email = viewModel.email,
                    password = viewModel.password,
                    isError = state.errorMessage != null,
                    onValueChangeEmail = { viewModel.email = it },
                    onValueChangePassword = { viewModel.password = it },
                    onClickContinue = { viewModel.login() },
                    onClickRegister = { isLoginVisible = false },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                CreateNewUser(
                    name = viewModel.name,
                    email = viewModel.email,
                    password = viewModel.password,
                    isError = state.errorMessage != null,
                    onValueChangeName = { viewModel.name = it },
                    onValueChangeEmail = { viewModel.email = it },
                    onValueChangePassword = { viewModel.password = it },
                    onClickSendRegister = { viewModel.register() },
                    onClickSignIn = { isLoginVisible = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

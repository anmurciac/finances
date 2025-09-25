package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import viewmodel.ApiClient.BASE_URL

@Serializable
data class LoginRequest(val email: String, val password: String)
@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)
@Serializable
data class AuthResponse(val token: String, val userId: String)

data class AuthViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authResponse: AuthResponse? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _state = MutableStateFlow(AuthViewState())
    val state: StateFlow<AuthViewState> = _state

    // Variables de estado para los campos de entrada
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")

    fun login() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = ApiClient.client.post("${BASE_URL}/api/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(email, password))
                }
                if (response.status.value in 200..299) {
                    val authResponse = response.body<AuthResponse>()
                    AuthManager.setToken(authResponse.token)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        authResponse = authResponse,
                        isAuthenticated = true,
                        errorMessage = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}",
                        isAuthenticated = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}",
                    isAuthenticated = false
                )
            }
        }
    }

    fun register() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val registerRequest = RegisterRequest(name, email, password)
                val response = ApiClient.client.post("${BASE_URL}/api/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(registerRequest)
                }
                if (response.status.value in 200..299) {
                    val authResponse = response.body<AuthResponse>()
                    // Intenta login automático después de registrar
                    val loginResponse = ApiClient.client.post("${BASE_URL}/api/auth/login") {
                        contentType(ContentType.Application.Json)
                        setBody(LoginRequest(email, password))
                    }
                    if (loginResponse.status.value in 200..299) {
                        val loginAuthResponse = loginResponse.body<AuthResponse>()
                        AuthManager.setToken(authResponse.token)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            authResponse = loginAuthResponse,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Registro exitoso, pero fallo al iniciar sesión: ${loginResponse.status.description}",
                            isAuthenticated = false
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}",
                        isAuthenticated = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar: ${e.message}",
                    isAuthenticated = false
                )
            }
        }
    }

    fun logout() {
        scope.launch {
            _state.value = _state.value.copy(
                isAuthenticated = false,
                authResponse = null
            )
            AuthManager.clearToken()
            email = ""
            password = ""
            name = ""
        }
    }
}
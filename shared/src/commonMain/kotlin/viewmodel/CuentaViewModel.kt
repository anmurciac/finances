package viewmodel

import androidx.lifecycle.ViewModel
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import viewmodel.ApiClient.BASE_URL
import java.math.BigDecimal


@Serializable
data class CuentaDTO(
    val id: String,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val saldo: BigDecimal
)

@Serializable
data class CuentaRequest(
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val saldo: BigDecimal?
)

data class CuentaViewState(
    val cuentas: List<CuentaDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)

class CuentaViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(CuentaViewState())
    val state: StateFlow<CuentaViewState> = _state

    // Cargar todas las cuentas
    fun loadCuentas() {
        scope.launch {
            val token = AuthManager.token.value
            if (token == null) {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.get("${BASE_URL}/api/cuentas") {
                    header("Authorization", "Bearer $token")
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        cuentas = emptyList(),
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    val result: List<CuentaDTO> = response.body()
                    _state.value = _state.value.copy(
                        cuentas = result,
                        isLoading = false,
                        isLoaded = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar cuentas: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun createCuenta(nombre: String, saldoInicial: BigDecimal?) {
        scope.launch {
            val token = AuthManager.token.value
            if (token == null) {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.post("${BASE_URL}/api/cuentas") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(CuentaRequest(nombre, saldoInicial))
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    val nuevaCuenta = response.body<CuentaDTO>()
                    _state.value = _state.value.copy(
                        cuentas = _state.value.cuentas + nuevaCuenta,
                        isLoading = false,
                        isLoaded = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al crear cuenta: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun updateCuenta(id: String, nombre: String, saldo: BigDecimal) {
        scope.launch {
            val token = AuthManager.token.value
            if (token == null) {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.put("${BASE_URL}/api/cuentas/$id") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(CuentaRequest(nombre, saldo))
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    val cuentaActualizada = response.body<CuentaDTO>()
                    _state.value = _state.value.copy(
                        cuentas = _state.value.cuentas.map { if (it.id == id) cuentaActualizada else it },
                        isLoading = false,
                        isLoaded = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al actualizar cuenta: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun deleteCuenta(id: String) {
        scope.launch {
            val token = AuthManager.token.value
            if (token == null) {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.delete("${BASE_URL}/api/cuentas/$id") {
                    header("Authorization", "Bearer $token")
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        cuentas = _state.value.cuentas.filter { it.id != id },
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        cuentas = _state.value.cuentas.filter { it.id != id },
                        isLoading = false,
                        isLoaded = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar cuenta: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }
}
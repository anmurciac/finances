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
import java.time.LocalDateTime

@Serializable
data class TransaccionDTO(
    val id: String,
    @Serializable(with = BigDecimalSerializer::class)
    val monto: BigDecimal,
    val descripcion: String,
    val categoria: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fecha: LocalDateTime,
    val tipo: String,
)

@Serializable
data class TransaccionRequest(
    val cuentaId: String,
    @Serializable(with = BigDecimalSerializer::class)
    val monto: BigDecimal,
    val descripcion: String,
    val categoriaId: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fecha: LocalDateTime
)

data class TransaccionViewState(
    val transacciones: List<TransaccionDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)

class TransaccionesViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(TransaccionViewState())
    val state: StateFlow<TransaccionViewState> = _state

    fun loadTransacciones(cuentaId: String?) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(
                    errorMessage = "No autenticado",
                    isLoading = false
                )
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.get("${BASE_URL}/api/transacciones") {
                    header("Authorization", "Bearer $token")
                    url {
                        if (cuentaId != null) {
                            parameters.append("cuenta", cuentaId)
                        }
                    }
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        transacciones = emptyList(),
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    val result: List<TransaccionDTO> = response.body()
                    _state.value = _state.value.copy(
                        transacciones = result,
                        isLoading = false,
                        isLoaded = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar transacciones: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun registrarIngreso(cuentaId: String, monto: BigDecimal, descripcion: String, categoriaId: String, fecha: LocalDateTime) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val request = TransaccionRequest(cuentaId, monto, descripcion, categoriaId, fecha)
                val response: HttpResponse = ApiClient.client.post("${BASE_URL}/api/transacciones/ingresos") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoaded = true
                    )
                } else if (response.status.value in 200..299) {
                    val nuevaTransaccion = response.body<TransaccionDTO>()
                    _state.value = _state.value.copy(
                        transacciones = _state.value.transacciones + nuevaTransaccion,
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}",
                        isLoaded = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar ingreso: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun registrarGasto(cuentaId: String, monto: BigDecimal, descripcion: String, categoriaId: String, fecha: LocalDateTime) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val request = TransaccionRequest(cuentaId, monto, descripcion, categoriaId, fecha)
                val response: HttpResponse = ApiClient.client.post("${BASE_URL}/api/transacciones/gastos") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoaded = true
                    )
                } else if (response.status.value in 200..299) {
                    val nuevaTransaccion = response.body<TransaccionDTO>()
                    _state.value = _state.value.copy(
                        transacciones = _state.value.transacciones + nuevaTransaccion,
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}",
                        isLoaded = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar gasto: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun editarTransaccion(id: String, cuentaId: String, monto: BigDecimal, descripcion: String, categoriaId: String, fecha: LocalDateTime) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val request = TransaccionRequest(cuentaId, monto, descripcion, categoriaId, fecha)
                val response: HttpResponse = ApiClient.client.put("${BASE_URL}/api/transacciones/$id") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                if (response.status == HttpStatusCode.NoContent) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoaded = true
                    )
                } else if (response.status.value in 200..299) {
                    val transaccionActualizada = response.body<TransaccionDTO>()
                    _state.value = _state.value.copy(
                        transacciones = _state.value.transacciones.map { if (it.id == id) transaccionActualizada else it },
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}",
                        isLoaded = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al editar transacción: ${e.message}",
                    isLoaded = false
                )
            }
        }
    }

    fun eliminarTransaccion(id: String) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response: HttpResponse = ApiClient.client.delete("${BASE_URL}/api/transacciones/$id") {
                    header("Authorization", "Bearer $token")
                }
                if (response.status == HttpStatusCode.NoContent) {
                    // Eliminar de la lista local
                    _state.value = _state.value.copy(
                        transacciones = _state.value.transacciones.filter { it.id != id },
                        isLoading = false,
                        isLoaded = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error del servidor: ${response.status.description}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar transacción: ${e.message}"
                )
            }
        }
    }
}
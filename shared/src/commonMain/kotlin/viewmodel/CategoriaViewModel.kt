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

//TODO: Empaquetar todos los estados en una data class
@Serializable
data class CategoriaDTO(val id: String, val nombre: String, val tipo: String)

@Serializable
data class CategoriaRequest(val nombre: String, val tipo: String)

data class CategoriaViewState(
    val categorias: List<CategoriaDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)

class CategoriaViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(CategoriaViewState())
    val state: StateFlow<CategoriaViewState> = _state

    fun loadCategorias() {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false, isLoaded = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val result: List<CategoriaDTO> = ApiClient.client.get("${BASE_URL}/api/categorias") {
                    header("Authorization", "Bearer $token")
                }.body()
                _state.value = _state.value.copy(categorias = result, isLoading = false, isLoaded = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Error al cargar categorías: ${e.message}", isLoaded = false)
            }
        }
    }

    fun createCategoria(nombre: String, tipo: String) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                val response: HttpResponse = ApiClient.client.post("${BASE_URL}/api/categorias/crear") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(CategoriaRequest(nombre, tipo))
                }
                val nuevaCategoria = response.body<CategoriaDTO>()
                _state.value = _state.value.copy(
                    categorias = _state.value.categorias + nuevaCategoria,
                    isLoading = false,
                    isLoaded = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Error al crear categoría: ${e.message}", isLoaded = false)
            }
        }
    }

    fun deleteCategoria(id: String) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoaded = false)
            try {
                ApiClient.client.delete("${BASE_URL}/api/categorias/$id") {
                    header("Authorization", "Bearer $token")
                }
                _state.value = _state.value.copy(
                    categorias = _state.value.categorias.filter { it.id != id },
                    isLoading = false,
                    isLoaded = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Error al eliminar categoría: ${e.message}", isLoaded = false)
            }
        }
    }
}
package viewmodel

import androidx.lifecycle.ViewModel
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import viewmodel.ApiClient.BASE_URL


data class  BalancesViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val balanceMensual: BalanceResponse? = null
)
@Serializable
data class BalanceResponse(val ingresos: String, val gastos: String, val balance: String)
class BalanceViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(BalancesViewState())

    val state: StateFlow<BalancesViewState> = _state

    fun loadBalanceMensual(year: Int, month: Int) {
        scope.launch {
            val token = AuthManager.token.value ?: run {
                _state.value = _state.value.copy(errorMessage = "No autenticado", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val result = ApiClient.client.get("${BASE_URL}/api/balances/mensual") {
                   header("Authorization", "Bearer $token")
                    parameter("year", year)
                    parameter("month", month)
                }

            } catch (e: Exception) {
               _state.value = _state.value.copy(isLoading = false, errorMessage = "Error al cargar balance: ${e.message}")
            }
        }
    }

}
package viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AuthManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    fun setToken(newToken: String?) {
        _token.value = newToken
        _isAuthenticated.value = newToken != null
    }
    fun clearToken() {
        _token.value = null
        _isAuthenticated.value = false
    }
}
package com.sesac.common.ui_state

import com.sesac.domain.model.User

sealed interface JoinUiState {
    object Idle : JoinUiState
    object Loading : JoinUiState
    data class Success(val message: String) : JoinUiState
    data class Error(val message: String) : JoinUiState
}

sealed class ResponseUiState<out T> {
    object Idle : ResponseUiState<Nothing>()
    object Loading : ResponseUiState<Nothing>()
    data class Success<T>(val message: String, val result: T) : ResponseUiState<T>()
    data class Error(val message: String) : ResponseUiState<Nothing>()
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val token: String? = null,
    val user: User? = null,
) {
    companion object {
        val EMPTY = AuthUiState(
            isLoggedIn = true,
            token = "",
            user = User.EMPTY
        )
    }
    fun reset() = AuthUiState()
}
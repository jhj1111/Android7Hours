package com.sesac.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.domain.model.Coord
import com.sesac.common.ui_state.AuthUiState
import com.sesac.domain.result.LocationFlowResult
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.usecase.location.LocationUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val locationUseCase: LocationUseCase,
) : ViewModel() {

    val uiState: StateFlow<AuthUiState> = combine(
        sessionUseCase.getAccessToken(),
        sessionUseCase.getUserInfo()
    ) { token, user ->
        Log.d("CommonViewModel", "Combining user info: $user")
        if (token != null && user != null) {
            AuthUiState(
                isLoggedIn = true,
                token = token,
                user = user,
//                id = user.id,
//                nickname = user.nickname,
//                fullName = user.fullName,
//                email = user.email,
//                profileImageUrl = user.profileImageUrl,
            )
        } else {
            AuthUiState().reset()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthUiState()
    )

    private val _isLocationServiceRunning = MutableStateFlow(false)
    val isLocationServiceRunning = _isLocationServiceRunning.asStateFlow()

    fun updateLocationServiceState(isRunning: Boolean) {
        _isLocationServiceRunning.value = isRunning
    }

    private val _initialLocation = MutableStateFlow<ResponseUiState<Coord?>>(ResponseUiState.Idle)
    val initialLocation = _initialLocation.asStateFlow()

    fun fetchInitialLocation() {
        viewModelScope.launch {
            locationUseCase.getCurrentLocationUseCase().collectLatest { result ->
                when (result) {
                    is LocationFlowResult.Success -> {
                        _initialLocation.value = ResponseUiState.Success("Initial location fetched", result.coord)
                        Log.d("CommonViewModel", "Initial location fetched: ${result.coord}")
                    }
                    is LocationFlowResult.Error -> {
                        _initialLocation.value = ResponseUiState.Error(result.exception.message ?: "Unknown location error")
                        Log.e("CommonViewModel", "Initial location fetch error: ${result.exception.message}")
                    }
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            uiState.value.reset()
            sessionUseCase.clearSession()
        }
    }
}

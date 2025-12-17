package com.sesac.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.domain.model.BannerData
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthResult
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.usecase.home.HomeUseCase
import com.sesac.domain.usecase.path.PathUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pathUseCase: PathUseCase,
    private val homeUseCase: HomeUseCase,
): ViewModel() {
    private val _recommendPathList = MutableStateFlow<ResponseUiState<List<Path?>>>(ResponseUiState.Idle)
    val recommendPathList get() = _recommendPathList.asStateFlow()
    private val _bannerList = MutableStateFlow<List<BannerData?>>(emptyList())
    val bannerList get() = _bannerList.asStateFlow()

    init {
        viewModelScope.launch {
            homeUseCase.getAllBannersUseCase().collectLatest { _bannerList.value = it }
            getRecommendedPaths()
        }
    }

    fun getRecommendedPaths() {
        viewModelScope.launch {
            pathUseCase.getAllRecommendedPathsUseCase(null, null).collectLatest { result ->
                when(result) {
                    is AuthResult.Success -> _recommendPathList.value = ResponseUiState.Success("산책로 불러오기 성공", result.resultData)
                    is AuthResult.NetworkError -> _recommendPathList.value = ResponseUiState.Error(result.exception.message ?: "unknown")
                    else -> {}
                }
            }
        }
    }

}
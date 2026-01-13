package com.sesac.trail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.common.model.UiEvent
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Comment
import com.sesac.domain.model.Place
import com.sesac.domain.result.AuthResult
import com.sesac.domain.type.CommentType
import com.sesac.domain.usecase.comment.CommentUseCase
import com.sesac.domain.usecase.place.PlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val placeUseCases: PlaceUseCase,
    private val commentUseCase: CommentUseCase,
) : ViewModel() {

    private val _invalidToken = Channel<UiEvent>()
    val invalidToken = _invalidToken.receiveAsFlow()

    // =================================================================
    // 📌 1. 장소 목록 관리
    // =================================================================

    private val _placesState = MutableStateFlow<ResponseUiState<List<Place>>>(ResponseUiState.Idle)
    val placesState: StateFlow<ResponseUiState<List<Place>>> = _placesState

    fun loadPlaces(
        categoryId: Int? = null,
        lat: Double? = null,
        lng: Double? = null,
        radius: Int? = 5000
    ) {
        viewModelScope.launch {
            _placesState.value = ResponseUiState.Loading
            placeUseCases.getPlaceUseCase(
                categoryId = categoryId,
                latitude = lat,
                longitude = lng,
                radius = radius
            ).catch { e ->
                _placesState.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }.collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _placesState.value = ResponseUiState.Success("장소를 불러왔습니다.", result.resultData)
                    }
                    is AuthResult.NetworkError -> {
                        _placesState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                    }
                    else -> {}
                }
            }
        }
    }

    // =================================================================
    // 📌 2. 장소 댓글 관리
    // =================================================================

    private val _commentsState =
        MutableStateFlow<ResponseUiState<List<Comment>>>(ResponseUiState.Idle)
    val commentsState: StateFlow<ResponseUiState<List<Comment>>> = _commentsState

    fun loadPlaceComments(placeId: Int) {
        viewModelScope.launch {
            _commentsState.value = ResponseUiState.Loading
            commentUseCase.getCommentsUseCase(
                objectId = placeId,
                type = CommentType.PATH
            )
                .catch { e ->
                    _commentsState.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _commentsState.value = ResponseUiState.Success("댓글을 불러왔습니다.", result.resultData)
                        }
                        is AuthResult.NetworkError -> {
                            _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        }
                        else -> {}
                    }
                }
        }
    }

    fun postPlaceComment(uiState: AuthUiState, placeId: Int, content: String, type: CommentType) {
        viewModelScope.launch {
            val token = uiState.token
            if (token == null) {
                _invalidToken.send(UiEvent.ToastEvent("로그인이 필요합니다."))
                return@launch
            }

            commentUseCase.createCommentUseCase(
                token = token,
                objectId = placeId,
                content = content,
                type = type
            ).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        loadPlaceComments(placeId)
                    }
                    is AuthResult.NetworkError -> {
                        _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                    }
                    else -> {}
                }
            }
        }
    }

    fun updatePlaceComment(uiState: AuthUiState, placeId: Int, commentId: Int, content: String, type: CommentType) {
        viewModelScope.launch {
            val token = uiState.token
            if (token == null) {
                _invalidToken.send(UiEvent.ToastEvent("로그인이 필요합니다."))
                return@launch
            }

            commentUseCase.updateCommentUseCase(
                token = token,
                objectId = placeId,
                commentId = commentId,
                content = content,
                type = type
            ).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        loadPlaceComments(placeId)
                    }
                    is AuthResult.NetworkError -> {
                        _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                    }
                    else -> {}
                }
            }
        }
    }

    fun deletePlaceComment(uiState: AuthUiState, placeId: Int, commentId: Int, type: CommentType) {
        viewModelScope.launch {
            val token = uiState.token
            if (token == null) {
                _invalidToken.send(UiEvent.ToastEvent("로그인이 필요합니다."))
                return@launch
            }

            commentUseCase.deleteCommentUseCase(
                token = token,
                objectId = placeId,
                commentId = commentId,
                type = type
            ).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        loadPlaceComments(placeId)
                    }
                    is AuthResult.NetworkError -> {
                        _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                    }
                    else -> {}
                }
            }
        }
    }
}
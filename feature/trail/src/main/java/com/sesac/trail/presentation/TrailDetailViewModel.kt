package com.sesac.trail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.BookmarkedPath
import com.sesac.domain.model.Comment
import com.sesac.domain.model.Path
import com.sesac.domain.model.User
import com.sesac.domain.result.AuthResult
import com.sesac.domain.type.BookmarkType
import com.sesac.domain.type.CommentType
import com.sesac.domain.usecase.bookmark.BookmarkUseCase
import com.sesac.domain.usecase.comment.CommentUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailDetailViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val bookmarkUseCase: BookmarkUseCase,
    private val commentUseCase: CommentUseCase,
) : ViewModel() {

    // =================================================================
    // 📌 1. 선택된 경로 & 사용자 정보
    // =================================================================

    private val _selectedPath = MutableStateFlow<Path?>(null)
    val selectedPath get() = _selectedPath.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun updateSelectedPath(path: Path?) {
        viewModelScope.launch {
            _selectedPath.value = path
        }
    }

    fun clearSelectedPath() {
        viewModelScope.launch {
            _selectedPath.value = null
        }
    }

    fun getCurrentUserInfo() {
        viewModelScope.launch {
            _userInfo.value = sessionUseCase.getUserInfo().first()
        }
    }

    // =================================================================
    // 📌 2. 북마크 관리
    // =================================================================

    private val _bookmarkedPaths =
        MutableStateFlow<ResponseUiState<List<BookmarkedPath>>>(ResponseUiState.Idle)
    val bookmarkedPaths = _bookmarkedPaths.asStateFlow()

    fun getUserBookmarkedPaths(token: String?) {
        viewModelScope.launch {
            _bookmarkedPaths.value = ResponseUiState.Loading
            if (token == null) {
                _bookmarkedPaths.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            bookmarkUseCase.getMyBookmarksUseCase(token)
                .catch { e ->
                    _bookmarkedPaths.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { bookmarksResult ->
                    when (bookmarksResult) {
                        is AuthResult.Success -> {
                            val pathList = bookmarksResult.resultData.mapNotNull { it.bookmarkedItem as? BookmarkedPath }
                            _bookmarkedPaths.value = ResponseUiState.Success("북마크를 불러왔습니다.", pathList)
                        }
                        is AuthResult.NetworkError -> {
                            _bookmarkedPaths.value = ResponseUiState.Error(bookmarksResult.exception.message ?: "unknown")
                        }
                        else -> {}
                    }
                }
        }
    }

    fun toggleBookmark(token: String?, id: Int) {
        viewModelScope.launch {
            if (token == null) {
                Log.e("TrailDetailViewModel", "Toggle bookmark failed: token is null")
                return@launch
            }
            bookmarkUseCase.toggleBookmarkUseCase(token, id, BookmarkType.PATH)
                .collectLatest { bookmarkResponse ->
                    if (bookmarkResponse is AuthResult.Success) {
                        getUserBookmarkedPaths(token)
                        _selectedPath.value = _selectedPath.value?.copy(bookmarkCount = bookmarkResponse.resultData.bookmarkCount)
                    } else if (bookmarkResponse is AuthResult.NetworkError) {
                        Log.e("TrailDetailViewModel", "Toggle bookmark failed: ${bookmarkResponse.exception}")
                    }
                }
        }
    }

    // =================================================================
    // 📌 3. 댓글 관리
    // =================================================================

    private val _commentsState =
        MutableStateFlow<ResponseUiState<List<Comment>>>(ResponseUiState.Idle)
    val commentsState: StateFlow<ResponseUiState<List<Comment>>> = _commentsState

    fun getComments(pathId: Int) {
        viewModelScope.launch {
            _commentsState.value = ResponseUiState.Loading
            commentUseCase.getCommentsUseCase(pathId, CommentType.PATH)
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

    fun createComment(token: String, pathId: Int, content: String) {
        viewModelScope.launch {
            commentUseCase.createCommentUseCase(token, pathId, content, CommentType.PATH)
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> getComments(pathId)
                        is AuthResult.NetworkError -> _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        else -> {}
                    }
                }
        }
    }

    fun updateComment(token: String, pathId: Int, commentId: Int, content: String) {
        viewModelScope.launch {
            commentUseCase.updateCommentUseCase(token, pathId, commentId, content, CommentType.PATH)
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> getComments(pathId)
                        is AuthResult.NetworkError -> _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        else -> {}
                    }
                }
        }
    }

    fun deleteComment(token: String, pathId: Int, commentId: Int) {
        viewModelScope.launch {
            commentUseCase.deleteCommentUseCase(token, pathId, commentId, CommentType.PATH)
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> getComments(pathId)
                        is AuthResult.NetworkError -> _commentsState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        else -> {}
                    }
                }
        }
    }
    // =================================================================
    // 📌 4. 경로 삭제
    // =================================================================

    fun deletePath(pathId: Int) {
        viewModelScope.launch {
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                Log.e("TrailDetailViewModel", "Delete path failed: token is null")
                return@launch
            }
            // PathUseCase는 없지만 필요하다면 주입받아야 함
            // 임시로 로그만 남김
            Log.d("TrailDetailViewModel", "deletePath called for pathId: $pathId")
        }
    }
}

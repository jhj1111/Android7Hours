package com.sesac.trail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.sesac.common.model.UiEvent
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Coord
import com.sesac.domain.model.MemoMarker
import com.sesac.domain.model.MypageSchedule
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthResult
import com.sesac.domain.usecase.mypage.AddScheduleUseCase
import com.sesac.domain.usecase.mypage.DiaryUseCase
import com.sesac.domain.usecase.mypage.MypageUseCase
import com.sesac.domain.usecase.path.PathUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrailCreateViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val pathUseCase: PathUseCase,
    private val addScheduleUseCase: AddScheduleUseCase,
    private val mypageUseCase: MypageUseCase,
    private val diaryUseCase: DiaryUseCase,
) : ViewModel() {
    private val _invalidToken = Channel<UiEvent>()
    val invalidToken = _invalidToken.receiveAsFlow()

    // =================================================================
    // 📌 1. 선택된 경로 & 메모 마커
    // =================================================================

    private val _selectedPath = MutableStateFlow<Path?>(null)
    val selectedPath get() = _selectedPath.asStateFlow()

    private val _memoMarkers = MutableStateFlow<List<com.sesac.domain.model.MemoMarker>>(emptyList())
    val memoMarkers = _memoMarkers.asStateFlow()

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

    fun addMemoMarker(latitude: Double, longitude: Double, memo: String) {
        val newMarker = com.sesac.domain.model.MemoMarker(latitude, longitude, memo)
        _memoMarkers.value = _memoMarkers.value + newMarker
    }

    fun clearMemoMarkers() {
        _memoMarkers.value = emptyList()
    }

    // =================================================================
    // 📌 2. Draft 관리
    // =================================================================

    private val _draftPath = MutableStateFlow<Path?>(null)
    val draftPath = _draftPath.asStateFlow()

    private val _drafts = MutableStateFlow<List<Path>>(emptyList())
    val drafts: StateFlow<List<Path>> get() = _drafts.asStateFlow()

    // ✅ tempPathCoords 파라미터 추가
    fun createDraftPath(selectedPath: Path, tempPathCoords: List<LatLng>): Path {
        val coords = tempPathCoords.map { latLng ->
            Coord(latLng.latitude, latLng.longitude)
        }

        val newDraft = selectedPath.copy(
            coord = coords,
            markers = _memoMarkers.value
        )

        _draftPath.value = newDraft
        return newDraft
    }

    fun clearDraftPath() {
        _draftPath.value = null
        clearMemoMarkers()
    }

    suspend fun loadDrafts(): List<Path> {
        return try {
            val list = pathUseCase.getAllDraftsUseCase().first()
            _drafts.value = list
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveDraft(draft: Path): Path? {
        return try {
            Log.d("TrailCreateViewModel", "🔄 Calling pathUseCase.saveDraftUseCase...")
            Log.d("TrailCreateViewModel", "Draft details: id=${draft.id}, name=${draft.pathName}, coords=${draft.coord?.size}")

            val savedPath = pathUseCase.saveDraftUseCase(draft).first()
            Log.d("TrailCreateViewModel", "UseCase returned: $savedPath")

            loadDrafts()
            Log.d("TrailCreateViewModel", "✅ Draft saved and list reloaded")
            savedPath
        } catch (e: Exception) {
            Log.e("TrailCreateViewModel", "❌ Exception in saveDraft: ${e.message}", e)
            null
        }
    }

    fun saveDraftAsync(draft: Path) {
        viewModelScope.launch {
            saveDraft(draft)
        }
    }

    suspend fun deleteDraft(draft: Path): Boolean {
        return try {
            val success = pathUseCase.deleteDraftUseCase(draft).first()
            if (success) loadDrafts()
            success
        } catch (e: Exception) {
            false
        }
    }

    // =================================================================
    // 📌 3. 경로 CRUD
    // =================================================================

    private val _createState = MutableStateFlow<ResponseUiState<Path>>(ResponseUiState.Idle)
    val createState = _createState.asStateFlow()

    private val _updateState = MutableStateFlow<ResponseUiState<Path>>(ResponseUiState.Idle)
    val updateState = _updateState.asStateFlow()

    private val _diaryMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val diaryMap get() = _diaryMap.asStateFlow()

    fun savePath(token: String?, currentCoord: Coord?, radius: Float = 5000f) {
        viewModelScope.launch {
            if (token.isNullOrEmpty()) {
                _invalidToken.send(UiEvent.ToastEvent("유저 정보가 없습니다."))
                return@launch
            }

            _selectedPath.value?.let { path ->
                pathUseCase.createPathUseCase(token, path).collectLatest { result ->
                    if (result is AuthResult.Success) {
                        _selectedPath.value = result.resultData
                    }
                }
            }
        }
    }

    fun updatePath() {
        viewModelScope.launch {
            val token = sessionUseCase.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                _invalidToken.send(UiEvent.ToastEvent("유저 정보가 없습니다."))
                return@launch
            }
            _updateState.value = ResponseUiState.Loading
            _selectedPath.value?.let { path ->
                pathUseCase.updatePathUseCase(token, path.id, path)
                    .catch { e ->
                        _updateState.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류")
                    }
                    .collectLatest { result ->
                        when (result) {
                            is AuthResult.Success -> {
                                _updateState.value = ResponseUiState.Success("산책로가 수정되었습니다.", result.resultData)
                            }
                            is AuthResult.NetworkError -> {
                                _updateState.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    fun resetCreateState() {
        viewModelScope.launch {
            _createState.value = ResponseUiState.Idle
        }
    }

    fun resetUpdateState() {
        viewModelScope.launch {
            _updateState.value = ResponseUiState.Idle
        }
    }

    fun deletePath(pathId: Int) {
        viewModelScope.launch {
            val token = sessionUseCase.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                _invalidToken.send(UiEvent.ToastEvent("유저 정보가 없습니다."))
                return@launch
            }
            pathUseCase.deletePathUseCase(token, pathId).collectLatest { result ->
                if (result is AuthResult.Success) {
                    // 삭제 성공 처리
                }
            }
        }
    }

    // =================================================================
    // 📌 4. RoomDB 저장 & 서버 업로드
    // =================================================================

    fun savePathAndUpload(path: Path) {
        viewModelScope.launch {
            _createState.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            try {
                // 1️⃣ RoomDB에 저장
                val savedPathWithId = saveDraft(path)
                if (savedPathWithId == null) {
                    Log.e("TrailCreateViewModel", "Failed to save draft to RoomDB")
                    _createState.value = ResponseUiState.Error("경로 저장 실패")
                    return@launch
                }
                Log.d("TrailCreateViewModel", "✅ RoomDB 저장 완료 - path: $savedPathWithId")

                // 2️⃣ 서버 업로드
                Log.d("TrailCreateViewModel", "Attempting to upload path to server...")
                token?.let {
                    val result = pathUseCase.createPathUseCase(token, savedPathWithId)
                        .first { it is AuthResult.Success || it is AuthResult.NetworkError }
                    when (result) {
                        is AuthResult.Loading -> {}
                        is AuthResult.Success -> {
                            Log.d("TrailCreateViewModel", "Path uploaded successfully to server. ${result.resultData}")
                            _createState.value = ResponseUiState.Success("경로가 서버로 업로드되었습니다.", result.resultData)

                            // RoomDB 삭제
                            val deleted = deleteDraft(savedPathWithId)
                            if (deleted) {
                                loadDrafts()

                                // ✅ 3️⃣ MypageSchedule 생성 및 저장
                                val scheduleId = savedPathWithId.id.toLong()
                                val newSchedule = MypageSchedule(
                                    id = scheduleId,
                                    date = LocalDate.now(),
                                    title = savedPathWithId.pathName,
                                    memo = "",
                                    isPath = true,
                                    pathId = savedPathWithId.id,
                                    isCompleted = false
                                )

                                addScheduleUseCase(newSchedule).collectLatest { success ->
                                    if (success) {
                                        Log.d("TrailCreateViewModel", "✅ Schedule 추가 성공: scheduleId=$scheduleId")
                                        generateAndSaveDiary(scheduleId, result.resultData)
                                        completeSchedule(scheduleId)
                                    } else {
                                        Log.e("TrailCreateViewModel", "❌ Schedule 추가 실패")
                                    }
                                }
                            }
                        }

                        is AuthResult.NetworkError -> {
                            val errorMsg = result.exception.message ?: ""

                            // 🔥 JsonDataException이면 실제로는 저장 성공한 것
                            if (errorMsg.contains("JsonDataException") ||
                                errorMsg.contains("Required value") ||
                                errorMsg.contains("missing at")
                            ) {
                                Log.d("TrailCreateViewModel", "✅ JSON 파싱 에러지만 서버 저장은 성공으로 간주")

                                val deleted = deleteDraft(savedPathWithId)
                                if (deleted) {
                                    loadDrafts()
                                    _createState.value = ResponseUiState.Success("경로가 서버로 업로드되었습니다.", savedPathWithId)

                                    val scheduleId = savedPathWithId.id.toLong()
                                    val newSchedule = MypageSchedule(
                                        id = scheduleId,
                                        date = LocalDate.now(),
                                        title = savedPathWithId.pathName,
                                        memo = "",
                                        isPath = true,
                                        pathId = savedPathWithId.id,
                                        isCompleted = false
                                    )

                                    addScheduleUseCase(newSchedule).collectLatest { success ->
                                        if (success) {
                                            Log.d("TrailCreateViewModel", "✅ Schedule 추가 성공")
                                            generateAndSaveDiary(scheduleId, savedPathWithId)
                                            completeSchedule(scheduleId)
                                        }
                                    }
                                }
                            } else {
                                Log.e("TrailCreateViewModel", "❌ 실제 업로드 실패: $errorMsg")
                                _createState.value = ResponseUiState.Error("서버 업로드 실패: $errorMsg")
                            }
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e("TrailCreateViewModel", "An exception occurred in savePathAndUpload: ${e.message}", e)
                _invalidToken.send(UiEvent.ToastEvent("오류 발생: ${e.message}"))
                _createState.value = ResponseUiState.Error("오류 발생: ${e.message}")
            }
        }
    }

    private fun generateAndSaveDiary(scheduleId: Long, path: Path) {
        viewModelScope.launch {
            try {
                Log.d("TrailCreateViewModel", "✅ [다이어리 생성 시작] scheduleId=$scheduleId, pathId=${path.id}")

                val diary = diaryUseCase(path)
                Log.d("TrailCreateViewModel", "✅ [다이어리 생성 성공] ${diary.diary.take(50)}...")

                mypageUseCase.saveDiaryToLocalUseCase(scheduleId, path.id, diary.diary)
                _diaryMap.value = _diaryMap.value + (scheduleId to diary.diary)

                Log.d("TrailCreateViewModel", "✅ [다이어리 저장 완료] scheduleId=$scheduleId")
            } catch (e: Exception) {
                Log.e("TrailCreateViewModel", "❌ [다이어리 생성 실패]", e)
                _diaryMap.value = _diaryMap.value + (scheduleId to "다이어리 생성 실패: ${e.message}")
            }
        }
    }

    private fun completeSchedule(scheduleId: Long) {
        viewModelScope.launch {
            try {
                Log.d("TrailCreateViewModel", "✅ [Schedule 완료 처리 시작] scheduleId=$scheduleId")

                mypageUseCase.getSchedulesUseCase(LocalDate.now()).collectLatest { schedules ->
                    val schedule = schedules.find { it.id == scheduleId }

                    if (schedule != null) {
                        Log.d("TrailCreateViewModel", "✅ Schedule 찾음: ${schedule.title}")

                        val completedSchedule = schedule.copy(isCompleted = true)

                        mypageUseCase.updateScheduleUseCase(completedSchedule).collectLatest { success ->
                            if (success) {
                                Log.d("TrailCreateViewModel", "✅ [Schedule 완료 업데이트 성공] scheduleId=$scheduleId")
                            } else {
                                Log.e("TrailCreateViewModel", "❌ [Schedule 완료 업데이트 실패]")
                            }
                        }
                    } else {
                        Log.e("TrailCreateViewModel", "❌ [Schedule을 찾을 수 없음] scheduleId=$scheduleId")

                        Log.d("TrailCreateViewModel", "⚠️ Schedule 재생성 시도")
                        val newSchedule = MypageSchedule(
                            id = scheduleId,
                            date = LocalDate.now(),
                            title = _selectedPath.value?.pathName ?: "산책로",
                            memo = "",
                            isPath = true,
                            pathId = scheduleId.toInt(),
                            isCompleted = true
                        )

                        mypageUseCase.addScheduleUseCase(newSchedule).collectLatest { success ->
                            if (success) {
                                Log.d("TrailCreateViewModel", "✅ Schedule 재생성 성공")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TrailCreateViewModel", "❌ [Schedule 완료 처리 실패]", e)
            }
        }
    }

    fun saveDiaryForPath(scheduleId: Long, path: Path) {
        viewModelScope.launch {
            try {
                val diary = mypageUseCase.diaryUseCase(path)
                mypageUseCase.saveDiaryToLocalUseCase(scheduleId, path.id, diary.diary)
                Log.d("TrailCreateViewModel", "✅ 다이어리 저장 완료: scheduleId=$scheduleId, pathId=${path.id}")
            } catch (e: Exception) {
                Log.e("TrailCreateViewModel", "❌ 다이어리 저장 실패: ${e.message}", e)
            }
        }
    }
}
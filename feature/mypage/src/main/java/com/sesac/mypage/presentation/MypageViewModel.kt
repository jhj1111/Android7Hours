package com.sesac.mypage.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.domain.model.BookmarkedPath
import com.sesac.domain.model.BookmarkedPost
import com.sesac.domain.model.Breed
import com.sesac.domain.model.InvitationCode
import com.sesac.domain.model.MypageSchedule
import com.sesac.domain.model.Path
import com.sesac.domain.model.Pet
import com.sesac.domain.result.AuthResult
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.type.BookmarkType
import com.sesac.domain.usecase.bookmark.BookmarkUseCase
import com.sesac.domain.usecase.mypage.DiaryUseCase
import com.sesac.domain.usecase.mypage.MypageUseCase
import com.sesac.domain.usecase.path.PathUseCase
import com.sesac.domain.usecase.pet.PetUseCase
import com.sesac.domain.usecase.post.PostUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import com.sesac.domain.usecase.user.UserUseCase
import com.sesac.mypage.model.MyPathStats
import com.sesac.mypage.utils.getMyPathStatsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val sessionUseCase: SessionUseCase,
    private val bookmarkUseCase: BookmarkUseCase,
    private val petUseCase: PetUseCase,
    private val pathUseCase: PathUseCase,
    private val diaryUseCase: DiaryUseCase,
    private val mypageUseCase: MypageUseCase,
    private val postUseCase: PostUseCase,
) : ViewModel() {
    val tabLabels = listOf("산책로", "커뮤니티")
    private val _activeFilter = MutableStateFlow<String>(tabLabels[0])
    val activeFilter get() = _activeFilter.asStateFlow()

    // MypageMainScreen
    private val _stats = MutableStateFlow<ResponseUiState<List<MyPathStats>>>(ResponseUiState.Idle)
    val stats = _stats.asStateFlow()

    // MypageDetailScreen
    private val _userPets = MutableStateFlow<List<Pet>>(emptyList())
    val userPets = _userPets.asStateFlow()
    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet = _selectedPet.asStateFlow()

    private val _addPetState = MutableStateFlow<ResponseUiState<Unit>>(ResponseUiState.Idle)
    val addPetState = _addPetState.asStateFlow()

    private val _updatePetState = MutableStateFlow<ResponseUiState<Unit>>(ResponseUiState.Idle)
    val updatePetState = _updatePetState.asStateFlow()

    private val _deletePetState = MutableStateFlow<ResponseUiState<Unit>>(ResponseUiState.Idle)
    val deletePetState = _deletePetState.asStateFlow()

    // AddPetScreen
    private val _breeds = MutableStateFlow<List<Breed>>(emptyList())
    val breeds = _breeds.asStateFlow()
    private val _bookmarkedPaths = MutableStateFlow<ResponseUiState<List<BookmarkedPath>>>(ResponseUiState.Idle)
    val bookmarkedPaths = _bookmarkedPaths.asStateFlow()
    private val _selectedPath = MutableStateFlow<ResponseUiState<Path>>(ResponseUiState.Idle)
    val selectedPath = _selectedPath.asStateFlow()

    // MypageFavoriteScreen
    private val _bookmarkedPosts = MutableStateFlow<ResponseUiState<List<BookmarkedPost>>>(ResponseUiState.Idle)
    val bookmarkedPosts = _bookmarkedPosts.asStateFlow()

    // MypageManageScreen
    private val _schedules = MutableStateFlow<List<MypageSchedule>>(emptyList())
    val schedules get() = _schedules.asStateFlow()

    // 다이어리 상태 - Map으로 여러 일정의 다이어리 관리
    private var currentScheduleId: Long? = null
    private val _diaryMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val diaryMap: StateFlow<Map<Long, String>> = _diaryMap.asStateFlow()

    // Invite Code
    private val _invitationCode = MutableStateFlow<ResponseUiState<InvitationCode>>(ResponseUiState.Idle)
    val invitationCode get() = _invitationCode.asStateFlow()

    fun onFilterChange(filter: String) {
        _activeFilter.value = filter
    }

    fun generateInvitationCode() {
        viewModelScope.launch {
            _invitationCode.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _invitationCode.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }
            userUseCase.postInvitationCodeUseCase(token)
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _invitationCode.value = ResponseUiState.Success("초대 코드가 생성되었습니다.", result.resultData)
                        }
                        is AuthResult.NetworkError -> {
                            _invitationCode.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        }
                        else -> {
                            _invitationCode.value = ResponseUiState.Error("초대 코드 생성에 실패했습니다.")
                        }
                    }
                }
        }
    }

    fun resetInvitationCodeState() {
        _invitationCode.value = ResponseUiState.Idle
    }

    fun getPathInfo(pathId: Int) {
        viewModelScope.launch {
            _selectedPath.value = ResponseUiState.Loading
            pathUseCase.getPathById(pathId)
                .catch { e ->
                    _selectedPath.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { path ->
                    when (path) {
                        is AuthResult.Success -> {
                            val selectedPath = path.resultData
                            _selectedPath.value = ResponseUiState.Success("산책로 불러오기 성공", selectedPath)
                        }
                        is AuthResult.NetworkError -> _selectedPath.value = ResponseUiState.Error(path.exception.message ?: "unknown")
                        else -> {}
                    }
                }
        }
    }

    fun resetSelectedPathState() {
        _selectedPath.value = ResponseUiState.Idle
    }

    fun loadPetForEditing(petId: Int) {
        val petToEdit = _userPets.value.find { it.id == petId }
        _selectedPet.value = petToEdit
    }

    fun clearSelectedPet() {
        _selectedPet.value = null
    }

    fun getAllUserPets() {
        viewModelScope.launch {
            val token = sessionUseCase.getAccessToken().first() ?: return@launch
            petUseCase.getUserPetsUseCase(token).collectLatest { result ->
                if (result is AuthResult.Success) {
                    _userPets.value = result.resultData
                } else {
                    // TODO: Pet list loading failure error handling
                }
            }
        }
    }

    fun getBreeds() {
        viewModelScope.launch {
            petUseCase.getBreedsUseCase().collectLatest { result ->
                if (result is AuthResult.Success) {
                    _breeds.value = result.resultData
                }
            }
        }
    }

    fun addPet(context: Context, pet: Pet, imageUri: Uri?) {
        viewModelScope.launch {
            _addPetState.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _addPetState.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            val imagePart = imageUri?.toMultipartBodyPart(context, "image")

            // Assumption: petUseCase.postUserPetUseCase is updated to handle a MultipartBody.Part.
            // The signature would be: postUserPetUseCase(token: String, image: MultipartBody.Part?, pet: Pet)
            petUseCase.postUserPetUseCase(token, imagePart, pet).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        getAllUserPets()
                        _addPetState.value = ResponseUiState.Success("반려견이 추가되었습니다.", Unit)
                    }
                    is AuthResult.NetworkError -> {
                        _addPetState.value =
                            ResponseUiState.Error(result.exception.message ?: "오류가 발생했습니다.")
                    }
                    else -> {
                        _addPetState.value = ResponseUiState.Error("알 수 없는 오류가 발생했습니다.")
                    }
                }
            }
        }
    }

    fun updatePet(context: Context, pet: Pet, imageUri: Uri?) {
        viewModelScope.launch {
            _updatePetState.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _updatePetState.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            val imagePart = imageUri?.toMultipartBodyPart(context, "image")

            // Assumption: petUseCase.updatePetUseCase is updated to handle a MultipartBody.Part.
            // The signature would be: updatePetUseCase(token: String, id: Int, image: MultipartBody.Part?, pet: Pet)
            petUseCase.updatePetUseCase(token, pet.id, imagePart, pet).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        getAllUserPets()
                        _updatePetState.value = ResponseUiState.Success("반려견 정보가 수정되었습니다.", Unit)
                    }
                    else -> _updatePetState.value = ResponseUiState.Error("수정에 실패했습니다.")
                }
            }
        }
    }

    private fun Uri.toMultipartBodyPart(context: Context, partName: String): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(this) ?: return null
            val fileBytes = inputStream.readBytes()
            inputStream.close()

            // Find the file name from the URI, or use a default.
            val fileName = "pet_image.jpg" // A more robust way to get the file name could be implemented.
            val requestFile = fileBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, fileBytes.size)

            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: Exception) {
            Log.e("MypageViewModel", "Failed to convert Uri to MultipartBody.Part", e)
            null
        }
    }

    fun deletePet(petId: Int) {
        viewModelScope.launch {
            _deletePetState.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _deletePetState.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            petUseCase.deletePetUseCase(token, petId).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        getAllUserPets()
                        _deletePetState.value = ResponseUiState.Success("반려견이 삭제되었습니다.", Unit)
                    }

                    is AuthResult.Loading -> {}
                    else -> _deletePetState.value = ResponseUiState.Error("삭제에 실패했습니다.")
                }
            }
        }
    }

    fun resetAddPetState() {
        _addPetState.value = ResponseUiState.Idle
    }

    fun resetUpdatePetState() {
        _updatePetState.value = ResponseUiState.Idle
    }

    fun resetDeletePetState() {
        _deletePetState.value = ResponseUiState.Idle
    }

    fun getMyBookmarks(token: String?) {
        viewModelScope.launch {
            _bookmarkedPaths.value = ResponseUiState.Loading
            _bookmarkedPosts.value = ResponseUiState.Loading
            if (token == null) {
                val error = "로그인이 필요합니다."
                _bookmarkedPaths.value = ResponseUiState.Error(error)
                _bookmarkedPosts.value = ResponseUiState.Error(error)
                return@launch
            }

            bookmarkUseCase.getMyBookmarksUseCase(token)
                .catch { e ->
                    val errorMsg = e.message ?: "알 수 없는 오류가 발생했습니다."
                    _bookmarkedPaths.value = ResponseUiState.Error(errorMsg)
                    _bookmarkedPosts.value = ResponseUiState.Error(errorMsg)
                }
                .collectLatest { bookmarksResult ->
                    when (bookmarksResult) {
                        is AuthResult.Success -> {
                            val allItems = bookmarksResult.resultData.map { it.bookmarkedItem }
                            val pathList = allItems.filterIsInstance<BookmarkedPath>()
                            val postList = allItems.filterIsInstance<BookmarkedPost>()
                            _bookmarkedPaths.value = ResponseUiState.Success("산책로 북마크를 불러왔습니다.", pathList)
                            _bookmarkedPosts.value = ResponseUiState.Success("게시글 북마크를 불러왔습니다.", postList)
                        }

                        is AuthResult.NetworkError -> {
                            val errorMsg = bookmarksResult.exception.message ?: "unknown"
                            _bookmarkedPaths.value = ResponseUiState.Error(errorMsg)
                            _bookmarkedPosts.value = ResponseUiState.Error(errorMsg)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getStats(uiState: AuthUiState) {
        viewModelScope.launch {
            _stats.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _stats.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            pathUseCase.getMyPaths(token)
                .catch { e ->
                    _stats.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { myPathResult ->
                    val userNickname = uiState.user?.nickname
                    when (myPathResult) {
                        is AuthResult.Success -> {
                            val pathList =
                                myPathResult.resultData.filter { it.uploader == userNickname }
                            val myPathStats = getMyPathStatsUtils(pathList)
                            _stats.value = ResponseUiState.Success("마이페이지 스탯을 불러왔습니다", myPathStats)
                        }

                        is AuthResult.NetworkError -> {
                            _stats.value =
                                ResponseUiState.Error(myPathResult.exception.message ?: "unknown")
                        }

                        else -> {
                            _stats.value = ResponseUiState.Error("데이터를 불러오는데 실패했습니다.")
                        }
                    }
                }
        }
    }

    fun toggleBookmark(token: String?, id: Int, type: BookmarkType) {
        viewModelScope.launch {
            if (token == null) {
                Log.e("MypageViewModel", "Toggle bookmark failed: token is null")
                return@launch
            }
            bookmarkUseCase.toggleBookmarkUseCase(token, id, type)
                .collectLatest { bookmarkResponse ->
                    if (bookmarkResponse is AuthResult.Success) {
                        // Refresh the list on success
                        getMyBookmarks(token)
                    } else if (bookmarkResponse is AuthResult.NetworkError) {
                        Log.e(
                            "MypageViewModel",
                            "Toggle bookmark failed: ${bookmarkResponse.exception}"
                        )
                    }
                }
        }
    }

    fun getSchedules(date: LocalDate) {
        viewModelScope.launch {
            mypageUseCase.getSchedulesUseCase(date)
                .catch { e -> Log.e("MypageViewModel", "일정 로드 실패", e) }
                .collectLatest { scheduleList ->
                    _schedules.value = scheduleList

                    // 완료된 산책로 일정의 다이어리 로드
                    scheduleList
                        .filter { it.isPath && it.isCompleted }
                        .forEach { schedule ->
                            if (!_diaryMap.value.containsKey(schedule.id)) {
                                loadDiaryFromLocal(schedule.id)
                            }
                        }
                }
        }
    }

    // Room에서 다이어리 불러와 메모리에 저장
    fun loadDiaryFromLocal(scheduleId: Long) {
        viewModelScope.launch {
            try {
                Log.d("MypageViewModel", "다이어리 로드 시도: scheduleId=$scheduleId")
                val diary = mypageUseCase.getDiaryFromLocalUseCase(scheduleId)
                if (diary != null) {
                    _diaryMap.value = _diaryMap.value + (scheduleId to diary)
                    Log.d("MypageViewModel", "다이어리 로드 성공: scheduleId=$scheduleId, diary=$diary")
                } else {
                    Log.d("MypageViewModel", "다이어리 없음: scheduleId=$scheduleId")
                }
            } catch (e: Exception) {
                Log.e("MypageViewModel", "다이어리 로드 실패: scheduleId=$scheduleId", e)
            }
        }
    }

    // 산책로 일정 완료 후 다이어리 생성 및 저장
    private fun loadPathAndGenerateDiary(scheduleId: Long, pathId: Int) {
        viewModelScope.launch {
            try {
                pathUseCase.getPathById(pathId).collectLatest { result ->
                    if (result is AuthResult.Success) {
                        val path = result.resultData
                        generateAndSaveDiary(scheduleId, pathId, path)
                    }
                }
            } catch (e: Exception) {
                Log.e("MypageViewModel", "Path 로드 실패", e)
            }
        }
    }

    private fun generateAndSaveDiary(scheduleId: Long, pathId: Int, path: Path) {
        viewModelScope.launch {
            try {
                val diary = diaryUseCase(path)
                mypageUseCase.saveDiaryToLocalUseCase(scheduleId, pathId, diary.diary)
                // ✅ Room뿐만 아니라 메모리에도 저장 -> Compose 재컴포즈
                _diaryMap.value = _diaryMap.value + (scheduleId to diary.diary)
                Log.d("MypageViewModel", "다이어리 저장 완료: scheduleId=$scheduleId")
            } catch (e: Exception) {
                Log.e("MypageViewModel", "다이어리 생성/저장 실패", e)
                _diaryMap.value = _diaryMap.value + (scheduleId to "다이어리 생성 실패")
            }
        }
    }

    fun addSchedule(schedule: MypageSchedule) {
        viewModelScope.launch {
//            mypageUseCase.addScheduleUseCase(schedule).collectLatest { success ->
//                if (success) {
//                    getSchedules(schedule.date) // Reload schedules for the date
//                }
//            }
        }
    }

    fun deleteSchedule(schedule: MypageSchedule) {
        viewModelScope.launch {
            mypageUseCase.deleteScheduleUseCase(schedule.id).collectLatest { success ->
                if (success) {
                    // 다이어리도 함께 삭제
                    _diaryMap.value = _diaryMap.value - schedule.id
                    getSchedules(schedule.date)
                }
            }
        }
    }
    fun completeSchedule(schedule: MypageSchedule) {
        viewModelScope.launch {
            val completedSchedule = schedule.copy(isCompleted = true)

            mypageUseCase.updateScheduleUseCase(completedSchedule).collectLatest { success ->
                if (success && schedule.isPath && schedule.pathId != null) {
                    getSchedules(schedule.date)

                    // ✅ 다이어리 생성 및 저장
                    loadPathAndGenerateDiary(schedule.id, schedule.pathId!!)
                }
            }
        }
    }

    // ✅ 서버 동기화 (서버 준비되면 호출)
    fun syncDiariesToServer() {
        viewModelScope.launch {
            try {
                // TODO: 미동기화 다이어리들 서버로 전송
                Log.d("MypageViewModel", "다이어리 서버 동기화 시작")
            } catch (e: Exception) {
                Log.e("MypageViewModel", "동기화 실패", e)
            }
        }
    }

    // ✅ completeScheduleById 함수 추가
    fun completeScheduleById(scheduleId: Long) {
        viewModelScope.launch {
            try {
                Log.d("MypageViewModel", "✅ [1단계] completeScheduleById 호출: scheduleId=$scheduleId")

                // 1. 현재 일정 목록에서 해당 일정 찾기
                val schedule = _schedules.value.find { it.id == scheduleId }

                if (schedule == null) {
                    Log.e("MypageViewModel", "❌ 일정을 찾을 수 없음: scheduleId=$scheduleId")
                    Log.d("MypageViewModel", "현재 일정 목록: ${_schedules.value.map { it.id }}")
                    return@launch
                }

                Log.d("MypageViewModel", "✅ [2단계] 일정 찾음: ${schedule.title}, isPath=${schedule.isPath}, pathId=${schedule.pathId}")

                // 2. 일정을 완료 상태로 업데이트
                val completedSchedule = schedule.copy(isCompleted = true)

                Log.d("MypageViewModel", "✅ [3단계] 일정 완료 상태로 변경 시도")

                mypageUseCase.updateScheduleUseCase(completedSchedule).collectLatest { success ->
                    if (success) {
                        Log.d("MypageViewModel", "✅ [4단계] 일정 업데이트 성공 - isCompleted=true")

                        // 3. 일정 목록 새로고침
                        getSchedules(schedule.date)

                        // 4. 산책로 일정이면 다이어리 생성
                        if (schedule.isPath && schedule.pathId != null) {
                            Log.d("MypageViewModel", "✅ [5단계] 산책로 일정 확인 완료")

                            // Room에 다이어리가 있는지 확인
                            val existingDiary = mypageUseCase.getDiaryFromLocalUseCase(scheduleId)

                            if (existingDiary != null) {
                                Log.d("MypageViewModel", "✅ [6단계] 이미 다이어리 존재: ${existingDiary.take(30)}...")
                                // 메모리에 추가
                                _diaryMap.value = _diaryMap.value + (scheduleId to existingDiary)
                            } else {
                                Log.d("MypageViewModel", "⚠️ Room에 다이어리 없음 - 생성 시도")
                                loadPathAndGenerateDiary(scheduleId, schedule.pathId!!)
                            }
                        }
                    } else {
                        Log.e("MypageViewModel", "❌ 일정 업데이트 실패")
                    }
                }
            } catch (e: Exception) {
                Log.e("MypageViewModel", "❌ completeScheduleById 실패", e)
            }
        }
    }


    fun updatePermission(key: String, isEnabled: Boolean) {
        viewModelScope.launch {
            mypageUseCase.updatePermissionStatusUseCase(key, isEnabled).collectLatest {
                // Can optionally reload permissions if the state is mutable
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionUseCase.clearSession()
        }
    }

    fun signOut(id: Int) {
        viewModelScope.launch {
            userUseCase.deleteUserUseCase(id).collectLatest { }
        }
    }

    fun updateProfileImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            val token = sessionUseCase.getAccessToken().first()
            // [수정] authUseCase 안에 있는 updateProfile 호출
            token?.let {
                userUseCase.updateProfile(token, imagePart, null)
                    .collectLatest { result ->
                        when (result) {
                            is AuthResult.Loading -> {
                                Log.d("MypageViewModel", "업로드 진행 중: Loading")
                            }

                            is AuthResult.Success -> {
                                val updatedUser = result.resultData
                                Log.d(
                                    "MypageViewModel",
                                    "업로드 성공, 유저 정보: ${updatedUser.profileImageUrl}"
                                )
                                // 성공 후 유저 정보 갱신
                                sessionUseCase.saveUser(updatedUser)
                                getAllUserPets()
                            }

                            is AuthResult.NetworkError -> {
                                Log.e(
                                    "MypageViewModel",
                                    "업로드 실패: 네트워크 오류 - ${result.exception.message}"
                                )
                            }

                            else -> {
                                Log.e("MypageViewModel", "업로드 실패: 알 수 없는 오류 - $result")
                            }
                        }
                    }
            }
        }
    }
}
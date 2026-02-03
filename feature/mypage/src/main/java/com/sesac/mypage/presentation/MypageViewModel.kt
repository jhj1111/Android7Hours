package com.sesac.mypage.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.BookmarkedPath
import com.sesac.domain.model.BookmarkedPost
import com.sesac.domain.model.Breed
import com.sesac.domain.model.Diary
import com.sesac.domain.model.InvitationCode
import com.sesac.domain.model.Path
import com.sesac.domain.model.Pet
import com.sesac.domain.result.AuthResult
import com.sesac.domain.type.BookmarkType
import com.sesac.domain.usecase.bookmark.BookmarkUseCase
import com.sesac.domain.usecase.diary.DiaryUseCase
import com.sesac.domain.usecase.mypage.MypageUseCase
import com.sesac.domain.usecase.path.PathUseCase
import com.sesac.domain.usecase.pet.PetUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import com.sesac.domain.usecase.user.UserUseCase
import com.sesac.mypage.model.MyPathStats
import com.sesac.mypage.utils.getMyPathStatsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    private val _myPathList = MutableStateFlow<ResponseUiState<List<Path>>>(ResponseUiState.Idle)
    val myPathList = _myPathList.asStateFlow()
    private val _diariesState = MutableStateFlow<Map<Int, ResponseUiState<Diary>>>(emptyMap())
    val diariesState = _diariesState.asStateFlow()

    // Invite Code
    private val _invitationCode = MutableStateFlow<ResponseUiState<InvitationCode>>(ResponseUiState.Idle)
    val invitationCode get() = _invitationCode.asStateFlow()

    fun onFilterChange(filter: String) {
        _activeFilter.value = filter
    }

    fun generateInvitationCode(uiState: AuthUiState) {
        viewModelScope.launch {
            _invitationCode.value = ResponseUiState.Loading
            val token = uiState.token
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

    fun getMyPathList(uiState: AuthUiState) {
        viewModelScope.launch {
            _myPathList.value = ResponseUiState.Loading
            pathUseCase.getMyPaths(uiState.token!!)
                .catch { e ->
                    _myPathList.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _myPathList.value = ResponseUiState.Success("산책로 불러오기 성공", result.resultData)
                        }
                        is AuthResult.NetworkError -> _selectedPath.value = ResponseUiState.Error(result.exception.message ?: "unknown")
                        else -> {}
                    }
                }
        }
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

    fun getDiaries(pathIds: List<Int>) {
        _diariesState.value = emptyMap()

        val loadingMap = pathIds.associateWith { ResponseUiState.Loading }
        _diariesState.value = loadingMap

        viewModelScope.launch {
            pathIds.forEach { pathId ->
                launch {
                    diaryUseCase.getDiaryUseCase(pathId)
                        .catch { e ->
                            _diariesState.value = _diariesState.value.toMutableMap().also {
                                it[pathId] = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                            }
                        }
                        .collect { result ->
                            val newState = when (result) {
                                is AuthResult.Success -> ResponseUiState.Success("다이어리 로딩 성공", result.resultData)
                                is AuthResult.NetworkError -> ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                                is AuthResult.Loading -> ResponseUiState.Loading
                                is AuthResult.NoConstructor -> ResponseUiState.Idle
                            }
                            _diariesState.value = _diariesState.value.toMutableMap().also {
                                it[pathId] = newState
                            }
                        }
                }
            }
        }
    }

    fun loadPetForEditing(petId: Int) {
        val petToEdit = _userPets.value.find { it.id == petId }
        _selectedPet.value = petToEdit
    }

    fun clearSelectedPet() {
        _selectedPet.value = null
    }

    fun getAllUserPets(uiState: AuthUiState) {
        viewModelScope.launch {
            val token = uiState.token ?: return@launch
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

    fun addPet(uiState: AuthUiState, context: Context, pet: Pet, imageUri: Uri?) {
        viewModelScope.launch {
            _addPetState.value = ResponseUiState.Loading
            val token = uiState.token
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
                        getAllUserPets(uiState)
                        _addPetState.value = ResponseUiState.Success("반려견이 추가되었습니다.", Unit)
                    }
                    is AuthResult.NetworkError -> {
                        _addPetState.value = ResponseUiState.Error(result.exception.message ?: "오류가 발생했습니다.")
                    }
                    else -> {
                        _addPetState.value = ResponseUiState.Error("알 수 없는 오류가 발생했습니다.")
                    }
                }
            }
        }
    }

    fun updatePet(uiState: AuthUiState, context: Context, pet: Pet, imageUri: Uri?) {
        viewModelScope.launch {
            _updatePetState.value = ResponseUiState.Loading
            val token = uiState.token
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
                        getAllUserPets(uiState)
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

    fun deletePet(uiState: AuthUiState, petId: Int) {
        viewModelScope.launch {
            _deletePetState.value = ResponseUiState.Loading
            val token = uiState.token
            if (token == null) {
                _deletePetState.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            petUseCase.deletePetUseCase(token, petId).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        getAllUserPets(uiState)
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

    fun getMyBookmarks(uiState: AuthUiState) {
        viewModelScope.launch {
            val token = uiState.token
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
            val token = uiState.token
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

    fun toggleBookmark(uiState: AuthUiState, id: Int, type: BookmarkType) {
        viewModelScope.launch {
            val token = uiState.token

            if (token == null) {
                Log.e("MypageViewModel", "Toggle bookmark failed: token is null")
                return@launch
            }
            bookmarkUseCase.toggleBookmarkUseCase(token, id, type)
                .collectLatest { bookmarkResponse ->
                    if (bookmarkResponse is AuthResult.Success) {
                        // Refresh the list on success
                        getMyBookmarks(uiState)
                    } else if (bookmarkResponse is AuthResult.NetworkError) {
                        Log.e(
                            "MypageViewModel",
                            "Toggle bookmark failed: ${bookmarkResponse.exception}"
                        )
                    }
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

    fun updateProfileImage(uiState: AuthUiState, imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            val token = uiState.token
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
                                getAllUserPets(uiState)
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
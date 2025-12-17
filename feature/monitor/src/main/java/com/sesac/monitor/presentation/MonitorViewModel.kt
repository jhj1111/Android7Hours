package com.sesac.monitor.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.common.model.webrtc.SignalingEvent
import com.sesac.common.ui_state.MonitorUiState
import com.sesac.common.usecase.webrtc.WebRTCUseCase
import com.sesac.domain.model.Pet
import com.sesac.domain.model.User
import com.sesac.domain.result.AuthResult
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.usecase.pet.PetUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import org.webrtc.VideoTrack
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val sessionUseCase: SessionUseCase,
    private val webRTCUseCase: WebRTCUseCase,
    private val eglBase: EglBase // EglBase 주입 추가
) : ViewModel() {

    val _activeTab = MutableStateFlow<String>("")
    val activeTab get() = _activeTab.asStateFlow()

    private val _uiState = MutableStateFlow<MonitorUiState>(MonitorUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _monitoredPet = MutableStateFlow<ResponseUiState<Pet>>(ResponseUiState.Idle)
    val monitoredPet = _monitoredPet.asStateFlow()

    private val _monitorablePets =
        MutableStateFlow<ResponseUiState<List<Pet>>>(ResponseUiState.Idle) // NEW STATE
    val monitorablePets = _monitorablePets.asStateFlow() // NEW EXPOSED STATE

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet = _selectedPet.asStateFlow()

    private val _remoteVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val remoteVideoTrack = _remoteVideoTrack.asStateFlow()

    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack = _localVideoTrack.asStateFlow()

    private var currentUser: User? = null
    private var targetUserId: String? = null // 주인 유저 아이디

    fun getEglBase(): EglBase { // EglBase getter 추가
        return eglBase
    }

    init {
        checkUserRole()
    }

    fun selectTab(selectedTab: String) {
        viewModelScope.launch {
            _activeTab.value = selectedTab
        }
    }

    fun selectPet(pet: Pet?) {
        _selectedPet.value = pet
        if (pet == null) {
            endCall()
        }
    }

    /**
     * 현재 로그인된 사용자의 역할을 확인하고, 역할에 맞는 초기 화면 상태를 설정합니다.
     */
    fun checkUserRole() {
        viewModelScope.launch {
            _uiState.value = MonitorUiState.Loading
            currentUser = sessionUseCase.getUserInfo().first()

            if (currentUser == null) {
                _uiState.value = MonitorUiState.Error("사용자 정보를 가져올 수 없습니다. 다시 로그인해주세요.")
                return@launch
            }

            if (currentUser?.isPet == true) {
                // 사용자가 '펫'인 경우, 스트리밍 준비
                prepareStreaming()
            } else {
                // 사용자가 '주인'인 경우, 펫 목록을 가져옵니다.
                // UI는 selectedPet 상태에 따라 PetSelectionScreen 또는 Dashboard를 표시합니다.
                getMonitorablePets()
                _uiState.value = MonitorUiState.OwnerScreen(emptyList()) // 초기 상태
            }
        }
    }

    // WebRTC 이벤트 처리를 시작하는 함수 추가
    private fun observeWebRTCEvents() {
        viewModelScope.launch {
            try {
                // 1. 시그널링 서버로부터 오는 이벤트를 관찰합니다.
                webRTCUseCase.observeSignalingEvents().collectLatest { event ->
                    when (event) {
                        is SignalingEvent.OfferReceived -> {
                            Log.d("MonitorViewModel", "Offer received from user ${event.fromUserId}")
                            // 펫 입장에서 Offer를 받으면 Answer를 생성하여 보냅니다.
                            // WebRTCRepository의 targetUserId를 업데이트합니다.
                            if (currentUser == null) {
                                _uiState.value = MonitorUiState.Error("사용자 정보가 없습니다.")
                                return@collectLatest // 여기서 return@collectLatest를 사용하여 현재 collectLatest 블록을 종료
                            }

                            // 이미 연결 중이면 무시
                            if (_uiState.value is MonitorUiState.Streaming) {
                                Log.d("MonitorViewModel", "Already streaming, ignoring duplicate offer")
                                return@collectLatest
                            }

                            // targetUserId 업데이트 (이제 누가 Offer를 보냈는지 알았으므로)
                            targetUserId = event.fromUserId

                            webRTCUseCase.initializeWebRTC(
                                myUserId = currentUser!!.id.toString(),
                                targetUserId = event.fromUserId // Offer를 보낸 사용자의 ID를 target으로 설정
                            )
                            webRTCUseCase.sendAnswer(event.sdp)
                            _uiState.value = MonitorUiState.Streaming // 상태 업데이트
                        }

                        is SignalingEvent.AnswerReceived -> {
                            Log.d("MonitorViewModel", "Answer received")
                            // 주인 입장에서 Answer를 받으면 원격 SDP로 설정합니다.
                            viewModelScope.launch { // suspend 함수 호출을 위해 코루틴 런치
                                webRTCUseCase.setRemoteDescription(event.sdp)
                            }
                        }

                        is SignalingEvent.IceCandidateReceived -> {
                            Log.d("MonitorViewModel", "ICE candidate received")
                            // 받은 ICE Candidate를 WebRTC 클라이언트에 추가합니다.
                            webRTCUseCase.sendIceCandidate(event.iceCandidate)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MonitorViewModel", "Error observing signaling events", e)
                _uiState.value = MonitorUiState.Error("시그널링 서버에 연결할 수 없습니다: ${e.message}")
            }
        }

        viewModelScope.launch {
            // 2. 원격 비디오 트랙을 관찰하고 StateFlow에 업데이트합니다.
            webRTCUseCase.observeRemoteVideoTrack().collectLatest { track ->
                _remoteVideoTrack.value = track
                // 주인인 경우에만 Viewing 상태로 변경
                if (track != null && currentUser?.isPet == false) {
                    // 영상 수신이 시작되면 상태를 Viewing으로 변경
                    _uiState.value = MonitorUiState.Viewing(
                        (_uiState.value as? MonitorUiState.Calling)?.pet
                            ?: Pet.EMPTY
                    )
                }
            }
        }

        viewModelScope.launch {
            // 3. 로컬 비디오 트랙을 관찰하고 StateFlow에 업데이트합니다.
            webRTCUseCase.observeLocalVideoTrack().collectLatest { track ->
                _localVideoTrack.value = track
            }
        }
    }

    fun getMonitorablePets() { // NEW FUNCTION
        viewModelScope.launch {
            _monitorablePets.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                _monitorablePets.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }

            petUseCase.getUserPetsUseCase(token).collectLatest { result ->
                when (result) {
                    is AuthResult.Success -> {
                        val monitorableList = result.resultData.filter { it.linkedUser != null }
                        _monitorablePets.value =
                            ResponseUiState.Success("모니터링 가능한 펫 목록을 가져왔습니다.", monitorableList)
                    }

                    is AuthResult.NetworkError -> {
                        _monitorablePets.value = ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                    }

                    else -> {
                        _monitorablePets.value = ResponseUiState.Error("펫 목록을 가져오는데 실패했습니다.")
                    }
                }
            }
        }
    }

    fun startMonitoringPetLocation(petId: Int) {
        viewModelScope.launch {
            _monitoredPet.value = ResponseUiState.Loading
            while (true) {
                petUseCase.getPetInfoUseCase(petId).collectLatest { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            val pet = result.resultData // getPetInfoUseCase returns List<Pet>
                            if (pet.lastLocation != null) {
                                _monitoredPet.value = ResponseUiState.Success("위치를 가져왔습니다.", pet)
                            } else {
                                _monitoredPet.value = ResponseUiState.Error("해당 펫을 찾을 수 없습니다.")
                            }
                        }

                        is AuthResult.NetworkError -> {
                            _monitoredPet.value =
                                ResponseUiState.Error(result.exception.message ?: "네트워크 오류")
                        }

                        else -> {
                            // Loading is handled outside, other states are ignored for now
                        }
                    }
                }
                delay(10000) // 10초마다 갱신
            }
        }
    }

    /**
     * 주인(Owner)이 펫 모니터링을 시작할 때 호출합니다.
     */
    fun startCall(pet: Pet) {
        viewModelScope.launch {
            Log.d("MonitorViewModel", "Starting call to pet: ${pet.name}")
            // 1. 상대방(펫)의 User ID를 가져옵니다.
            targetUserId = pet.linkedUser
            if (targetUserId == null) {
                _uiState.value = MonitorUiState.Error("연결된 펫 계정이 없습니다.")
                return@launch
            }
            if (currentUser == null) {
                _uiState.value = MonitorUiState.Error("사용자 정보가 없습니다.")
                return@launch
            }

            // 2. WebRTC 세션을 초기화합니다.
            webRTCUseCase.initializeWebRTC(
                myUserId = currentUser!!.id.toString(),
                targetUserId = targetUserId!!,
            )

            // 3. WebRTC 이벤트를 감지하기 시작합니다.
            observeWebRTCEvents()

            // 4. Offer(통화 제안)를 생성하고 보냅니다.
            webRTCUseCase.sendOffer()

            _uiState.value = MonitorUiState.Calling(pet)
        }
    }

    /**
     * 펫(Pet)이 스트리밍을 준비할 때 호출합니다.
     */
    private var isWebRTCInitialized = false // 플래그 추가

    fun prepareStreaming() {
        viewModelScope.launch {
            Log.d("MonitorViewModel", "Pet is preparing for streaming...")

            // 이미 초기화되어 있으면 무시
            if (isWebRTCInitialized) {
                Log.d("MonitorViewModel", "WebRTC already initialized, skipping")
                _uiState.value = MonitorUiState.PetScreen
                return@launch
            }

            // WebRTC 이벤트 감지 시작
            observeWebRTCEvents()
            isWebRTCInitialized = true

            _uiState.value = MonitorUiState.PetScreen
        }
    }

    /**
     * 통화를 종료하거나 취소합니다.
     */
    fun endCall() {
        viewModelScope.launch {
            Log.d("MonitorViewModel", "Ending call.")
            webRTCUseCase.closeSession()
            // 역할에 따라 이전 화면으로 돌아갑니다.
            selectPet(null)
            checkUserRole()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel이 소멸될 때 WebRTC 세션을 반드시 종료합니다.
        webRTCUseCase.closeSession()
    }
}
package com.sesac.monitor.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.twotone.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.naver.maps.map.NaverMap
import com.sesac.common.component.CommonFilterTabs
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui_state.MonitorUiState
import com.sesac.monitor.presentation.MonitorViewModel
import com.sesac.common.R as cR
import com.sesac.common.ui_state.ResponseUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorMainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MonitorViewModel = hiltViewModel(),
    commonMapLifecycle: CommonMapLifecycle,
    onMapReady: ((NaverMap) -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ViewModel's init block triggers the role check, so no LaunchedEffect is needed here.
    // The logic is now driven by uiState.

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MonitorUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is MonitorUiState.Error -> {
                Text(text = state.message, modifier = Modifier.align(Alignment.Center))
            }

            is MonitorUiState.PetScreen, is MonitorUiState.Streaming -> {
                // For Pet devices, show the camera/streaming view directly.
                MonitorCamScreen(viewModel = viewModel)
            }

            is MonitorUiState.OwnerScreen, is MonitorUiState.Calling, is MonitorUiState.Viewing -> {
                // For Owner devices, show the standard selection/dashboard flow.
                val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()
                val monitorablePetsState by viewModel.monitorablePets.collectAsStateWithLifecycle()

                if (selectedPet == null) {
                    // Pet Selection Screen
                    when (val petState = monitorablePetsState) {
                        is ResponseUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }

                        is ResponseUiState.Success -> {
                            PetSelectionScreen(
                                pets = petState.result,
                                onPetSelect = { pet -> viewModel.selectPet(pet) }
                            )
                        }

                        is ResponseUiState.Error -> {
                            Text(
                                text = "펫 목록 로드 실패: ${petState.message}",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> { // Idle
                            Text(
                                text = "펫 목록을 불러오는 중...",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
            // 모니터링 대시보드
                    MonitoringDashboard(
                        viewModel = viewModel,
                        commonMapLifecycle = commonMapLifecycle
                    )
                }
            }
        }
    }
}

@Composable
fun MonitoringDashboard(
    viewModel: MonitorViewModel,
    commonMapLifecycle: CommonMapLifecycle,
) {
    val webCam = stringResource(cR.string.monitor_button_webcam)
    val GPS = stringResource(cR.string.monitor_button_GPS)
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()

    // 펫이 선택 해제되면 이 Composable이 사라지므로, null 체크는 방어적으로만 수행
    val currentPet = selectedPet ?: return

    // 카메라 탭이 활성화되면 자동으로 통화 시작
    LaunchedEffect(activeTab, currentPet) {
        if (activeTab == webCam) {
            viewModel.startCall(currentPet)
        }
    }

    val filterOptions = listOf(webCam, GPS)
    val filterIcons = listOf(Icons.TwoTone.Videocam, Icons.Default.Navigation)

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 바 (펫 이름, 뒤로가기 버튼)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectPet(null) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "펫 선택으로 돌아가기")
            }
            Spacer(modifier = Modifier.width(paddingMedium))
            Text(
                text = "${currentPet.name} 모니터링",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
        }

        // 탭 바
        CommonFilterTabs(
            modifier = Modifier.padding(horizontal = paddingMedium),
            filterOptions = filterOptions,
            selectedFilter = activeTab,
            onFilterSelected = viewModel::selectTab,
            fiterIcons = filterIcons,
        )

        // 탭 콘텐츠
        when (activeTab) {
            GPS -> {
                MonitorGpsScreen(
                    petId = currentPet.id,
                    commonMapLifecycle = commonMapLifecycle
                )
            }

            else -> { // 기본값은 카메라
                MonitorCamScreen(viewModel = viewModel)
            }
        }
    }
}
package com.sesac.monitor.presentation.monitor_main

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.R
import com.sesac.common.component.CommonFilterTabs
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui_state.AuthUiState
import com.sesac.monitor.presentation.MonitorViewModel
import com.sesac.monitor.presentation.monitor_cam.MonitorCamScreen
import com.sesac.monitor.presentation.monitor_GPS.MonitorGpsScreen

@Composable
fun MonitoringDashboard(
    authorUiState: AuthUiState,
    viewModel: MonitorViewModel,
    commonMapLifecycle: CommonMapLifecycle,
) {
    val webCam = stringResource(R.string.monitor_button_webcam)
    val GPS = stringResource(R.string.monitor_button_GPS)
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()

    // 펫이 선택 해제되면 이 Composable이 사라지므로, null 체크는 방어적으로만 수행
    val currentPet = selectedPet ?: return

    // 카메라 탭이 활성화되면 자동으로 통화 시작
    LaunchedEffect(activeTab, currentPet) {
        if (activeTab == webCam) {
            viewModel.startCall(authorUiState, currentPet)
        }
    }

    val filterOptions = listOf(webCam, GPS)
    val filterIcons = listOf(Icons.TwoTone.Videocam, Icons.Default.Navigation)

    Column(modifier = Modifier.Companion.fillMaxSize()) {
        // 상단 바 (펫 이름, 뒤로가기 버튼)
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = paddingMedium),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectPet(authorUiState, null) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "펫 선택으로 돌아가기")
            }
            Spacer(modifier = Modifier.Companion.width(paddingMedium))
            Text(
                text = "${currentPet.name} ${stringResource(R.string.monitoring)}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 탭 바
        CommonFilterTabs(
            modifier = Modifier.Companion.padding(horizontal = paddingMedium),
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
                MonitorCamScreen(
                    authorUiState = authorUiState,
                    viewModel = viewModel,
                )
            }
        }
    }
}
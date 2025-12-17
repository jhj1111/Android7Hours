package com.sesac.monitor.presentation.monitor_cam

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sesac.common.R
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui_state.MonitorUiState
import com.sesac.monitor.presentation.MonitorViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MonitorCamScreen(viewModel: MonitorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val remoteVideoTrack by viewModel.remoteVideoTrack.collectAsStateWithLifecycle()
    val localVideoTrack by viewModel.localVideoTrack.collectAsStateWithLifecycle()
    val eglBase = viewModel.getEglBase()

    // WebRTC에 필요한 카메라 및 오디오 권한 요청
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    )

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    if (!permissionsState.allPermissionsGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.monitor_permissions), textAlign = TextAlign.Center)
        }
        return
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MonitorUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MonitorUiState.PetScreen -> {
                PetStreamingReadyContent(onStartClick = { viewModel.prepareStreaming() })
            }
            is MonitorUiState.Calling -> {
                CallingContent(petName = state.pet.name, onCancel = { viewModel.endCall() })
            }
            is MonitorUiState.Viewing -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    VideoView(
                        videoTrack = remoteVideoTrack,
                        eglBase = eglBase,
                        modifier = Modifier.fillMaxSize()
                    )
                    Button(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(paddingLarge)
                    ) {
                        Text(stringResource(R.string.common_finish))
                    }
                }
            }
            is MonitorUiState.Streaming -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // 로컬 카메라 미리보기 화면
                    VideoView(
                        videoTrack = localVideoTrack,
                        eglBase = eglBase,
                        modifier = Modifier.fillMaxSize(),
                        isMirror = true // 로컬 카메라는 좌우 반전
                    )
                }
            }
            is MonitorUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message)
                }
            }
            // OwnerScreen is handled by MonitorMainScreen now
            is MonitorUiState.OwnerScreen -> {
                // This case is now handled before navigating to the dashboard.
                // You can show a loading indicator or a placeholder.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


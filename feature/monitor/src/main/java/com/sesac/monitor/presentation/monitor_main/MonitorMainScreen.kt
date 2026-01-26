package com.sesac.monitor.presentation.monitor_main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.utils.HandleUiState
import com.sesac.common.ui_state.MonitorUiState
import com.sesac.monitor.presentation.MonitorViewModel
import com.sesac.monitor.presentation.monitor_cam.MonitorCamScreen
import com.sesac.common.R
import com.sesac.common.ui_state.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorMainScreen(
    modifier: Modifier = Modifier,
    authorUiState: AuthUiState,
    viewModel: MonitorViewModel = hiltViewModel(),
) {
    val monitorUiState by viewModel.monitorUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit, authorUiState) {
        viewModel.checkUserRole(authorUiState)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = monitorUiState) {
            is MonitorUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is MonitorUiState.Error -> {
                Text(text = state.message, modifier = Modifier.align(Alignment.Center))
            }

            is MonitorUiState.PetScreen, is MonitorUiState.Streaming -> {
                // For Pet devices, show the camera/streaming view directly.
                MonitorCamScreen(
                    authorUiState = authorUiState,
                    viewModel = viewModel,
                )
            }

            is MonitorUiState.OwnerScreen, is MonitorUiState.Calling, is MonitorUiState.Viewing -> {
                // For Owner devices, show the standard selection/dashboard flow.
                val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()
                val monitorablePetsState by viewModel.monitorablePets.collectAsStateWithLifecycle()

                if (selectedPet == null) {
                    // Pet Selection Screen
                    HandleUiState(
                        uiState = monitorablePetsState,
                        error = { message ->
                            Text(
                                text = "${stringResource(R.string.monitor_pet_list_load_failed)} : $message",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        },
                        idle = {
                            Text(
                                text = stringResource(R.string.monitor_pet_list_loading),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        },
                        success = { pets ->
                            PetSelectionScreen(
                                pets = pets,
                                onPetSelect = { pet -> viewModel.selectPet(authorUiState, pet) }
                            )
                        }
                    )
                } else {
                    // 모니터링 대시보드
                    MonitoringDashboard(
                        authorUiState = authorUiState,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}
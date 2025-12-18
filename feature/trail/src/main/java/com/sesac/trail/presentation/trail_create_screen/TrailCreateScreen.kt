package com.sesac.trail.presentation.trail_create_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sesac.common.ui.theme.Border
import com.sesac.common.ui.theme.PaddingSection
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.model.UiEvent
import com.sesac.common.model.toPathParceler
import com.sesac.domain.model.ValidationState
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.component.TagFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrailCreateScreen(
    viewModel: TrailViewModel = hiltViewModel(),
    navController: NavController,
//    uiState: AuthUiState,
) {
    val context = LocalContext.current
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val createState by viewModel.createState.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
//    var isLoading by remember { mutableStateOf(false) }
    val isLoading = updateState is ResponseUiState.Loading || createState is ResponseUiState.Loading
    val recordTime by viewModel.recordingTime.collectAsStateWithLifecycle()
    Log.d("TAG-TrailCreateScree", "is loading : $isLoading")

    val scope = rememberCoroutineScope()
    var validationState by remember { mutableStateOf(ValidationState()) }

    LaunchedEffect(key1 = updateState) {
        when(val state = updateState) {
            is ResponseUiState.Loading -> {}
            is ResponseUiState.Success -> {
                val updatedPath = state.result
                Toast.makeText(context, "산책로가 수정되었습니다!", Toast.LENGTH_SHORT).show()
//                isLoading = false
                // 수정 화면 스택에서 제거하고, 수정된 상세 화면으로 이동
                navController.navigate(NestedNavigationRoute.TrailDetail(updatedPath.toPathParceler())) {
                    popUpTo(navController.currentDestination!!.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                viewModel.resetUpdateState()
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateState()
//                isLoading = false
            }
            else -> {  }
        }
    }

    LaunchedEffect(key1 = createState) {
        when(val state = createState) {
            is ResponseUiState.Loading -> {}
            is ResponseUiState.Success -> {
                val createdPath = state.result
                Toast.makeText(context, "산책로가 생성되었습니다!", Toast.LENGTH_SHORT).show()
                Log.d("TAG-TrailCreateScreen", "created path : $createdPath")
                // 수정 화면 스택에서 제거하고, 수정된 상세 화면으로 이동
                viewModel.resetCreateState()
                navController.popBackStack()

            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetCreateState()

            }
            else -> {  }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.invalidToken.collectLatest { event ->
            if (event is UiEvent.ToastEvent) Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    if (selectedPath == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val handleTagToggle: (String) -> Unit = { tag ->
        selectedPath?.let {
            val newTags = if (it.tags.contains(tag)) {
                it.tags - tag
            } else {
                if (it.tags.size < 5) {
                    it.tags + tag
                } else {
                    scope.launch {
                        Toast.makeText(context, "태그는 최대 5개까지 선택 가능합니다", Toast.LENGTH_SHORT).show()
                    }
                    it.tags
                }
            }
            viewModel.updateSelectedPath(it.copy(tags = newTags))
        }
    }

    fun handleSave() {
        scope.launch {
            selectedPath?.let { selected ->
                val isNameInvalid = selected.pathName.isBlank()

                validationState = ValidationState(
                    isNameInvalid = isNameInvalid
                )

                if (isNameInvalid) {
                    Toast.makeText(context, "필수 항목을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (selected.id != -1) {
                    // 기존 경로 수정 -> ViewModel에 위임
                    viewModel.updatePath()
                } else {
                    // 신규 경로: Draft 생성 → RoomDB 저장
                    val duration = if (recordTime == 0L) selected.duration else recordTime.toInt()
                    val newDraft = viewModel.createDraftPath(selected.copy(duration = duration))
                    viewModel.savePathAndUpload(newDraft)
                    viewModel.resetCreateState()
                    Toast.makeText(context, "산책로가 저장되었습니다!", Toast.LENGTH_SHORT).show()
                }

                // 🔥 저장 완료 후 마커 초기화
                viewModel.clearMemoMarkers()
                viewModel.clearTempPath()
            }
        }
    }

    selectedPath?.let { pathContent ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(paddingLarge),
            verticalArrangement = Arrangement.spacedBy(PaddingSection)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("추천 산책로 등록")
                Spacer(modifier = Modifier.fillMaxSize(0.8f))
                Switch(
                    checked = !pathContent.isPrivate,
                    onCheckedChange = { newIsPrivate -> viewModel.updateSelectedPath(pathContent.copy(isPrivate = !newIsPrivate)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = Border
                    )
                )
            }

            FormTextField(
                label = "산책로 이름",
                value = pathContent.pathName,
                onValueChange = { newValue -> viewModel.updateSelectedPath(pathContent.copy(pathName = newValue)) },
                placeholder = "예: 한강공원 벚꽃길",
                isRequired = true,
                isError = validationState.isNameInvalid
            )

            FormTextField(
                label = "산책로 소개",
                value = pathContent.pathComment ?: "",
                onValueChange = { newDescription -> viewModel.updateSelectedPath(pathContent.copy(pathComment = newDescription)) },
                placeholder = "이 산책로의 특징이나 추천 이유를 작성해주세요...",
                minLines = 4
            )

            TagFlow(
                selectedTags = pathContent.tags,
                onTagToggle = handleTagToggle,
                editable = true
            )

            CreateBottomActions(
                onCancel = {
                    scope.launch {
                        viewModel.clearSelectedPath()
                        navController.popBackStack()
                    }
                },
                onSave = {
                    handleSave()
                },
                isEditing = pathContent.id != -1,
                isLoading = isLoading
            )
        }
    }
}



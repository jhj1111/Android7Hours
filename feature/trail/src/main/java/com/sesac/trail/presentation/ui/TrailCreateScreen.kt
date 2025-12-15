package com.sesac.trail.presentation.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sesac.common.model.PathParceler
import com.sesac.common.ui.theme.Border
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.NoteBox
import com.sesac.common.ui.theme.PaddingSection
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.SheetHandle
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingNone
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.model.UiEvent
import com.sesac.common.model.toPathParceler
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.nav_graph.TrailNavigationRoute
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.component.TagFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ValidationState(
    val isNameInvalid: Boolean = false
)

// --- 메인 Composable ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrailCreateScreen(
    viewModel: TrailViewModel = hiltViewModel(),
    navController: NavController,
    uiState: AuthUiState,
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
                navController.navigate(NestedNavigationRoute.TrailDetail(createdPath.toPathParceler())) {
                    popUpTo(navController.currentDestination!!.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

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

    var uploadedImageUri by remember { mutableStateOf<String?>(null) }
    var distanceString by remember { mutableStateOf(selectedPath?.distance?.takeIf { it > 0f }?.toString() ?: "") }
    var timeString by remember { mutableStateOf(selectedPath?.duration?.takeIf { it > 0 }?.toString() ?: "") }

    /*LaunchedEffect(selectedPath) {
        distanceString = selectedPath?.distance?.takeIf { it > 0f }?.toString() ?: ""
        timeString = selectedPath?.duration?.takeIf { it > 0 }?.toString() ?: ""
    }*/

//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        if (uri != null) {
//            uploadedImageUri = uri.toString()
//            scope.launch{ Toast.makeText(context, "이미지가 업로드되었습니다", Toast.LENGTH_SHORT).show() }
//        }
//    }

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
//            ImageUploader(
//                imageUri = uploadedImageUri,
//                onUploadClick = { imagePickerLauncher.launch("image/*") },
//                onRemoveClick = { uploadedImageUri = null }
//            )

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

//            DifficultySelector(
//                selectedLevel = pathContent.level ?: "초급",
//                onLevelSelect = { newDifficulty -> viewModel.updateSelectedPath(pathContent.copy(level = newDifficulty)) }
//            )

            /*DistanceTimeInputs(
                distance = distanceString,
                onDistanceChange = { newString ->
                    if (newString.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        distanceString = newString
                        viewModel.updateSelectedPath(pathContent.copy(distance = newString.toFloatOrNull() ?: 0f))
                    }
                },
                isDistanceError = validationState.isDistanceInvalid,
                time = timeString,
                onTimeChange = { newString ->
                    if (newString.matches(Regex("^\\d*\$"))) {
                        timeString = newString
                        viewModel.updateSelectedPath(pathContent.copy(duration = newString.toIntOrNull() ?: 0))
                    }
                },
                isTimeError = validationState.isTimeInvalid
            )*/

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


// --- 공통 부분 컴포넌트화 ---

@Composable
fun CreateBottomActions(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isEditing: Boolean,
    isLoading: Boolean,
) {
    Surface(
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            Button(
                onClick = onSave,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple600
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                } else {
                    Text(if (isEditing) "수정하기" else "등록하기", color = White)
                }
            }
        }
    }
}

@Composable
fun ImageUploader(
    imageUri: String?,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column {
        Text("대표 사진", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(paddingMicro))

        if (imageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp) // h-48
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Uploaded",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(paddingMicro)
                        .background(Red500, CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = White)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove image")
                }
            }
        } else {
            val dashedBorderColor = SheetHandle

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onUploadClick() }
                    .background(PrimaryPurpleLight.copy(alpha = 0.5f))
                    .drawBehind {
                        // 점선 테두리 그리기
                        val strokeWidth = 2.dp.toPx()
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        val shape = RoundedCornerShape(12.dp)
                        val outline = shape.createOutline(size, layoutDirection, this)

                        drawOutline(
                            outline = outline,
                            color = dashedBorderColor,
                            style = Stroke(width = strokeWidth, pathEffect = dashEffect)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Upload, contentDescription = null, tint = GrayTabText)
                    Text("사진 업로드", color = GrayTabText)
                    Text("선택 사항", style = MaterialTheme.typography.bodySmall, color = SheetHandle)
                }
            }
        }
    }
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLines: Int = 1
) {
    Column(modifier = modifier) { // ✅ 여기에 modifier 적용
        Row {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (isRequired) {
                Text(" *", color = Red500, style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(paddingMicro))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            keyboardOptions = keyboardOptions,
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(), // ✅ 내부는 fillMaxWidth 유지
            shape = MaterialTheme.shapes.medium,
            isError = isError
        )
    }
}

@Composable
fun DifficultySelector(
    selectedLevel: String,
    onLevelSelect: (String) -> Unit
) {
    val levels = listOf("초급", "중급", "고급")

    LabeledSection(label = "난이도", isRequired = true) {
        Row(horizontalArrangement = Arrangement.spacedBy(paddingSmall)) {
            levels.forEach { level ->
                Button(
                    onClick = { onLevelSelect(level) },
                    colors = if (selectedLevel == level) {
                        ButtonDefaults.buttonColors(containerColor = Purple600)
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = NoteBox,
                            contentColor = GrayTabText
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(level)
                }
            }
        }
    }
}

// --- 공통 Wrapper (라벨 + 별표 + 내용)
@Composable
fun LabeledSection(
    label: String,
    isRequired: Boolean = false,
    content: @Composable () -> Unit
) {
    Column {
        Row {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (isRequired) {
                Text(" *", color = Red500, style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(paddingMicro))
        content()
    }
}

// --- 거리 / 시간 입력 묶음
@Composable
fun DistanceTimeInputs(
    distance: String,
    onDistanceChange: (String) -> Unit,
    isDistanceError: Boolean,
    time: String,
    onTimeChange: (String) -> Unit,
    isTimeError: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(paddingSmall)) {
        FormTextField(
            label = "거리 (km)",
            value = distance,
            onValueChange = onDistanceChange,
            placeholder = "1.5",
            isRequired = true,
            isError = isDistanceError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(paddingNone)
                .weight(1f, fill = true)
        )
        FormTextField(
            label = "예상 시간 (분)",
            value = time,
            onValueChange = onTimeChange,
            placeholder = "25",
            isRequired = true,
            isError = isTimeError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(paddingNone)
                .weight(1f, fill = true)
        )
    }
}

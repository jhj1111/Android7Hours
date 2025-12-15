package com.sesac.mypage.presentation.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.model.Pet
import com.sesac.domain.model.PetLocation
import com.sesac.domain.result.ResponseUiState
import com.sesac.mypage.presentation.MypageViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    petId: Int,
    navController: NavController,
    viewModel: MypageViewModel = hiltViewModel(),
    uiState: AuthUiState,
) {
    val context = LocalContext.current
    val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()
    val breeds by viewModel.breeds.collectAsStateWithLifecycle()
    val addPetState by viewModel.addPetState.collectAsStateWithLifecycle()
    val updatePetState by viewModel.updatePetState.collectAsStateWithLifecycle()

    val isLoading = addPetState is ResponseUiState.Loading || updatePetState is ResponseUiState.Loading
    val isEditMode = petId != -1

    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("남아") }
    var birthday by remember { mutableStateOf("날짜를 선택해주세요") }
    var isNeutered by remember { mutableStateOf(false) }
    var selectedBreed by remember { mutableStateOf("") }
    var isBreedDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { imageUri = it }
        }
    )

    // Screen 진입 시 데이터 로딩
    LaunchedEffect(petId) {
        viewModel.getBreeds()
        if (isEditMode) {
            viewModel.loadPetForEditing(petId)
        }
    }

    // ViewModel의 selectedPet이 변경되면 UI 상태 업데이트
    LaunchedEffect(selectedPet) {
        if (isEditMode) {
            selectedPet?.let { pet ->
                name = pet.name
                selectedGender = if (pet.gender == "M") "남아" else "여아"
                birthday = pet.birthday ?: ""
                isNeutered = pet.neutering
                selectedBreed = pet.breed ?: ""
                imageUrl = pet.image // 기존 이미지 URL 설정
            }
        }
    }

    // Screen에서 벗어날 때 선택된 펫 정보 초기화
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedPet()
        }
    }

    // CUD 상태 변화 감지
    LaunchedEffect(addPetState) {
        when(val state = addPetState) {
            is ResponseUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetAddPetState()
                navController.popBackStack()
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetAddPetState()
            }
            else -> {}
        }
    }

    LaunchedEffect(updatePetState) {
        when(val state = updatePetState) {
            is ResponseUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUpdatePetState()
                navController.popBackStack()
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUpdatePetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(paddingLarge)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isEditMode) "반려견 정보 수정" else "반려견 정보 입력",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(paddingLarge))

            // Image Picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { galleryLauncher.launch("image/*") }
                    .border(2.dp, Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null && imageUrl == null) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(40.dp),
                        tint = Primary
                    )
                } else {
                    AsyncImage(
                        model = imageUri ?: imageUrl,
                        contentDescription = "Pet Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Spacer(modifier = Modifier.height(paddingLarge))

            // Form Items...
            AddPetFormItem(label = "이름") {
                OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("이름을 입력해주세요") })
            }
            AddPetFormItem(label = "성별") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("남아", "여아").forEach { gender ->
                        RadioButton(selected = selectedGender == gender, onClick = { selectedGender = gender }, colors = RadioButtonDefaults.colors(selectedColor = Primary))
                        Text(text = gender, modifier = Modifier.padding(start = paddingSmall))
                        Spacer(modifier = Modifier.width(paddingMedium))
                    }
                }
            }
            AddPetFormItem(label = "생일") {
                Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }.padding(vertical = paddingMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Birthday")
                        Spacer(modifier = Modifier.width(paddingSmall))
                        Text(text = birthday)
                    }
                }
            }
            AddPetFormItem(label = "중성화") {
                Switch(checked = isNeutered, onCheckedChange = { isNeutered = it }, colors = SwitchDefaults.colors(checkedThumbColor = Primary))
            }
            AddPetFormItem(label = "품종") {
                ExposedDropdownMenuBox(expanded = isBreedDropdownExpanded, onExpandedChange = { isBreedDropdownExpanded = it }) {
                    OutlinedTextField(
                        value = selectedBreed,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBreedDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        placeholder = { Text("품종을 선택해주세요") }
                    )
                    ExposedDropdownMenu(expanded = isBreedDropdownExpanded, onDismissRequest = { isBreedDropdownExpanded = false }) {
                        breeds.forEach { breed ->
                            DropdownMenuItem(text = { Text(breed.breedName) }, onClick = { selectedBreed = breed.breedName; isBreedDropdownExpanded = false })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(paddingLarge))

        // Save Button
        Button(
            onClick = {
                if (name.isBlank() || birthday == "날짜를 선택해주세요" || selectedBreed.isBlank()) {
                    Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    val pet = Pet(
                        id = if (isEditMode) petId else 0,
                        name = name,
                        gender = if (selectedGender == "남아") "M" else "F",
                        birthday = birthday,
                        neutering = isNeutered,
                        breed = selectedBreed,
                        owner = uiState.user?.id.toString(),
                        image = imageUrl, // The image is now sent as a separate part, not in the Pet object.
                        linkedUser = null,
                        lastLocation = PetLocation.EMPTY,
                    )
                    if (isEditMode) {
                        viewModel.updatePet(context, pet, imageUri)
                    } else {
                        viewModel.addPet(context, pet, imageUri)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
            } else {
                Text(text = "저장", color = White)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        birthday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun AddPetFormItem(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = paddingMedium).fillMaxWidth()) {
        Text(text = label, style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(paddingSmall))
        content()
    }
}
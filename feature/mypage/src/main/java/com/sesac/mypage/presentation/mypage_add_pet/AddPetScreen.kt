package com.sesac.mypage.presentation.mypage_add_pet

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sesac.common.R
import com.sesac.common.component.CommonDropDownMenuBox
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.buttonHeightMedium
import com.sesac.common.ui.theme.iconSizeMedium
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Pet
import com.sesac.domain.model.PetLocation
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

    val textMale = stringResource(R.string.common_pet_male)
    val textFemale = stringResource(R.string.common_pet_female)
    val textSelectDate = stringResource(R.string.common_select_date)
    val textErrorMessageInputAll = stringResource(R.string.common_error_message_input_all)

    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(textMale) }
    var birthday by remember { mutableStateOf(textSelectDate) }
    var isNeutered by remember { mutableStateOf(false) }
    var selectedBreed by remember { mutableStateOf("") }
    var isBreedDropdownExpanded = remember { mutableStateOf(false) }
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
                selectedGender = if (pet.gender == "M") textMale else textFemale
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
                text = if (isEditMode) stringResource(R.string.mypage_edit_pet) else stringResource(R.string.mypage_create_new_pet),
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(paddingLarge))

            ImagePicker(
                modifier = Modifier.clickable { galleryLauncher.launch("image/*") },
                imageUri = imageUri,
                imageUrl = imageUrl,
            )

            Spacer(modifier = Modifier.height(paddingLarge))

            // Form Items...
            AddPetFormItem(label = stringResource(R.string.common_name)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.mypage_input_pet_name)) },
                )
            }
            AddPetFormItem(label = stringResource(R.string.common_gender)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf(textMale, textFemale).forEach { gender ->
                        RadioButton(selected = selectedGender == gender, onClick = { selectedGender = gender }, colors = RadioButtonDefaults.colors(selectedColor = Primary))
                        Text(text = gender, modifier = Modifier.padding(start = paddingSmall))
                        Spacer(modifier = Modifier.width(paddingMedium))
                    }
                }
            }
            AddPetFormItem(label = stringResource(R.string.common_birthday)) {
                Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }.padding(vertical = paddingMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Birthday")
                        Spacer(modifier = Modifier.width(paddingSmall))
                        Text(text = birthday)
                    }
                }
            }
            AddPetFormItem(label = stringResource(R.string.mypage_pet_neutering)) {
                Switch(checked = isNeutered, onCheckedChange = { isNeutered = it }, colors = SwitchDefaults.colors(checkedThumbColor = Primary))
            }
            AddPetFormItem(label = stringResource(R.string.common_pet_breed)) {
                CommonDropDownMenuBox(
                    isDropdownExpanded = isBreedDropdownExpanded,
                    selectedItem = selectedBreed,
                ) {
                    breeds.forEach { breed ->
                        DropdownMenuItem(
                            text = { Text(breed.breedName) },
                            onClick = {
                                selectedBreed = breed.breedName; isBreedDropdownExpanded.value = false
                            },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(paddingLarge))

        // Save Button
        Button(
            onClick = {
                if (name.isBlank() || birthday == textSelectDate || selectedBreed.isBlank()) {
                    Toast.makeText(context, textErrorMessageInputAll, Toast.LENGTH_SHORT).show()
                } else {
                    val pet = Pet(
                        id = if (isEditMode) petId else 0,
                        name = name,
                        gender = if (selectedGender == textMale) "M" else "F",
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
            modifier = Modifier.fillMaxWidth().height(buttonHeightMedium),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(iconSizeMedium), color = White)
            } else {
                Text(text = stringResource(R.string.common_action_save), color = White)
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
                }) { Text(stringResource(R.string.common_action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.common_action_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
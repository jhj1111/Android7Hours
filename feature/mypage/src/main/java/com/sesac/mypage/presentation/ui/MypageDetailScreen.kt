package com.sesac.mypage.presentation.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.sesac.common.ui.theme.Background
import com.sesac.common.ui.theme.Gray200
import com.sesac.common.ui.theme.Gray300
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.LightBlue
import com.sesac.common.ui.theme.LightGreen
import com.sesac.common.ui.theme.LightPurple
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.FileUtils
import com.sesac.common.utils.fixImageUrl
import com.sesac.domain.model.InvitationCode
import com.sesac.domain.model.Pet
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.mypage.nav_graph.MypageNavigationRoute
import com.sesac.mypage.presentation.MypageViewModel
import com.sesac.common.R as cR


@Composable
fun MypageDetailScreen(
    navController: NavController,
    viewModel: MypageViewModel = hiltViewModel(),
    uiState: AuthUiState,
) {
    val pets by viewModel.userPets.collectAsStateWithLifecycle()
    val deletePetState by viewModel.deletePetState.collectAsStateWithLifecycle()
    val invitationCodeState by viewModel.invitationCode.collectAsStateWithLifecycle() // NEW
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val context = LocalContext.current

    // Dialog control states
    var showAddPetOptionsDialog by remember { mutableStateOf(false) } // NEW
    var showInvitationCodeDialog by remember { mutableStateOf(false) } // NEW

    // 갤러리 실행기 (이 변수 선언이 없어서 에러가 난 것입니다)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            // 1. URI -> MultipartBody.Part 변환
            val imagePart = FileUtils.createMultipartBody(context, uri, "profile_image")
            // 2. 서버로 전송 (토큰이 있을 때만)
            if (imagePart != null) {
                viewModel.updateProfileImage(imagePart)
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState.user?.id != -1) {
            viewModel.getAllUserPets()
            viewModel.clearSelectedPet()
            Log.d("TAG-MypageDetailScreen", "pets : $pets")
        }
    }

    LaunchedEffect(deletePetState) {
        when(val state = deletePetState) {
            is ResponseUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeletePetState()
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeletePetState()
            }
            else -> {}
        }
    }

    // NEW: Handle invitationCodeState for dialog display and toast messages
    LaunchedEffect(invitationCodeState) {
        when(val state = invitationCodeState) {
            is ResponseUiState.Loading -> {
                // Dialog will show loading indicator
                showInvitationCodeDialog = true
            }
            is ResponseUiState.Success -> {
                // Dialog will show code
                showInvitationCodeDialog = true
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetInvitationCodeState()
                showInvitationCodeDialog = false // Close dialog on error
            }
            else -> {
                // Idle state, do nothing or hide dialog if it was showing
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 80.dp) // FAB에 가려지지 않도록 패딩 추가
        ) {
            item {
                MypageDetailHeader(
                    name = uiState.user?.fullName ?: "-",
                    description = "반려견과 함께하는 즐거운 산책",
                    imageUrl = uiState.user?.profileImageUrl,
                    localImageUri = selectedImageUri,
                    onCameraClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(paddingMedium)) }

            item {
                UserInfoSection(
                    email = uiState.user?.email ?: "-",
                    phone = "-",
                    address = "-"
                )
            }

            item { Spacer(modifier = Modifier.height(paddingLarge)) }

            item {
                HorizontalDivider(thickness = 8.dp, color = Gray200)
            }

            item {
                PetInfoSection(
                    pets = pets,
                    onEditPet = { pet ->
                        navController.navigate(MypageNavigationRoute.AddPetScreen(petId = pet.id))
                    },
                    onDeletePet = { pet ->
                        viewModel.deletePet(pet.id)
                    }
                )
            }
        }

        // Modified FAB to show options
        FloatingActionButton(
            onClick = { showAddPetOptionsDialog = true }, // NEW: show options dialog
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(paddingLarge),
            containerColor = Primary,
            contentColor = White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pet")
        }

        // NEW: Add Pet Options Dialog
        if (showAddPetOptionsDialog) {
            AddPetOptionsDialog(
                onDismissRequest = { showAddPetOptionsDialog = false },
                onAddAnimalPet = {
                    showAddPetOptionsDialog = false
                    navController.navigate(MypageNavigationRoute.AddPetScreen())
                },
                onInviteUser = {
                    showAddPetOptionsDialog = false
                    viewModel.generateInvitationCode() // Trigger code generation
                    // showInvitationCodeDialog = true will be handled by LaunchedEffect
                }
            )
        }

        // NEW: Share Invitation Code Dialog
        if (showInvitationCodeDialog) {
            ShareInvitationCodeDialog(
                invitationCodeState = invitationCodeState,
                onDismiss = {
                    viewModel.resetInvitationCodeState()
                    showInvitationCodeDialog = false
                }
            )
        }
    }
}

// NEW: AddPetOptionsDialog Composable
@Composable
fun AddPetOptionsDialog(
    onDismissRequest: () -> Unit,
    onAddAnimalPet: () -> Unit,
    onInviteUser: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("펫 추가 옵션 선택") },
        text = {
            Column {
                Text("어떤 방식으로 펫을 추가하시겠습니까?")
                Spacer(modifier = Modifier.height(paddingMedium))
                Button(
                    onClick = onAddAnimalPet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("반려동물 정보 등록", color = White)
                }
                Spacer(modifier = Modifier.height(paddingSmall))
                Button(
                    onClick = onInviteUser,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("반려견 기기 등록 코드 생성", color = White)
                }
            }
        },
        confirmButton = { /* No confirm button needed, actions are in text part */ }
    )
}

// NEW: ShareInvitationCodeDialog Composable
@Composable
fun ShareInvitationCodeDialog(
    invitationCodeState: ResponseUiState<InvitationCode>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var codeToShare by remember { mutableStateOf("") }

    LaunchedEffect(invitationCodeState) {
        if (invitationCodeState is ResponseUiState.Success) {
            codeToShare = invitationCodeState.result.code
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("펫 초대 코드") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (invitationCodeState) {
                    is ResponseUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(paddingLarge))
                        Text("코드 생성 중...")
                    }
                    is ResponseUiState.Success -> {
                        Text("초대 코드가 생성되었습니다. 친구에게 공유하여 위치 추적을 시작하세요.")
                        Spacer(modifier = Modifier.height(paddingMedium))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Gray300, RoundedCornerShape(8.dp))
                                .padding(paddingMedium)
                        ) {
                            Text(codeToShare, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(paddingSmall))
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(codeToShare))
                                Toast.makeText(context, "코드가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code")
                            }
                        }
                    }
                    is ResponseUiState.Error -> {
                        Text("코드 생성에 실패했습니다: ${invitationCodeState.message}")
                    }
                    else -> { /* Idle state, shouldn't happen when dialog is shown */ }
                }
            }
        },
        confirmButton = {
            if (invitationCodeState is ResponseUiState.Success) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "내 펫 추적 초대 코드: $codeToShare\n앱을 설치하고 회원가입 시 코드를 입력해주세요!")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Code")
                        Spacer(modifier = Modifier.width(paddingSmall))
                        Text("공유하기")
                    }
                    Button(onClick = onDismiss) {
                        Text("닫기")
                    }
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("닫기")
                }
            }
        }
    )
}


@Composable
fun MypageDetailHeader(
    name: String,
    description: String,
    imageUrl: String?,
    localImageUri: android.net.Uri?,
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingLarge, start = paddingLarge, end = paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // [핵심] key를 사용하여 URL이 바뀌면 강제로 새로 그림
            androidx.compose.runtime.key(localImageUri, imageUrl) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        // 1순위: 방금 갤러리 선택 사진 / 2순위: 서버 사진 / 3순위: 기본 사진
                        .data(localImageUri ?: imageUrl ?: cR.drawable.placeholder)
                        // [중요] 캐시 끄기: 이미지가 갱신 안 되는 문제 해결
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, White, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = cR.drawable.placeholder),
                    error = painterResource(id = cR.drawable.placeholder),
                    // 에러 발생 시 로그 출력 (디버깅용)
                    onError = { Log.e("IMAGE_LOAD", "실패 원인: ${it.result.throwable.message}") }
                )
            }

            // ... (카메라 아이콘 등 기존 코드 유지) ...
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).size(36.dp)
                    .background(Primary, CircleShape).border(2.dp, White, CircleShape)
                    .clickable(onClick = onCameraClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PhotoCamera, "Change", tint = White, modifier = Modifier.size(20.dp))
            }
        }
        // ... (텍스트 부분 유지) ...
        Spacer(modifier = Modifier.height(paddingMedium))
        Text(text = name, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(paddingSmall))
        Text(text = description, style = Typography.bodyMedium, color = Gray500)
    }
}

@Composable
fun UserInfoSection(email: String, phone: String, address: String) {
    Column(
        modifier = Modifier.padding(horizontal = paddingLarge),
        verticalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        UserInfoRow(icon = Icons.Default.Email, label = "이메일", value = email, iconBgColor = LightPurple)
        UserInfoRow(icon = Icons.Default.Call, label = "전화번호", value = phone, iconBgColor = LightBlue)
        UserInfoRow(icon = Icons.Default.LocationOn, label = "주소", value = address, iconBgColor = LightGreen)
    }
}

@Composable
fun UserInfoRow(icon: ImageVector, label: String, value: String, iconBgColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = Primary)
            }
            Spacer(modifier = Modifier.width(paddingMedium))
            Column {
                Text(text = label, style = Typography.bodySmall, color = Gray500)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PetInfoSection(
    pets: List<Pet>,
    onEditPet: (Pet) -> Unit,
    onDeletePet: (Pet) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingLarge)
    ) {
        Text(text = "반려견 정보", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(paddingMedium))

        if (pets.isEmpty()) {
            EmptyPetView()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(paddingMedium)) {
                pets.forEach { pet ->
                    PetInfoCard(
                        pet = pet,
                        onEditClicked = { onEditPet(pet) },
                        onDeleteClicked = { onDeletePet(pet) }
                    )
                }
            }
        }
    }
}

@Composable
fun PetInfoCard(
    pet: Pet,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteClicked()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(paddingLarge)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = pet.image ?: cR.drawable.placeholder,
                        contentDescription = pet.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(paddingMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pet.name, style = Typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = pet.breed ?: "", style = Typography.bodyMedium, color = Gray500)
                    }
                }
                Spacer(modifier = Modifier.height(paddingMedium))
                Row(horizontalArrangement = Arrangement.spacedBy(paddingMedium)) {
                    PetDetailChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Cake,
                        label = "생일",
                        value = pet.birthday ?: ""
                    )
                    PetDetailChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.FavoriteBorder,
                        label = "성별",
                        value = pet.gender ?: ""
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("수정") },
                        onClick = {
                            onEditClicked()
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("삭제") },
                        onClick = {
                            showDeleteDialog = true
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("삭제 확인") },
        text = { Text("정말 이 반려견의 정보를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun PetDetailChip(modifier: Modifier = Modifier, icon: ImageVector, label: String, value: String) {
    Row(
        modifier = modifier
            .background(Gray200, RoundedCornerShape(12.dp))
            .padding(paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Primary)
        Spacer(modifier = Modifier.width(paddingSmall))
        Column {
            Text(text = label, style = Typography.bodySmall, color = Gray500)
            Text(text = value, style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EmptyPetView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = cR.drawable.placeholder),
            contentDescription = "Empty Pet",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(paddingMedium))
        Text(
            text = "등록된 반려견이 없어요",
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(paddingSmall))
        Text(
            text = "우측 하단의 '+' 버튼으로\n반려견을 추가하고 산책을 기록해보세요",
            fontSize = 14.sp,
            color = Gray300,
            textAlign = TextAlign.Center
        )
    }
}


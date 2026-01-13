package com.sesac.mypage.presentation.mypage_detail

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sesac.common.R
import com.sesac.common.component.CommonListContainer
import com.sesac.common.ui.theme.Background
import com.sesac.common.ui.theme.Gray200
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.common.utils.FileUtils
import com.sesac.mypage.nav_graph.MypageNavigationRoute
import com.sesac.mypage.presentation.MypageViewModel


@Composable
fun MypageDetailScreen(
    navController: NavController,
    viewModel: MypageViewModel = hiltViewModel(),
    uiState: AuthUiState,
) {
    val pets by viewModel.userPets.collectAsStateWithLifecycle()
    val deletePetState by viewModel.deletePetState.collectAsStateWithLifecycle()
    val invitationCodeState by viewModel.invitationCode.collectAsStateWithLifecycle() // NEW
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
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
                viewModel.updateProfileImage(uiState, imagePart)
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState.user?.id != -1) {
            viewModel.getAllUserPets(uiState)
            viewModel.clearSelectedPet()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Background),
            horizontalAlignment = Alignment.CenterHorizontally,
//            contentPadding = PaddingValues(bottom = 80.dp) // FAB에 가려지지 않도록 패딩 추가
        ) {
            MypageDetailHeader(
                name = uiState.user?.fullName ?: "-",
                description = stringResource(R.string.mypage_detail_header_message),
                imageUrl = uiState.user?.profileImageUrl,
                localImageUri = selectedImageUri,
                onCameraClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            )


            Spacer(modifier = Modifier.height(paddingMedium))

            UserInfoSectionView(
                email = uiState.user?.email ?: "-",
                phone = "-",
                address = "-"
            )


            Spacer(modifier = Modifier.height(paddingLarge))

            HorizontalDivider(thickness = paddingSmall, color = Gray200)


            CommonListContainer(
                title = stringResource(R.string.mypage_pet_info),
                itemList = pets,
                placeHolder = painterResource(R.drawable.placeholder),
                emptyStateMessage = stringResource(R.string.mypage_pet_empty_list),
                emptyStateSubMessage = stringResource(R.string.mypage_pet_empty_list_subtitle),
                itemContent = { pet ->
                    PetInfoCard(
                        pet = pet,
                        onEditClicked = {
                            navController.navigate(MypageNavigationRoute.AddPetScreen(petId = pet.id))
                        },
                        onDeleteClicked = { viewModel.deletePet(uiState, pet.id) }
                    )

                }
            )
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
                    viewModel.generateInvitationCode(uiState) // Trigger code generation
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


package com.sesac.auth.presentation.find_account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sesac.auth.presentation.FindAccountViewModel
import com.sesac.auth.presentation.FindUiState
import androidx.compose.foundation.text.KeyboardOptions // 키보드 옵션용

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAccountScreen(
    viewModel: FindAccountViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("이메일 찾기", "비밀번호 찾기") // [변경] 탭 이름 변경
    val uiState by viewModel.uiState.collectAsState()

    // 결과 처리 (다이얼로그)
    when (val state = uiState) {
        is FindUiState.SuccessId -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("이메일 확인") },
                text = { Text("회원님의 이메일은\n[${state.userId}]\n입니다.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) { Text("확인") }
                }
            )
        }
        is FindUiState.SuccessReset -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("메일 발송 완료") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetState()
                        onNavigateBack()
                    }) { Text("로그인으로 이동") }
                }
            )
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("계정 찾기") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("취소") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.resetState()
                        },
                        text = { Text(title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is FindUiState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val errorMessage = (uiState as? FindUiState.Error)?.message

                when (selectedTabIndex) {
                    0 -> FindEmailContent( // [변경] 함수명 변경
                        error = errorMessage,
                        onFindEmailClick = { name, phone -> viewModel.findEmail(name, phone) }
                    )
                    1 -> FindPasswordContent(
                        error = errorMessage,
                        onResetClick = { name, email -> viewModel.resetPassword(name, email) }
                    )
                }
            }
        }
    }
}

// [변경] 이메일 찾기 화면 (이름 + 휴대폰 번호)
@Composable
fun FindEmailContent(
    error: String?,
    onFindEmailClick: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("휴대폰 번호") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 숫자 키패드
            singleLine = true,
            placeholder = { Text("가입 시 등록한 번호") }
        )

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { onFindEmailClick(name, phoneNumber) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && phoneNumber.isNotEmpty()
        ) {
            Text("이메일 찾기")
        }

        // 안내 문구 추가
        Text(
            text = "* 휴대폰 번호를 등록하지 않은 경우 찾기가 불가능합니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// [변경] 비밀번호 재설정 화면 (이름 + 이메일) -> 아이디 입력 삭제
@Composable
fun FindPasswordContent(
    error: String?,
    onResetClick: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { onResetClick(name, email) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && email.isNotEmpty()
        ) {
            Text("재설정 이메일 발송")
        }
    }
}
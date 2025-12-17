package com.sesac.auth.presentation.join_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.auth.presentation.AuthViewModel
import com.sesac.common.ui_state.JoinUiState
import com.sesac.common.R
import com.sesac.common.component.CommonLabelledTextField
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun AuthJoinScreen(
    nav2Home: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val formState by viewModel.joinFormState.collectAsStateWithLifecycle()
    val joinUiState by viewModel.joinUiState.collectAsStateWithLifecycle()

    LaunchedEffect(joinUiState) {
        if (joinUiState is JoinUiState.Success) {
            nav2Home()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingLarge)
    ) {

        Text(
            text = stringResource(id = R.string.auth_join_welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.auth_join_welcome_message),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(paddingLarge))

        val showError = formState.showValidationErrors
        CommonLabelledTextField(
            value = formState.email,
            onValueChange = viewModel::onEmailChange,
            labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_email_label)) },
            placeholder = { Text("email@example.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = showError && !formState.isEmailValid,
            helperText = if (showError && !formState.isEmailValid) "Invalid email format" else null
        )
        CommonLabelledTextField(
            value = formState.password,
            onValueChange = viewModel::onPasswordChange,
            labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_password_label)) },
            isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = showError && !formState.isPasswordValid,
            helperText = if (showError && !formState.isPasswordValid) stringResource(id = R.string.auth_join_password_helper) else null
        )
        CommonLabelledTextField(
            value = formState.passwordConfirm,
            onValueChange = viewModel::onPasswordConfirmChange,
            labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_password_confirm_label)) },
            isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = showError && !formState.doPasswordsMatch,
            helperText = if (showError && !formState.doPasswordsMatch) "Passwords do not match" else null
        )
        CommonLabelledTextField(
            value = formState.nickname,
            onValueChange = viewModel::onNicknameChange,
            labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_nickname_label)) },
            isError = showError && formState.nickname.isBlank(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CommonLabelledTextField(
                    value = formState.name,
                    onValueChange = viewModel::onNameChange,
                    labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_name_label)) },
                    isError = showError && formState.name.isBlank(),
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CommonLabelledTextField(
                    value = formState.phone,
                    onValueChange = viewModel::onPhoneChange,
                    labelContent = { RequiredLabelView(text = stringResource(id = R.string.auth_join_phone_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        }

        // NEW FIELD: Invitation Code
        CommonLabelledTextField(
            value = formState.invitationCode,
            onValueChange = viewModel::onInvitationCodeChange,
            labelContent = { Text(stringResource(R.string.auth_info_pet_register_code)) },
            placeholder = { Text(stringResource(R.string.auth_input_pet_register_code)) }
        )

        Spacer(modifier = Modifier.height(paddingSmall))

        AgreementSectionView(
            agreeAll = formState.agreeAll,
            onAgreeAllChange = viewModel::onAgreeAllChange,
            agreeAge = formState.agreeAge,
            onAgreeAgeChange = viewModel::onAgreeAgeChange,
            agreeTerms = formState.agreeTerms,
            onAgreeTermsChange = viewModel::onAgreeTermsChange,
            agreePrivacy = formState.agreePrivacy,
            onAgreePrivacyChange = viewModel::onAgreePrivacyChange
        )

        Spacer(modifier = Modifier.height(paddingSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(iconSizeLarge),
            contentAlignment = Alignment.Center
        ) {
            when (joinUiState) {
                is JoinUiState.Loading -> CircularProgressIndicator()
                else -> Button(
                    onClick = viewModel::onJoinClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(id = R.string.auth_join_submit_button),
                        modifier = Modifier.padding(vertical = paddingSmall),
                    )
                }
            }
        }
        if (joinUiState is JoinUiState.Error) {
            Text((joinUiState as JoinUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingLarge)
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            Text(
                text = stringResource(id = R.string.auth_join_or_divider),
                color = Gray500,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
        }

        SocialJoinButtonView(
            text = stringResource(id = R.string.auth_join_kakao_work_button),
            icon = Icons.Default.AccountCircle,
            tint = Color.Yellow,
            onClick = { /* TODO: 카카오워크 가입 */ }
        )
        Spacer(modifier = Modifier.height(paddingLarge))
    }
}
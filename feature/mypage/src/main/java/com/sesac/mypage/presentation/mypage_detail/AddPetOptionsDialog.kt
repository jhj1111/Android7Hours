package com.sesac.mypage.presentation.mypage_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall

// NEW: AddPetOptionsDialog Composable
@Composable
fun AddPetOptionsDialog(
    onDismissRequest: () -> Unit,
    onAddAnimalPet: () -> Unit,
    onInviteUser: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.mypage_select_pet_options)) },
        text = {
            Column {
                Text(stringResource(R.string.mypage_select_pet_options))
                Spacer(modifier = Modifier.Companion.height(paddingMedium))
                Button(
                    onClick = onAddAnimalPet,
                    modifier = Modifier.Companion.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.mypage_create_new_pet), color = White)
                }
                Spacer(modifier = Modifier.Companion.height(paddingSmall))
                Button(
                    onClick = onInviteUser,
                    modifier = Modifier.Companion.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.mypage_create_pet_code), color = White)
                }
            }
        },
        confirmButton = { /* No confirm button needed, actions are in text part */ }
    )
}

@Preview
@Composable
fun AddPetOptionsDialogPreview(){
    Android7HoursTheme {
        AddPetOptionsDialog(
            onAddAnimalPet = {},
            onInviteUser = {},
            onDismissRequest = {},
        )
    }
}
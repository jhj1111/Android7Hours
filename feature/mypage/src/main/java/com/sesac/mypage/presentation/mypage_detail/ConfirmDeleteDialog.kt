package com.sesac.mypage.presentation.mypage_detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${stringResource(R.string.common_action_delete)} ${stringResource(R.string.common_action_confirm)}") },
        text = { Text(stringResource(R.string.mypage_pet_delete_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.common_action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_action_cancel))
            }
        }
    )
}

@Preview
@Composable
fun ConfirmDeleteDialogPreview(){
    Android7HoursTheme {
        ConfirmDeleteDialog(
            onConfirm = {},
            onDismiss = {},
        )
    }
}
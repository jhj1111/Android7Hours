package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.textFieldHeightLarge
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun AddScheduleDialogView(
    selectedDate: LocalDate,
    title: String,
    memo: String,
    onTitleChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dateFormat = stringResource(R.string.common_date_format_yyyyMd)
    val formatter = remember { DateTimeFormatter.ofPattern(dateFormat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.mypage_manage_schedules_text_create)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(paddingMedium)) {
                Text(stringResource(R.string.mypage_manage_schedules_create_in_selected_date))
                Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryPurple,
                    textAlign = TextAlign.Companion.Center,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .background(PrimaryPurpleLight, RoundedCornerShape(paddingSmall))
                        .padding(paddingMedium)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.mypage_manage_schedules_title)) },
                    placeholder = { Text(stringResource(R.string.mypage_manage_schedules_input_title)) },
                    modifier = Modifier.Companion.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = memo,
                    onValueChange = onMemoChange,
                    label = { Text(stringResource(R.string.common_memo)) },
                    placeholder = { Text(stringResource(R.string.common_input_memo)) },
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .heightIn(min = textFieldHeightLarge),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(stringResource(R.string.common_action_add), color = White)
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
fun AddScheduleDialogViewPreview(){
    Android7HoursTheme {
        AddScheduleDialogView(
            selectedDate = LocalDate.of(2000,1,1),
            title = "제목",
            memo = "메모",
            onTitleChange = { _ -> },
            onMemoChange = { _ -> },
            onConfirm = {},
            onDismiss = {},
        )
    }
}
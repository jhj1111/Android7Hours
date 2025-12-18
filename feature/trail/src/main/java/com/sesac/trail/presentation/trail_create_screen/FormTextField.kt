package com.sesac.trail.presentation.trail_create_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.paddingMicro

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

@Preview(showBackground = true)
@Composable
fun FormTextFieldPreview() {
    val textState = remember { mutableStateOf("") }

    FormTextField(
        label = "이름",
        value = textState.value,
        onValueChange = { textState.value = it },
        placeholder = "이름을 입력하세요",
        isRequired = true,
        isError = false
    )
}
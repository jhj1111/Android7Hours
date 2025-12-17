package com.sesac.mypage.presentation.mypage_add_pet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun AddPetFormItem(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.Companion.padding(vertical = paddingMedium).fillMaxWidth()) {
        Text(text = label, style = Typography.bodyLarge, fontWeight = FontWeight.Companion.SemiBold)
        Spacer(modifier = Modifier.Companion.height(paddingSmall))
        content()
    }
}

@Preview
@Composable
fun AddPetFormItemPreview(){
    Android7HoursTheme {
        AddPetFormItem(
            label = "예시",
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("이름을 입력해주세요") },
            )
        }
    }
}
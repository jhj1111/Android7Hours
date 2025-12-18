package com.sesac.trail.presentation.trail_create_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun CreateBottomActions(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isEditing: Boolean,
    isLoading: Boolean,
) {
    Surface(
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            Button(
                onClick = onSave,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple600
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                } else {
                    Text(if (isEditing) "수정하기" else "등록하기", color = White)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateBottomActionsPreview() {
    // 임의의 클릭 이벤트
    val dummyClick: () -> Unit = {}

    // 편집 모드 + 로딩 아님
    CreateBottomActions(
        onCancel = dummyClick,
        onSave = dummyClick,
        isEditing = false,
        isLoading = false
    )
}

@Preview(showBackground = true, name = "Editing + Loading")
@Composable
fun CreateBottomActionsEditingLoadingPreview() {
    val dummyClick: () -> Unit = {}

    // 편집 모드 + 로딩 중
    CreateBottomActions(
        onCancel = dummyClick,
        onSave = dummyClick,
        isEditing = true,
        isLoading = true
    )
}
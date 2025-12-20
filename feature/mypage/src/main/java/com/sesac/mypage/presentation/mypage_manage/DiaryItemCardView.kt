package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.PurpleLight
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.iconSizeMedium
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun DiaryItemCardView(pathName: String, diaryText: String) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFFF0F4FF) // 연한 보라색 배경
            containerColor = PurpleLight // 연한 보라색 배경
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall)
    ) {
        Column(modifier = Modifier.Companion.padding(paddingMedium)) {
            // ✅ 산책로 제목
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically,
                modifier = Modifier.Companion.padding(bottom = paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // 아이콘 추가 필요
                    contentDescription = "산책로",
                    tint = PrimaryPurple,
                    modifier = Modifier.Companion.size(iconSizeMedium)
                )
                Spacer(Modifier.Companion.width(paddingSmall))
                Text(
                    text = pathName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Companion.Bold,
                    color = PrimaryPurple
                )
            }

            // ✅ 구분선
            HorizontalDivider(
                modifier = Modifier.Companion.padding(vertical = paddingSmall),
                thickness = paddingMicro,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // ✅ 다이어리 내용
            Text(
                text = diaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview
@Composable
fun DiaryItemCardViewPreview(){
    Android7HoursTheme {
        DiaryItemCardView(
            pathName = "경로 이름",
            diaryText = "일정",
        )
    }
}
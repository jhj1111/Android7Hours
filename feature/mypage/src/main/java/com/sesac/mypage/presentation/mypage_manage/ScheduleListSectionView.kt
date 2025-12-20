package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.domain.model.MypageSchedule
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun ScheduleListSectionView(
    selectedDate: LocalDate,
    schedules: List<MypageSchedule>,
    onAddClick: () -> Unit
) {
    val dateFormat = stringResource(R.string.common_date_format_Md)
    val formatter = remember { DateTimeFormatter.ofPattern(dateFormat) }

    if (schedules.isEmpty()) {
        Surface(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(vertical = paddingLarge),
            shape = RoundedCornerShape(paddingMedium),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.Companion.padding(paddingLarge * 2)
            ) {
                Text(
                    text = "${selectedDate.format(formatter)} ${stringResource(R.string.mypate_manage_no_schedules)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.Companion.height(paddingMedium))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(stringResource(R.string.mypage_manage_schedules_create))
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Text(
                text = "${selectedDate.format(formatter)}${stringResource(R.string.mypage_manage_schedules_date_of)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Companion.Bold
            )
            Box(
                modifier = Modifier.Companion
                    .background(PrimaryPurpleLight, shape = CircleShape)
                    .padding(horizontal = paddingMedium, vertical = paddingMicro),
                contentAlignment = Alignment.Companion.Center
            ) {
                Text(
                    text = "${schedules.size}개",
                    color = PrimaryPurple,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Companion.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun ScheduleListSectionViewPreview(){
    Android7HoursTheme {
        ScheduleListSectionView(
            selectedDate = LocalDate.of(2000,1,1),
            schedules = listOf(MypageSchedule.Empty),
            onAddClick = {},
        )
    }
}
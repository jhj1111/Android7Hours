package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.component.CommonLoading
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.iconSize
import com.sesac.common.ui.theme.paddingBottom
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.MypageSchedule
import org.threeten.bp.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContentView(
    modifier: Modifier = Modifier.Companion,
    selectedDate: LocalDate,
    schedules: List<MypageSchedule>,
    datePickerState: DatePickerState,
    onAddClick: () -> Unit,
    loadDiaryFromLocal: (Long) -> Unit,
    onDeleteClick: (MypageSchedule) -> Unit,
    diaryMap: Map<Long, String>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = paddingMedium),
        contentPadding = PaddingValues(bottom = paddingBottom)
    ) {
        item {
            ScheduleCalendarView(
                datePickerState = datePickerState,
            )
            Spacer(Modifier.Companion.height(paddingLarge))
        }

        item {
            ScheduleListSectionView(
                selectedDate = selectedDate,
                schedules = schedules,
                onAddClick = onAddClick
            )
            Spacer(Modifier.Companion.height(paddingMedium))
        }

        if (schedules.isNotEmpty()) {
            items(schedules, key = { it.id }) { schedule ->

                // ✅ 완료된 산책로 일정이면 ScheduleItemCard 렌더링하지 않음
                if (!(schedule.isPath && schedule.isCompleted)) {
                    ScheduleItemCardView(
                        schedule = schedule,
                        onDeleteClick = { onDeleteClick(schedule) }
                    )
                    Spacer(Modifier.Companion.height(paddingSmall))
                }

                // ✅ 산책로 일정 완료하면 다이어리 보여주기
                if (schedule.isPath && schedule.isCompleted) {
                    val diary = diaryMap[schedule.id]

                    // Room에서 메모리에 없으면 불러오기
                    LaunchedEffect(schedule.id) {
                        if (diary.isNullOrEmpty()) {
                            loadDiaryFromLocal(schedule.id)
                        }
                    }

                    Spacer(Modifier.Companion.height(paddingMedium))

                    // 다이어리 섹션 헤더
                    Text(
                        text = stringResource(R.string.mypage_manage_schedules_today),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Companion.Bold,
                        color = PrimaryPurple,
                        modifier = Modifier.Companion.padding(vertical = paddingSmall)
                    )

                    if (!diary.isNullOrEmpty()) {
                        DiaryItemCardView(
                            pathName = schedule.title,
                            diaryText = diary
                        )
                    } else {
                        CommonLoading(
                            modifier = Modifier.size(iconSize),
                            text = stringResource(R.string.mypage_manage_schedules_loading),
                            backgroundColor = PrimaryPurple,
                        )
                    }

                    Spacer(Modifier.Companion.height(paddingSmall))
                }
            }
        }
    }
}

@Preview
@Composable
fun ScheduleContentPreview(){
    Android7HoursTheme {
        ScheduleContentView(
            selectedDate = LocalDate.of(2000, 2, 1),
            schedules = listOf(MypageSchedule.Empty),
            datePickerState = rememberDatePickerState(),
            onAddClick = {},
            loadDiaryFromLocal = { _ -> Unit },
            onDeleteClick = { _ -> Unit },
            diaryMap = mapOf(Pair(0L, "")),
        )
    }
}
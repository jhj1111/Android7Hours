package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
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
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Diary
import com.sesac.domain.model.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContentView(
    modifier: Modifier = Modifier,
    datePickerState: DatePickerState,
    paths: List<Path>,
    diariesState: Map<Int, ResponseUiState<Diary>>,
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
            Spacer(Modifier.height(paddingLarge))
        }

        item {
            Text(
                text = stringResource(R.string.mypage_manage_schedules_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier.padding(vertical = paddingSmall)
            )
        }

        if (paths.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(vertical = paddingLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "이 날짜에 산책 기록이 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(paths, key = { it.id }) { path ->
                val diaryState = diariesState[path.id]
                when (diaryState) {
                    is ResponseUiState.Success -> {
                        DiaryItemCardView(
                            pathName = path.pathName,
                            diaryText = diaryState.result.diary ?: "작성된 일기가 없습니다."
                        )
                    }

                    is ResponseUiState.Error -> {
                        DiaryItemCardView(
                            pathName = path.pathName,
                            diaryText = "일기를 불러오는데 실패했습니다: ${diaryState.message}"
                        )
                    }

                    is ResponseUiState.Loading, is ResponseUiState.Idle, null -> {
                        CommonLoading(
                            modifier = Modifier.size(iconSize),
                            text = stringResource(R.string.mypage_manage_schedules_loading),
                            backgroundColor = PrimaryPurple,
                        )
                    }
                }
                Spacer(Modifier.height(paddingSmall))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ScheduleContentPreview() {
    Android7HoursTheme {
        ScheduleContentView(
            datePickerState = rememberDatePickerState(),
            paths = listOf(
                Path.EMPTY.copy(id = 1, pathName = "한강 공원 산책", pathComment = ""),
                Path.EMPTY.copy(id = 2, pathName = "서울숲 강아지랑", pathComment = "")
            ),
            diariesState = mapOf(
                1 to ResponseUiState.Success("Success", Diary(diary = "오늘 한강공원에 다녀왔다. 날씨가 정말 좋아서 강아지도 신나게 뛰어놀았다. 다음에도 또 와야지!")),
                2 to ResponseUiState.Loading
            )
        )
    }
}
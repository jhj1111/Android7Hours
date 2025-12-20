package com.sesac.mypage.presentation.mypage_manage

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.R
import com.sesac.common.ui.theme.Gray50
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.White
import com.sesac.domain.model.MypageSchedule
import com.sesac.mypage.presentation.MypageViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MypageManageScreen(viewModel: MypageViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val diaryMap by viewModel.diaryMap.collectAsStateWithLifecycle()
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var scheduleTitle by remember { mutableStateOf("") }
    var scheduleMemo by remember { mutableStateOf("") }
    val onDateSelected = { newDate: LocalDate -> selectedDate = newDate }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()

    val textCreateDateSucceeded = stringResource(R.string.mypage_schedules_created_succeed)

    LaunchedEffect(selectedDate) {
        viewModel.getSchedules(selectedDate)
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val newDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if (newDate != selectedDate) {
                onDateSelected(newDate)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = PrimaryPurple,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "일정 추가")
            }
        },
        containerColor = Gray50
    ) { paddingValues ->

        ScheduleContentView(
            modifier = Modifier.padding(paddingValues),
            selectedDate = selectedDate,
            schedules = schedules,
            datePickerState = datePickerState,
            onAddClick = { isAddDialogOpen = true },
            loadDiaryFromLocal = viewModel::loadDiaryFromLocal,
            onDeleteClick = { schedule -> viewModel.deleteSchedule(schedule) },
            diaryMap = diaryMap,
        )
    }

    if (isAddDialogOpen) {
        AddScheduleDialogView(
            selectedDate = selectedDate,
            title = scheduleTitle,
            memo = scheduleMemo,
            onTitleChange = { scheduleTitle = it },
            onMemoChange = { scheduleMemo = it },
            onDismiss = { isAddDialogOpen = false },
            onConfirm = {
                if (scheduleTitle.isNotBlank()) {
                    viewModel.addSchedule(
                        MypageSchedule(
                            id = System.currentTimeMillis(),
                            date = selectedDate,
                            title = scheduleTitle,
                            memo = scheduleMemo,
                            isPath = false,        // ✅ 추가
                            pathId = null,         // ✅ 추가
                            isCompleted = false    // ✅ 추가
                        )
                    )
                    scheduleTitle = ""
                    scheduleMemo = ""
                    isAddDialogOpen = false
                    Toast.makeText(context, textCreateDateSucceeded, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}
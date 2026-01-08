package com.sesac.mypage.presentation.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.MypageSchedule
import com.sesac.mypage.presentation.MypageViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MypageManageScreen(viewModel: MypageViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var scheduleTitle by remember { mutableStateOf("") }
    var scheduleMemo by remember { mutableStateOf("") }

    val schedules by viewModel.schedules.collectAsStateWithLifecycle()

    LaunchedEffect(selectedDate) {
        viewModel.getSchedules(selectedDate)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = PrimaryPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "일정 추가")
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->

        ScheduleContent(
            modifier = Modifier.padding(paddingValues),
            selectedDate = selectedDate,
            schedules = schedules,
            onDateSelected = { newDate -> selectedDate = newDate },
            onAddClick = { isAddDialogOpen = true },
            onDeleteClick = { schedule -> viewModel.deleteSchedule(schedule) },
            viewModel = viewModel
        )
    }

    if (isAddDialogOpen) {
        AddScheduleDialog(
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
                    Toast.makeText(context, "일정이 추가되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleContent(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    schedules: List<MypageSchedule>,
    onDateSelected: (LocalDate) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (MypageSchedule) -> Unit,
    viewModel: MypageViewModel
) {
    // diaryMap 상태 관찰
    val diaryMap by viewModel.diaryMap.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = paddingMedium),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            ScheduleCalendar(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
            Spacer(Modifier.height(paddingLarge))
        }

        item {
            ScheduleListSection(
                selectedDate = selectedDate,
                schedules = schedules,
                onAddClick = onAddClick
            )
            Spacer(Modifier.height(paddingMedium))
        }

        if (schedules.isNotEmpty()) {
            items(schedules, key = { it.id }) { schedule ->

                // ✅ 완료된 산책로 일정이면 ScheduleItemCard 렌더링하지 않음
                if (!(schedule.isPath && schedule.isCompleted)) {
                    ScheduleItemCard(
                        schedule = schedule,
                        onDeleteClick = { onDeleteClick(schedule) }
                    )
                    Spacer(Modifier.height(paddingSmall))
                }

                // ✅ 산책로 일정 완료하면 다이어리 보여주기
                if (schedule.isPath && schedule.isCompleted) {
                    val diary = diaryMap[schedule.id]

                    // Room에서 메모리에 없으면 불러오기
                    LaunchedEffect(schedule.id) {
                        if (diary.isNullOrEmpty()) {
                            viewModel.loadDiary(schedule.id, schedule.pathId)
//                            viewModel.loadDiaryFromLocal(schedule.id)

                        }
                    }

                    Spacer(Modifier.height(paddingMedium))

                    // 다이어리 섹션 헤더
                    Text(
                        text = "🐕 오늘의 산책로 일기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        modifier = Modifier.padding(vertical = paddingSmall)
                    )

                    if (!diary.isNullOrEmpty()) {
                        DiaryItemCard(
                            pathName = schedule.title,
                            diaryText = diary
                        )
                    } else {
                        DiaryLoadingCard()
                    }

                    Spacer(Modifier.height(paddingSmall))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
            modifier = Modifier.padding(paddingSmall)
        )
    }
}

@Composable
private fun ScheduleListSection(
    selectedDate: LocalDate,
    schedules: List<MypageSchedule>,
    onAddClick: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("M월 d일") }

    if (schedules.isEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingLarge),
            shape = RoundedCornerShape(paddingMedium),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(paddingLarge * 2)
            ) {
                Text(
                    text = "${selectedDate.format(formatter)} 일정이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(paddingMedium))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("일정 추가하기")
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedDate.format(formatter)}의 일정",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .background(PrimaryPurpleLight, shape = CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${schedules.size}개",
                    color = PrimaryPurple,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(
    schedule: MypageSchedule,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(paddingMedium),
            verticalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Red500
                    )
                }
            }

            if (schedule.memo.isNotBlank()) {
                Text(
                    text = schedule.memo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(paddingSmall)
                        )
                        .padding(paddingMedium)
                )
            }
        }
    }
}

@Composable
private fun AddScheduleDialog(
    selectedDate: LocalDate,
    title: String,
    memo: String,
    onTitleChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("일정 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(paddingMedium)) {
                Text("선택한 날짜에 새로운 일정을 추가하세요.")
                Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryPurple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryPurpleLight, RoundedCornerShape(paddingSmall))
                        .padding(paddingMedium)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("일정 제목") },
                    placeholder = { Text("일정 제목을 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = memo,
                    onValueChange = onMemoChange,
                    label = { Text("메모") },
                    placeholder = { Text("메모를 입력하세요") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun DiaryItemCard(pathName: String, diaryText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F4FF) // 연한 보라색 배경
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(paddingMedium)) {
            // ✅ 산책로 제목
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // 아이콘 추가 필요
                    contentDescription = "산책로",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(paddingSmall))
                Text(
                    text = pathName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            // ✅ 구분선
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = paddingSmall)
            )

            // ✅ 다이어리 내용
            Text(
                text = diaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}

// ✅ 로딩 카드 추가
@Composable
fun DiaryLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F4FF)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(paddingMedium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryPurple
            )
            Spacer(Modifier.height(paddingSmall))
            Text(
                text = "일기를 불러오는 중...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@SuppressLint("RememberReturnType")
@Preview(showBackground = true)
@Composable
fun MyPageManageScreenPreview() {
    val context = LocalContext.current
    remember {
        com.jakewharton.threetenabp.AndroidThreeTen.init(context)
    }
    MypageManageScreen ()
}
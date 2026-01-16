package com.sesac.mypage.presentation.mypage_manage

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Path
import com.sesac.mypage.presentation.MypageViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MypageManageScreen(
    viewModel: MypageViewModel,
    uiState: AuthUiState,
) {
    val context = LocalContext.current
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val onDateSelected = { newDate: LocalDate -> selectedDate = newDate }
    val myPathListState by viewModel.myPathList.collectAsState()
    val diariesState by viewModel.diariesState.collectAsState()
    var pathsForSelectedDate by rememberSaveable { mutableStateOf<List<Path>>(emptyList()) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    LaunchedEffect(Unit, uiState) {
        if (uiState.isLoggedIn) {
            viewModel.getMyPathList(uiState)
        }
    }

    LaunchedEffect(myPathListState, selectedDate) {
        val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        if (myPathListState is ResponseUiState.Success) {
            val allPaths = (myPathListState as ResponseUiState.Success<List<Path>>).result
            pathsForSelectedDate = allPaths.filter { dateParser.parse(it.createdAt ?: return@LaunchedEffect) == dateParser.parse(selectedDate.toString()) }
            val pathIds = pathsForSelectedDate.map { it.id }
            viewModel.getDiaries(pathIds)
        }
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

    if (myPathListState is ResponseUiState.Error) {
        Toast.makeText(context, (myPathListState as ResponseUiState.Error).message, Toast.LENGTH_SHORT).show()
    }

    ScheduleContentView(
        datePickerState = datePickerState,
        paths = pathsForSelectedDate,
        diariesState = diariesState
    )
}
package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import org.threeten.bp.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCalendarView(
    datePickerState: DatePickerState,
) {
    Surface(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        shadowElevation = elevationSmall,
        color = MaterialTheme.colorScheme.surface
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
            modifier = Modifier.Companion.padding(paddingSmall)
        )
    }
}

@Preview
@Composable
fun ScheduleCalendarViewPreview(){
    Android7HoursTheme {
        val datePickerState = rememberDatePickerState()
        ScheduleCalendarView(
            datePickerState = datePickerState,
        )
    }
}
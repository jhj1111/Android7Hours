package com.sesac.mypage.presentation.mypage_manage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.iconSize
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.MypageSchedule
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

@Composable
fun ScheduleItemCardView(
    schedule: MypageSchedule,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        border = BorderStroke(borderMicro, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.Companion.padding(paddingMedium),
            verticalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.Top
            ) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Companion.Bold,
                    modifier = Modifier.Companion.weight(1f)
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.Companion.size(iconSize)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.common_action_cancel),
                        tint = Red500
                    )
                }
            }

            if (schedule.memo.isNotBlank()) {
                Text(
                    text = schedule.memo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                paddingSmall
                            )
                        )
                        .padding(paddingMedium)
                )
            }
        }
    }
}

@Preview
@Composable
fun ScheduleItemCardViewPreview(){
    Android7HoursTheme {
        ScheduleItemCardView(
            schedule = MypageSchedule.Empty.copy(
                title = "제목",
                memo = "메모",
                isCompleted = true,
            ),
            onDeleteClick = {},
        )
    }
}
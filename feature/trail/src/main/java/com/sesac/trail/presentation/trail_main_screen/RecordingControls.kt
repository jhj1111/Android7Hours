package com.sesac.trail.presentation.trail_main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import java.util.concurrent.TimeUnit

@Composable
fun RecordingControls(
    recordingTime: Long,
    onStopRecording: () -> Unit
) {
    // 시간 포맷팅
    val formattedTime = remember(recordingTime) {
        val minutes = TimeUnit.SECONDS.toMinutes(recordingTime)
        val seconds = recordingTime % 60
        "%02d:%02d".format(minutes, seconds)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        // Recording Info
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
            color = White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                Red500,
                                CircleShape
                            )
                    )
                    Spacer(Modifier.width(paddingMicro))
                    Text(
                        text = "기록 중",
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(formattedTime, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(paddingSmall)) {
//                    Text("0.0 km", fontSize = 14.sp, color = Color.Gray)
//                    Text("•", fontSize = 14.sp, color = Color.Gray)
//                    Text("0 걸음", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        // Control Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingLarge)
        ) {
            // Stop Button
            LargeFloatingActionButton(
                onClick = onStopRecording,
                containerColor = Red500,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Filled.Stop, contentDescription = "기록 중지", modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Preview
@Composable
fun RecordingControlsPreview() {
    Android7HoursTheme {
        RecordingControls(
            recordingTime = 0,
            onStopRecording = {},
        )
    }
}
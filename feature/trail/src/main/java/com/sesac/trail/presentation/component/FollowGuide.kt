package com.sesac.trail.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.trail.presentation.TrailFollowViewModel
import com.sesac.trail.utils.toLatLng

@Composable
fun FollowGuide(viewModel: TrailFollowViewModel,
                onStopFollowing: () -> Unit,
                modifier: Modifier = Modifier
) {
    val offRoute by viewModel.offRoute.collectAsStateWithLifecycle()
    val remainingDistance by viewModel.remainingDistance.collectAsStateWithLifecycle()
    val isCompleted by viewModel.isRouteCompleted.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocationMarker.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        color = when {
            isCompleted -> Color(0xFFB2DFDB) // 완료 (청록색)
            offRoute -> Color(0xFFFFCDD2) // 이탈 (빨간색)
            else -> Color(0xFFE8F5E9) // 정상 (초록색)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 상태 표시
            when {
                isCompleted -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle, // ✅ 아이콘 추가 필요
                            contentDescription = null,
                            tint = Color(0xFF00897B)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "🎉 경로 완료!",
                            color = Color(0xFF00897B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                offRoute -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "경로에서 벗어났습니다",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    Text("경로 안내 중", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Refactored conditional text display
            if (!isCompleted) {
                // 🔹 남은 거리
                Text(
                    "도착까지 ${String.format("%.0f", remainingDistance)}m",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                // 🔹 목적지까지 직선거리
                selectedPath?.coord?.lastOrNull()?.let { destination ->
                    userLocation?.let { current ->
                        val directDistance = current.distanceTo(destination.toLatLng())
                        Text(
                            "직선거리: ${String.format("%.0f", directDistance)}m",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Text(
                    "수고하셨습니다!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00897B)
                )
            }

            Spacer(Modifier.height(16.dp))
            // ✅ 종료 버튼
            Button(
                onClick = onStopFollowing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) Color(0xFF00897B) else MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isCompleted) "완료" else "따라가기 종료",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
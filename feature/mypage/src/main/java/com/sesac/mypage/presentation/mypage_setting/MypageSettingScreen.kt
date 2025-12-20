package com.sesac.mypage.presentation.mypage_setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sesac.common.R
import com.sesac.common.ui.theme.TextPrimary
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.MypagePermission
import com.sesac.mypage.presentation.MypageViewModel

val permissions = listOf(
    MypagePermission("CAMERA", "CameraAlt", "카메라", "산책 중 사진 및 영상 촬영", "Purple"),
    MypagePermission("GPS", "LocationOn", "GPS", "위치 기반 산책로 추천 및 기록", "Blue"),
    MypagePermission("NOTIFICATION", "Notifications", "알림", "산책 알림 및 커뮤니티 소식", "Green")
)

@Composable
fun MypageSettingScreen(
    viewModel: MypageViewModel = hiltViewModel(),
    permissionStates: SnapshotStateMap<String, Boolean> = remember { mutableStateMapOf<String, Boolean>() },
) {
    LaunchedEffect(permissions) {
        permissions.forEach { permission ->
            if (!permissionStates.containsKey(permission.key)) {
                permissionStates[permission.key] = true
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = paddingLarge)
    ) {
        item {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingLarge)
                ) {
                    Text(
                        text = stringResource(id = R.string.mypage_setting_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(paddingSmall))
                    Text(
                        text = stringResource(id = R.string.mypage_setting_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(paddingLarge))

                    permissions.forEach { item ->
                        PermissionCardView(
                            item = item,
                            isEnabled = permissionStates[item.key] ?: false,
                            onToggle = {
                                val newState = !(permissionStates[item.key] ?: false)
                                permissionStates[item.key] = newState
                                viewModel.updatePermission(item.key, newState)
                            }
                        )
                        Spacer(modifier = Modifier.height(paddingMedium))
                    }
                }
            }
        }
        item {
            InfoBoxView(
                modifier = Modifier.padding(
                    horizontal = paddingLarge,
                    vertical = paddingMedium
                )
            )
        }
        item {
            PrivacyNoteView(
                modifier = Modifier.padding(horizontal = paddingLarge)
            )
        }
    }
}
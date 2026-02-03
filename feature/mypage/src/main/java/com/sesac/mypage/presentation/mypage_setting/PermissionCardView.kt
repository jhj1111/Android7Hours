package com.sesac.mypage.presentation.mypage_setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Border
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Surface
import com.sesac.common.ui.theme.TextPrimary
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.brushBlue
import com.sesac.common.ui.theme.brushGreen
import com.sesac.common.ui.theme.brushPurple
import com.sesac.common.ui.theme.iconBoxSize
import com.sesac.common.ui.theme.iconSize
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.permEnabledBg
import com.sesac.common.ui.theme.permEnabledBorder
import com.sesac.common.ui.theme.shapeCard
import com.sesac.common.ui.theme.shapeIcon
import com.sesac.domain.model.MypagePermission

@Composable
fun PermissionCardView(
    item: MypagePermission,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    val icon = when (item.iconName) {
        "CameraAlt" -> Icons.Default.CameraAlt
        "LocationOn" -> Icons.Default.LocationOn
        "Notifications" -> Icons.Default.Notifications
        else -> Icons.Default.CameraAlt
    }
    val brush = when (item.colorName) {
        "Purple" -> brushPurple
        "Blue" -> brushBlue
        "Green" -> brushGreen
        else -> brushPurple
    }

    val label = when (item.key) {
        "CAMERA" -> stringResource(id = R.string.mypage_permission_camera_label)
        "GPS" -> stringResource(id = R.string.mypage_permission_gps_label)
        "NOTIFICATION" -> stringResource(id = R.string.mypage_permission_notification_label)
        else -> item.label
    }

    val description = when (item.key) {
        "CAMERA" -> stringResource(id = R.string.mypage_permission_camera_desc)
        "GPS" -> stringResource(id = R.string.mypage_permission_gps_desc)
        "NOTIFICATION" -> stringResource(id = R.string.mypage_permission_notification_desc)
        else -> item.description
    }

    val borderColor = if (isEnabled) permEnabledBorder else Border
    val backgroundColor = if (isEnabled) permEnabledBg else Surface

    Surface(
        shape = shapeCard,
        color = backgroundColor,
        border = BorderStroke(borderMicro, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingMedium),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(iconBoxSize)
                    .clip(shapeIcon)
                    .background(brush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = White,
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.width(paddingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = White,
                            uncheckedTrackColor = Border
                        )
                    )
                }

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                AnimatedVisibility(visible = isEnabled) {
                    EnabledBadgeView(
                        modifier = Modifier.padding(top = paddingSmall)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "PermissionCardView - Enabled")
@Composable
fun PermissionCardViewEnabledPreview() {
    val permission = MypagePermission("CAMERA", "CameraAlt", "카메라", "산책 중 사진 및 영상 촬영", "Purple")
    Android7HoursTheme {
        PermissionCardView(item = permission, isEnabled = true, onToggle = {})
    }
}

@Preview(showBackground = true, name = "PermissionCardView - Disabled")
@Composable
fun PermissionCardViewDisabledPreview() {
    val permission = MypagePermission("GPS", "LocationOn", "GPS", "위치 기반 산책로 추천 및 기록", "Blue")
    Android7HoursTheme {
        PermissionCardView(item = permission, isEnabled = false, onToggle = {})
    }
}

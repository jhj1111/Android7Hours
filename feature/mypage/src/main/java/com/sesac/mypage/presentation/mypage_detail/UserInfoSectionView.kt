package com.sesac.mypage.presentation.mypage_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.LightBlue
import com.sesac.common.ui.theme.LightGreen
import com.sesac.common.ui.theme.LightPurple
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingMicro

@Composable
fun UserInfoSectionView(email: String, phone: String, address: String) {
    Column(
        modifier = Modifier.Companion.padding(horizontal = paddingLarge),
        verticalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        UserInfoRow(
            icon = Icons.Default.Email,
            label = stringResource(R.string.mypage_menu_email),
            value = email,
            iconBgColor = LightPurple
        )
        UserInfoRow(
            icon = Icons.Default.Call,
            label = stringResource(R.string.mypage_menu_phone),
            value = phone,
            iconBgColor = LightBlue
        )
        UserInfoRow(
            icon = Icons.Default.LocationOn,
            label = stringResource(R.string.mypage_menu_address),
            value = address,
            iconBgColor = LightGreen
        )
    }
}

@Composable
fun UserInfoRow(icon: ImageVector, label: String, value: String, iconBgColor: Color) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall)
    ) {
        Row(
            modifier = Modifier.Companion.padding(paddingLarge),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Box(
                modifier = Modifier.Companion
                    .size(iconSizeLarge)
                    .background(
                        iconBgColor,
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Companion.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = Primary)
            }
            Spacer(modifier = Modifier.Companion.width(paddingMedium))
            Column {
                Text(text = label, style = Typography.bodySmall, color = Gray500)
                Spacer(modifier = Modifier.Companion.height(paddingMicro))
                Text(
                    text = value,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Companion.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun UserInfoSectionViewPreview(){
    Android7HoursTheme {
        UserInfoSectionView(
            email = "aaa@bbb.com",
            phone = "010-1111-2222",
            address = "서울시 인천구",
        )
    }
}
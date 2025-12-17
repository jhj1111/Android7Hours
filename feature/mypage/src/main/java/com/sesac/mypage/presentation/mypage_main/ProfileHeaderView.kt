package com.sesac.mypage.presentation.mypage_main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.AccentGreen
import com.sesac.common.ui.theme.Gray400
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.avatarSizeLarge
import com.sesac.common.ui.theme.borderSmall
import com.sesac.common.ui.theme.iconSize
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.utils.sampleIconImageUrl

@Composable
fun ProfileHeaderView(
    name: String,
    email: String,
    imageUrl: String,
    onNavigateToProfile: () -> Unit
) {
    Surface(color = White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(avatarSizeLarge)
                        .clip(CircleShape)
                        .border(borderSmall, White, CircleShape),
                    contentScale = ContentScale.Crop,
//                    placeholder = painterResource(id = R.drawable.placeholder) // ⚠️ placeholder 이미지 추가
                )
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(AccentGreen, CircleShape)
                        .border(borderSmall, White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
            Spacer(modifier = Modifier.width(paddingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "프로필 수정",
                    tint = Gray400
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
    ProfileHeaderView(
        name = "홍길동",
        email = "hong@example.com",
        imageUrl = sampleIconImageUrl,
        onNavigateToProfile = { }
    )
}
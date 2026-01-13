package com.sesac.mypage.presentation.mypage_detail

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.iconPhotoInnerSize
import com.sesac.common.ui.theme.iconPhotoSize
import com.sesac.common.ui.theme.iconSizeExtremeLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.config.defaultProfileImageUrl
import com.sesac.common.config.sampleBannerImageUrl

@Composable
fun MypageDetailHeader(
    name: String,
    description: String,
    imageUrl: String?,
    localImageUri: Uri?,
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(top = paddingLarge, start = paddingLarge, end = paddingLarge),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Box {
            // [핵심] key를 사용하여 URL이 바뀌면 강제로 새로 그림
            key(localImageUri, imageUrl) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        // 1순위: 방금 갤러리 선택 사진 / 2순위: 서버 사진 / 3순위: 기본 사진
                        .data(localImageUri ?: imageUrl ?: defaultProfileImageUrl)
                        // [중요] 캐시 끄기: 이미지가 갱신 안 되는 문제 해결
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.Companion
                        .size(iconSizeExtremeLarge)
                        .clip(CircleShape)
                        .border(borderMicro, White, CircleShape),
                    contentScale = ContentScale.Companion.Crop,
                    placeholder = painterResource(id = R.drawable.placeholder),
                    error = painterResource(id = R.drawable.placeholder),
                    // 에러 발생 시 로그 출력 (디버깅용)
                    onError = { Log.e("IMAGE_LOAD", "실패 원인: ${it.result.throwable.message}") }
                )
            }

            // ... (카메라 아이콘 등 기존 코드 유지) ...
            Box(
                modifier = Modifier.Companion
                    .align(Alignment.Companion.BottomEnd)
                    .size(iconPhotoSize)
                    .background(Primary, CircleShape).border(borderMicro, White, CircleShape)
                    .clickable(onClick = onCameraClick),
                contentAlignment = Alignment.Companion.Center
            ) {
                Icon(
                    Icons.Default.PhotoCamera,
                    "Change",
                    tint = White,
                    modifier = Modifier.Companion.size(iconPhotoInnerSize)
                )
            }
        }
        // ... (텍스트 부분 유지) ...
        Spacer(modifier = Modifier.Companion.height(paddingMedium))
        Text(text = name, style = Typography.titleLarge, fontWeight = FontWeight.Companion.Bold)
        Spacer(modifier = Modifier.Companion.height(paddingSmall))
        Text(text = description, style = Typography.bodyMedium, color = Gray500)
    }
}

@Preview
@Composable
fun MypageDetailHeaderPreview(){
    Android7HoursTheme {
        MypageDetailHeader(
            name = "고길동",
            description = "설명충",
            imageUrl = sampleBannerImageUrl,
            localImageUri = null,
            onCameraClick = {},
        )
    }
}
package com.sesac.mypage.presentation.mypage_add_pet

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.cardHeightSmall
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.utils.samplePathUrl

/**
* @param modifier : galleryLauncher.launch("type") 설정
* @param imageUri : 선택된 이미지 출력
* @param imageUrl : 갤러리에서 이미지 선택
*/
@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    imageUrl: String?,
) {
    Box(
        modifier = modifier
            .size(cardHeightSmall)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(borderMicro, Primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri == null && imageUrl == null) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Add Photo",
                modifier = Modifier.size(iconSizeLarge),
                tint = Primary
            )
        } else {
            AsyncImage(
                model = imageUri ?: imageUrl,
                contentDescription = "Pet Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
fun ImagePickerPreview(){
    Android7HoursTheme {
        ImagePicker(
            imageUri = null,
            imageUrl = null,
        )
    }
}

@Preview
@Composable
fun ImagePickerImageSelectedPreview(){
    Android7HoursTheme {
        ImagePicker(
            imageUri = null,
            imageUrl = samplePathUrl,
        )
    }
}
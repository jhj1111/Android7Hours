package com.sesac.home.presentation.home_main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Black
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.config.sampleBannerImageUrl
import com.sesac.domain.model.BannerData

@Composable
fun BannerCardContentView(banner: BannerData?) {
    val context = LocalContext.current

    Box(modifier = Modifier.Companion.fillMaxSize()) {
        if (banner?.image == "drawable_banner_image") {
            Image(
                painter = painterResource(id = R.drawable.icons8_dog_50),
                contentDescription = banner.title,
                modifier = Modifier.Companion.fillMaxSize(),
                contentScale = ContentScale.Companion.FillWidth
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(banner?.image)
                    .crossfade(true)
                    .scale(Scale.FILL)
                    .build(),
                contentDescription = banner?.title,
                modifier = Modifier.Companion.fillMaxSize(),
                contentScale = ContentScale.Companion.Crop,
            )
        }

        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(
                    Brush.Companion.verticalGradient(
                        colors = listOf(Color.Companion.Transparent, Black.copy(alpha = 0.7f)),
                        startY = 300f
                    )
                )
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingLarge),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = banner?.title ?: stringResource(R.string.home_banner_empty),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(paddingMicro))
            Text(
                text = banner?.subtitle ?: stringResource(R.string.home_banner_empty),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Companion.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun BannerCardContentViewPreview(){
    Android7HoursTheme {
        BannerCardContentView(BannerData.EMPTY.copy(image = sampleBannerImageUrl, title = "제목", subtitle = "부제목"))
    }
}
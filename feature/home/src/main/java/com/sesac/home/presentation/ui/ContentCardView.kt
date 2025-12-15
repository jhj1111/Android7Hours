package com.sesac.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.sesac.common.model.PathParceler
import com.sesac.common.ui.theme.cardHeight
import com.sesac.common.ui.theme.cardRound
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.utils.fixImageUrl
import com.sesac.domain.model.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentCardView(data: Path?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        onClick = onClick,
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(cardRound)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data?.thumbnail)
                    .crossfade(true)
                    .scale(Scale.FILL)
                    .build(),
                contentDescription = data?.pathName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 200f
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(paddingMedium),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = data?.pathName ?: "정보 없음",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
package com.sesac.trail.presentation.trail_detail_screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.utils.fixImageUrl

@Composable
fun PathImageHeader(
    pathName: String,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    imageUrl: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
    ) {
        Log.d("TAG-TrailDetailScreen", "imageUrl : ${fixImageUrl(imageUrl)}")
        AsyncImage(
            model = fixImageUrl(imageUrl),
            contentDescription = pathName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        FloatingActionButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(paddingLarge),
            containerColor = if (isBookmarked) Purple600 else MaterialTheme.colorScheme.surface,
            contentColor = if (isBookmarked) White else GrayTabText,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "좋아요"
            )
        }
    }
}
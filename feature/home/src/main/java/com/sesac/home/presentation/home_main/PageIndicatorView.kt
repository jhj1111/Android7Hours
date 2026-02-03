package com.sesac.home.presentation.home_main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.iconSizeMedium
import com.sesac.common.ui.theme.iconSizeMico
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall

// PageIndicator
@Composable
fun PageIndicatorView(isSelected: Boolean) {
    val width = if (isSelected) iconSizeMedium else iconSizeMico
    Box(
        modifier = Modifier.Companion
            .padding(horizontal = paddingMicro)
            .height(paddingSmall)
            .width(width)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Preview
@Composable
fun PageIndicatorViewPreview(){
    Android7HoursTheme {
        Column {
            PageIndicatorView(
                isSelected = true
            )
            Spacer(Modifier.padding(paddingMicro))
            PageIndicatorView(
                isSelected = false
            )

        }
    }
}


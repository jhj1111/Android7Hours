package com.sesac.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.TextDisabled
import com.sesac.common.ui.theme.cardImageSizeSmall
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun CommonEmptyState(
    placeHolder: Any = Icons.Default.Star,
    message: String,
    subMessage: String,
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when(placeHolder){
            is ImageVector -> {
                Icon(
                    placeHolder,
                    contentDescription = "Empty",
                    modifier = Modifier.size(cardImageSizeSmall),
                    tint = TextDisabled
                )
            }
            is Painter -> {
                Icon(
                    painter = placeHolder,
                    contentDescription = "Empty",
                    modifier = Modifier.size(cardImageSizeSmall)
                )
            }
        }
        Spacer(modifier = Modifier.height(paddingMedium))
        Text(
            text = message,
//            color = TextDisabled,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(paddingSmall))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.titleMedium,
            color = TextDisabled,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateViewPreview() {
    Android7HoursTheme {
        CommonEmptyState(
            message = "빈화면 메세지",
            subMessage = "서브 메세지",
            )
    }
}
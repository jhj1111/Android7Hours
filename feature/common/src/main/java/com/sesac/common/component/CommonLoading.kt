package com.sesac.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.PrimaryPurple
import com.sesac.common.ui.theme.PurpleLight
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall

// ✅ 로딩 카드 추가
@Composable
fun CommonLoading(
    modifier: Modifier = Modifier.Companion,
    text: String? = null,
    backgroundColor: Color = PrimaryPurple,
) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = PurpleLight
        )
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(paddingMedium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = modifier,
                color = backgroundColor
            )
            Spacer(Modifier.Companion.height(paddingSmall))
            text?.let {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun CommonLoadingPreview(){
    Android7HoursTheme {
        CommonLoading(
            text = "로딩중"
        )
    }
}
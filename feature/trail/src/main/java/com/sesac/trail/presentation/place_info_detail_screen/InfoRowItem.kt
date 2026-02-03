package com.sesac.trail.presentation.place_info_detail_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.PrimaryGreenDark
import com.sesac.common.ui.theme.Purple600

@Composable
fun InfoRowItem(
    icon: ImageVector,
    text: String,
    subText: String? = null,
    isHighlight: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isHighlight) PrimaryGreenDark else GrayTabText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
            )
            if (subText != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (onClick != null) Purple600 else GrayTabText,
                    fontWeight = if (onClick != null) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun InfoRowItemPreview_NoClick() {
    InfoRowItem(
        icon = Icons.Filled.Place,
        text = "주소, 전화번호 등 내용",
        subText = "주소, 전화번호 등 내용 내용 내용 "
    )
}
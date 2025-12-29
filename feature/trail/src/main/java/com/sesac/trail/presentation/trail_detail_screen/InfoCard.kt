package com.sesac.trail.presentation.trail_detail_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurpleLight)
    ) {
        Column(modifier = Modifier.padding(paddingSmall)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = paddingMicro)
            ) {
                Icon(icon, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(paddingMicro))
                Text(label, style = MaterialTheme.typography.bodySmall, color = GrayTabText)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
package com.sesac.trail.presentation.place_info_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sesac.common.ui.theme.Gray200
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.PaddingSection
import com.sesac.common.ui.theme.PrimaryGreenDark
import com.sesac.common.ui.theme.PrimaryGreenLight
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.domain.model.Place
import com.sesac.domain.model.PlaceCategory
import com.sesac.trail.presentation.component.TagFlow

@Composable
fun PlaceInfoSection(
    place: Place,
    onCall: () -> Unit,
    onCopyAddress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingLarge),
        verticalArrangement = Arrangement.spacedBy(PaddingSection)
    ) {

        // 1️⃣ 타이틀 & 평점
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "영업중",
                    color = PrimaryGreenDark,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(PrimaryGreenLight, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = place.category.name,
                    color = GrayTabText,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = place.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    " ${place.rating} (${place.reviewCount}명)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                place.distance?.let { dist ->
                    Text(
                        " · 거리 ${String.format("%.1f", dist)}km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayTabText
                    )
                }
            }
        }

        Divider(color = Gray200.copy(alpha = 0.3f))

        // 2️⃣ 상세 정보 (주소 / 전화)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            place.address?.let { address ->
                InfoRowItem(
                    icon = Icons.Default.LocationOn,
                    text = address,
                    subText = "주소 복사",
                    onClick = onCopyAddress
                )
            }

            place.tel?.let { phone ->
                InfoRowItem(
                    icon = Icons.Default.Call,
                    text = phone,
                    onClick = onCall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceInfoSectionPreview() {
    PlaceInfoSection(
        place = Place(
            id = 0,
            title = "병원 이름",
            tel = "02-123-4567",
            address = "서울시 강남구",
            latitude = 0.0,
            longitude = 0.0,
            source = null,
            isActive = true,
            category = PlaceCategory(
                id = 1,
                name = "동물병원",
                parent = null,
                parentCategory = null
            ),
            distance = null,
            createdAt = "",
            updatedAt = "",
            isBookmarked = false,

            // UI용 필드
            rating = 4.5f,
            reviewCount = 23,
            operatingHours = "09:00 - 18:00",
            tags = emptyList(),
            description = null,
            imageUrl = null
        ),
        onCall = {},
        onCopyAddress = {}
    )
}
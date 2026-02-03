package com.sesac.mypage.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import com.sesac.common.ui.theme.ColorBlue
import com.sesac.common.ui.theme.ColorGreen
import com.sesac.common.ui.theme.Purple40
import com.sesac.domain.model.BookmarkedPath
import com.sesac.domain.model.Path
import com.sesac.mypage.model.MyPathStats
import java.util.Locale

val icons = listOf(
    Icons.Default.LocationOn,
    Icons.Default.Schedule,
    Icons.Default.CalendarToday,
)
val colors = listOf(
    Purple40,
    ColorBlue,
    ColorGreen,
)
val label = listOf(
    "산책 거리",
    "산책 시간",
    "산책 횟수"
)

@JvmName("getStatsForBookmarkedPaths")
fun getMyPathStatsUtils(stats: List<BookmarkedPath>): List<MyPathStats> {
    val totalDistance = stats.sumOf { it.distance }
    val totalDuration = stats.sumOf { it.duration ?: 0 }
    val totalCount = stats.size

    val values = listOf(
        String.format(Locale.KOREA, "%.1f km", totalDistance),
        "${totalDuration}분",
        "${totalCount}회"
    )

    return icons.indices.map { i ->
        MyPathStats(
            icon = icons[i],
            label = label[i],
            value = values[i],
            color = colors[i]
        )
    }
}

fun getMyPathStatsUtils(stats: List<Path>): List<MyPathStats> {
    val totalDistance = stats.sumOf { it.distance.toDouble() }
    val totalDuration = stats.sumOf { it.duration }
    val totalCount = stats.size

    val values = listOf(
        String.format(Locale.KOREA, "%.1f km", totalDistance),
        "${totalDuration/60}분",
        "${totalCount}회"
    )

    return icons.indices.map { i ->
        MyPathStats(
            icon = icons[i],
            label = label[i],
            value = values[i],
            color = colors[i]
        )
    }
}
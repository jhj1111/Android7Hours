package com.sesac.data.mapper

import com.naver.maps.geometry.LatLng
import com.sesac.common.utils.fixImageUrl
import com.sesac.data.dto.PathDTO
import com.sesac.data.dto.CoordRequestDTO
import com.sesac.data.dto.PathCreateRequestDTO
import com.sesac.data.dto.PathUpdateRequestDTO
import com.sesac.data.mapper.CoordMapper.toCoordList
import com.sesac.data.mapper.CoordMapper.toCoordRequestDTOList
import com.sesac.data.util.PolylineEncoder
import com.sesac.domain.model.Path

fun PathDTO.toPath() = Path(
    id = this.id,
    uploader = this.authUserNickname ?: "",
    pathName = this.pathName,
    pathComment = this.pathComment,
    level = when(this.level) {
        1 -> "초급"
        2 -> "중급"
        3 -> "고급"
        else -> "알 수 없음"
    },
    distance = this.distance.toFloat(),
    duration = this.duration ?: 0,
    isPrivate = this.isPrivate,
    thumbnail = fixImageUrl(this.thumbnail),
    coord = this.coords.toCoordList(),
    distanceFromMe = this.distanceFromMe,
    bookmarkCount = this.bookmarksCount,
    isBookmarked = this.isBookmarked,
    likes = 0,
    tags = emptyList(),
    markers = this.markers?.mapNotNull { markerData ->
        if (markerData.size >= 2) {
            val lat = markerData.getOrNull(0) as? Double
            val lng = markerData.getOrNull(1) as? Double
            val memoValue = markerData.getOrNull(2)
            val memo: String = if (memoValue is Double) {
                if (memoValue == memoValue.toInt().toDouble()) {
                    memoValue.toInt().toString()
                } else {
                    memoValue.toString()
                }
            } else {
                memoValue?.toString() ?: ""
            }
            if (lat != null && lng != null) {
                com.sesac.domain.model.MemoMarker(lat, lng, memo)
            } else {
                null
            }
        } else {
            null
        }
    }
)

fun Path.toPathCreateRequestDTO() = PathCreateRequestDTO(
    id = null,  // ✅ 항상 null (서버가 신규 생성으로 인식)
    pathName = this.pathName,
    pathComment = this.pathComment,
    level = when(level) {
        "초급" -> 1
        "중급" -> 2
        "고급" -> 3
        else -> 0
    },
    uploader = this.uploader,
    distance = this.distance,
    duration = this.duration,
    coords = this.coord?.toCoordRequestDTOList() ?: listOf(CoordRequestDTO.EMPTY),
    startTime = null, // Not available in UserPath
    endTime = null, // Not available in UserPath
    thumbnail = this.thumbnail,
    isPrivate = this.isPrivate,
    // ✅ 좌표 10,000개를 20KB 문자열로 압축
    polyline = PolylineEncoder.encode(
        this.coord?.map { LatLng(it.latitude, it.longitude) } ?: emptyList()
    ),

    markers = this.markers?.map {
        listOf(it.latitude, it.longitude, it.memo ?: "")
    }
)

fun Path.toPathUpdateRequestDTO(): PathUpdateRequestDTO {
    return PathUpdateRequestDTO(
        pathName = this.pathName,
        pathComment = this.pathComment,
        level = when(level) {
            "초급" -> 1
            "중급" -> 2
            "고급" -> 3
            else -> 0
        },
        distance = this.distance,
        duration = this.duration,
        thumbnail = this.thumbnail,
        isPrivate = this.isPrivate,
    )
}
fun List<PathDTO>.toPathList() =
    this.map { it.toPath() }.toList()

fun List<Path>.toPathCreateRequestDTOList() =
    this.map { it.toPathCreateRequestDTO() }.toList()

fun List<Path>.toPathUpdateRequestDTOList() =
    this.map { it.toPathUpdateRequestDTO() }.toList()
package com.sesac.common.model

import android.os.Parcelable
import com.sesac.domain.model.Coord
import com.sesac.domain.model.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@TypeParceler<Coord, CoordParceler>
data class PathParceler(
    val id: Int,
    val uploader: String,
    val pathName: String,
    val pathComment: String? = null,
    val level: String? = null,
    val distance: Float = 0f,
    val duration: Int = 0,
    val isPrivate: Boolean = false,
    val thumbnail: String? = null,
    val coord: List<Coord>? = null,
    val bookmarksCount: Int,
    val isBookmarked: Boolean,
    val likes: Int = 0,
    val distanceFromMe: Float? = 0f, // React의 distance_from_me
    val tags: List<String> = emptyList(),
) : Parcelable {
    fun toPath() = Path(
        id = this.id,
        uploader = this.uploader,
        pathName = this.pathName,
        pathComment = this.pathComment,
        level = this.level,
        distance = this.distance,
        duration = this.duration,
        isPrivate = this.isPrivate,
        thumbnail = this.thumbnail,
        coord = this.coord,
        bookmarkCount = this.bookmarksCount,
        isBookmarked = this.isBookmarked,
        likes = this.likes,
        distanceFromMe = this.distanceFromMe,
        tags = this.tags,
    )
}

fun Path.toPathParceler() = PathParceler(
    id = this.id,
    uploader = this.uploader,
    pathName = this.pathName,
    pathComment = this.pathComment,
    level = this.level,
    distance = this.distance,
    duration = this.duration,
    isPrivate = this.isPrivate,
    thumbnail = this.thumbnail,
    coord = this.coord,
    bookmarksCount = this.bookmarkCount,
    isBookmarked = this.isBookmarked,
    likes = this.likes,
    distanceFromMe = this.distanceFromMe,
    tags = this.tags,
)
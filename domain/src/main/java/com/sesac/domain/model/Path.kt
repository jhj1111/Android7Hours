package com.sesac.domain.model

data class RecommendedPath(
    val id: Int,
    val name: String,
    val distance: String,
    val time: String,
    val rating: Double,
    val reviews: Int,
    val color: Any
)

data class Path(
    val id: Int = -1,
    val uploader: String,
    val pathName: String,
    val pathComment: String? = null,
    val level: String? = null,
    val distance: Float = 0f,
    val duration: Int = 0,
    val isPrivate: Boolean = false,
    val thumbnail: String? = null,
    val coord: List<Coord>? = null,
    val bookmarkCount: Int,
    val isBookmarked: Boolean,
    val likes: Int = 0,
    val distanceFromMe: Float? = 0f, // React의 distance_from_me
    val markers: List<MemoMarker>? = null,
    val tags: List<String> = emptyList(),
) {
    companion object {
        val EMPTY = Path(
            id = -1,
            uploader = "",
            pathName = "",
            pathComment = "",
            level = null,
            distance = 0f,
            duration = 0,
            isPrivate = false,
            thumbnail = "",
            coord = null,
            bookmarkCount = 0,
            isBookmarked = false,
            likes = 0,
            distanceFromMe = 0f,
            markers = null,
            tags = emptyList(),
        )
    }

    fun toBookmarkedPath(): BookmarkedPath = BookmarkedPath(
        id = this.id,
        source = "PUBLIC_API",
        uploader = this.uploader,
        pathName = this.pathName,
        pathComment = this.pathComment,
        level = when(this.level) {
            "초급" -> 1
            "중급" -> 2
            "고급" -> 3
            else -> 0
        },
        distance = this.distance.toDouble(),
        duration = this.duration,
        isPrivate = this.isPrivate,
        thumbnail = this.thumbnail,
        bookmarkCount = this.bookmarkCount,
        isBookmarked = true,
    )

}

/**
 * Represents a bookmarked Path in the domain layer.
 * bookmark 정보에 필요없는 정보 제외(좌표 등)
 */
data class BookmarkedPath(
    override val id: Int,
    val source: String,
    val uploader: String,
    val pathName: String,
    val pathComment: String?,
    val level: Int,
    val distance: Double,
    val duration: Int?,
    val isPrivate: Boolean,
    val thumbnail: String?,
    var bookmarkCount: Int,
    var isBookmarked: Boolean,
) : BookmarkedItem {
    companion object {
        val EMPTY = BookmarkedPath(
            id = -1,
            source = "",
            uploader = "",
            pathName = "",
            pathComment = "",
            level = 1,
            distance = .0,
            duration = 0,
            isPrivate = false,
            thumbnail = "",
            bookmarkCount = 0,
            isBookmarked = false
        )
    }
}

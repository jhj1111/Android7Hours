package com.sesac.domain.model

import com.sesac.domain.type.PostType
import java.util.Date

// 상세 페이지 출력용(게시글 목록 출력용 + content, comments)
data class Post(
    // PostBaseItem의 모든 속성 구현
    val id: Int,
    val userId: Int,
    val authUserNickname: String? = null,
    val authUserProfileImageUrl: String?,
    val postType: PostType,
    val title: String,
    val image: String?,
    val viewCount: Int,
    val commentCount: Int,
    val likeCount: Int,
    val bookmarkCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    val content: String,
    var comments: List<Comment>? = null,
) {
    companion object {
        val EMPTY = Post(
            id = -1,
            userId = -1,
            authUserNickname = "",
            authUserProfileImageUrl = null,
            postType = PostType.UNKNOWN,
            title = "",
            image = null,
            createdAt = Date(0L),
            updatedAt = Date(0L),
            viewCount = 0,
            commentCount = 0,
            likeCount = 0,
            bookmarkCount = 0,
            isLiked = false,
            isBookmarked = false,
            content = "",
            comments = null,
        )
    }
}

data class BookmarkedPost(
    override val id: Int,
    val userId: Int,
    val authUserNickname: String,
    val authUserProfileImageUrl: String?,
    val postType: PostType,
    val title: String,
    val content: String,
    val image: String?,
    val viewCount: Int,
    val commentCount: Int,
    val likeCount: Int,
    val bookmarkCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean
) : BookmarkedItem {
    companion object {
        val EMPTY = BookmarkedPost(
            id = -1,
            userId = -1,
            authUserNickname = "",
            authUserProfileImageUrl = "",
            postType = PostType.UNKNOWN,
            title = "",
            content = "",
            image = "",
            viewCount = 0,
            commentCount = 0,
            likeCount = 0,
            bookmarkCount = 0,
            isLiked = false,
            isBookmarked = false,
        )
    }
}
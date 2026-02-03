package com.sesac.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 북마크된 항목이 게시글(Post)일 때의 구체적인 DTO 구조.
 * 이는 'users/me/bookmarks/' API 응답의 'bookmarked_object' 필드에 매핑됩니다.
 * JSON 응답 순서에 맞춰 필드를 재배열하여 가독성을 높였습니다.
 */
@JsonClass(generateAdapter = true)
data class BookmarkedPostDTO(
    override val id: Int,
    @Json(name = "auth_id")
    val authUserId: Int,
    @Json(name = "auth_name")
    val authUserNickname: String,
    @Json(name = "auth_profile_image")
    val authUserProfileImageUrl: String?,
    @Json(name = "post_type")
    val postType: String?,
    val title: String,
    val content: String, // PostList 출력 형태라 임시 제거
    val image: String?,
    @Json(name = "view_count")
    val viewCount: Int,
    @Json(name = "comment_count")
    val commentCount: Int,
    @Json(name = "like_count")
    val likeCount: Int,
    @Json(name = "bookmark_count")
    val bookmarkCount: Int,
    @Json(name = "is_liked")
    val isLiked: Boolean,
    @Json(name = "is_bookmarked")
    val isBookmarked: Boolean,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String,
) : BookmarkedObject {
    // Moshi 식별자. JSON 응답의 type 필드와 일치
    override val type: String = "POST"
}
package com.sesac.data.dto.post.response

import com.sesac.data.dto.CommentDTO
import com.sesac.data.type.PostTypeDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date
import kotlin.time.Clock
import kotlin.time.Instant

@JsonClass(generateAdapter = true)
data class PostDTO(
    // 1. 고유 ID
    val id: Int,
    // 2. 작성자 정보: PostListDTO와 동일하게 평면화된 구조 반영
    @Json(name = "auth_id")
    val authId: Int?,
    @Json(name = "auth_name")
    val authUserNickname: String? = null,
    @Json(name = "auth_profile_image")
    val authUserProfileImageUrl: String?,
    // 3. 게시글 타입 및 제목
    @Json(name = "post_type")
    val postType: PostTypeDTO,
    val title: String,
    // 4. 게시글 본문 (Detail 고유 필드)
    val content: String,
    // 5. 썸네일 이미지 URL
    val image: String?,
    // 7. 카운트 정보
    @Json(name = "view_count")
    val viewCount: Int = 0,
    @Json(name = "comment_count")
    val commentCount: Int = 0,
    @Json(name = "like_count")
    val likeCount: Int = 0,
    @Json(name = "bookmark_count")
    val bookmarkCount: Int = 0,
    // 8. 상태 정보 (사용자별)
    @Json(name = "is_liked")
    val isLiked: Boolean = false,
    @Json(name = "is_bookmarked")
    val isBookmarked: Boolean = false,
    // 9. 시간 정보 (String 타입)
    @Json(name = "created_at")
    val createdAt: String = Date().toString(),
    @Json(name = "updated_at")
    val updatedAt: String= Date().toString(),
    val comments: List<CommentDTO>? = null,
)
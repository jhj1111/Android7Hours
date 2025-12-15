package com.sesac.data.mapper

import com.sesac.common.utils.fixImageUrl
import com.sesac.common.utils.parseDate
import com.sesac.data.dto.post.request.PostCreateRequestDTO
import com.sesac.data.dto.post.request.PostUpdateRequestDTO
import com.sesac.data.dto.post.response.PostDTO
import com.sesac.data.mapper.CommentMapper.toDomain
import com.sesac.domain.model.Post
import com.sesac.domain.type.PostType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// 현재 장고 서버에서 보내는 날짜 형식
// Mapper 전용, Thread-safe, 한국 시간 적용
private val DATE_FORMATTER = ThreadLocal.withInitial {
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Seoul")
    }
}

/**
 * Mapper 전용 확장 함수
 * 문자열 → Date 안전 변환
 */
private fun String.toDateOrEpoch(): Date {
    return try {
        DATE_FORMATTER.get().parse(this) ?: Date(0L)
    } catch (e: Exception) {
        Date(0L)
    }
}
// ======================================================================
// 🟢 1. 중첩된 댓글 구조를 위한 매퍼
// ======================================================================

/**
 * 댓글 작성자 DTO를 도메인 모델로 변환 (가정: PostDetailDTO 내부의 author 구조와 유사)
 */
//fun CommentAuthorDTO.toDomain() = CommentItem.Author(
//    id = this.authId,
//    nickname = this.authUserNickname
//)

/**
 * 댓글 DTO를 도메인 모델로 변환
 */
//fun CommentItemDTO.toDomain() = CommentItem(
//    id = this.id,
//    author = this.author.toDomain(), // 💡 중첩 매퍼 호출
//    content = this.content,
//    createdAt = this.createdAt.toDateOrEpoch()
//    // PostDetailDTO와 마찬가지로 업데이트 날짜, 좋아요 상태 등의 필드가 더 있을 수 있음
//)

fun PostDTO.toPost(): Post = Post(
    id = this.id,
    userId = this.authId?: -1,
    authUserNickname = this.authUserNickname,
    authUserProfileImageUrl = fixImageUrl(this.authUserProfileImageUrl),
    postType = try {
        PostType.valueOf(this.postType.name)
    } catch (e: Exception) {
        PostType.UNKNOWN
    },
    title = this.title,
    image = fixImageUrl(this.image),
    viewCount = this.viewCount,
    commentCount = this.commentCount,
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    isLiked = this.isLiked,
    isBookmarked = this.isBookmarked,
//    createdAt = this.createdAt.toDateOrEpoch(),
    createdAt = parseDate(this.createdAt),
    content = this.content,
//    updatedAt = this.updatedAt.toDateOrEpoch(),
    updatedAt = parseDate(this.updatedAt),
    comments = this.comments?.toDomain(this.id)
)

fun Post.toPostCreateRequestDTO(): PostCreateRequestDTO = PostCreateRequestDTO(
    id = this.id,
    postType = this.postType.name.lowercase(Locale.ROOT),
    title = this.title,
    content = this.content,
    image = this.image
)

fun Post.toPostUpdateRequestDTO(): PostUpdateRequestDTO = PostUpdateRequestDTO(
    id = this.id,
    postType = this.postType.name.lowercase(Locale.ROOT),
    title = this.title,
    content = this.content,
    image = this.image
)

fun List<PostDTO>.toDomain(): List<Post> = this.map { it.toPost() }

fun List<Post>.toPostCreateRequestDTOList(): List<PostCreateRequestDTO> =
    this.map { it.toPostCreateRequestDTO() }

fun List<Post>.toPostUpdateRequestDTOList(): List<PostUpdateRequestDTO> =
    this.map { it.toPostUpdateRequestDTO() }
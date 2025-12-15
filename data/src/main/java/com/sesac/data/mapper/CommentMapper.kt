package com.sesac.data.mapper

import android.content.Context
import com.sesac.common.utils.fixImageUrl
import com.sesac.common.utils.getTimeAgo
import com.sesac.common.utils.parseDate
import com.sesac.data.dto.AuthorDTO
import com.sesac.data.dto.CommentDTO
import com.sesac.domain.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommentMapper {

    fun CommentDTO.toDomain(context: Context, postId: Int): Comment {
        val parsedDate = parseDate(createdAt)
        val timeAgoString = parsedDate.getTimeAgo(context)

        return Comment(
            id = this.id,
            objectId = postId,
            authorId = this.author.id,
            authorNickName = this.author.nickname,
            content = this.content,
            createdAt = this.createdAt,
            timeAgo = timeAgoString,
            authorImage = fixImageUrl(this.author.image) ?: "https://img.icons8.com/?size=100&id=Fx70T4fgtNmt&format=png&color=000000"
        )
    }

    fun List<CommentDTO>.toDomain(context: Context, postId: Int): List<Comment> {
        return this.map { it.toDomain(context, postId) }
    }

    // Context 없이 변환하는 함수 추가
    fun CommentDTO.toDomain(postId: Int): Comment {
        return Comment(
            id = this.id,
            objectId = postId,
            authorId = this.author.id,
            authorNickName = this.author.nickname,
            content = this.content,
            createdAt = this.createdAt,
            timeAgo = null, // UI 레이어에서 계산하도록 null로 설정
            authorImage = fixImageUrl(this.author.image) ?: "https://img.icons8.com/?size=100&id=Fx70T4fgtNmt&format=png&color=000000"
        )
    }

    fun List<CommentDTO>.toDomain(postId: Int): List<Comment> {
        return this.map { it.toDomain(postId) }
    }


    fun Comment.toDTO(): CommentDTO {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val createdAtString = sdf.format(Date())

        return CommentDTO(
            id = this.id,
            author = AuthorDTO(
                id = this.authorId,
                nickname = this.authorNickName,
                image = this.authorImage
            ),
            content = this.content,
            createdAt = createdAtString
        )
    }
}
package com.sesac.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookmarkedPathDTO(
    override val id: Int,
    val source: String,
    @Json(name = "auth_user_nickname")
    val authUserNickname: String,
    @Json(name = "path_name")
    val pathName: String, // Discriminator for Moshi
    @Json(name = "path_comment")
    val pathComment: String?,
    val level: Int,
    val distance: Double,
    val duration: Int?,
    @Json(name = "is_private")
    val isPrivate: Boolean,
    val thumbnail: String?,
    @Json(name = "bookmark_count")
    val bookmarksCount: Int,
    @Json(name = "is_bookmarked")
    val isBookmarked: Boolean,
) : BookmarkedObject {
    // Moshi 식별자. JSON 응답의 type 필드와 일치
    override val type: String = "PATH"
}

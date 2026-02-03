package com.sesac.domain.repository

import com.sesac.domain.model.BookmarkResponse
import com.sesac.domain.model.Like
import com.sesac.domain.model.Post
import com.sesac.domain.result.AuthResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface PostRepository {
    // 1. 공용 게시글 목록 조회
    suspend fun getPostList(query: String? = null): Flow<AuthResult<List<Post>>>
    // 2. 내 게시글 목록 조회
    suspend fun getMyPosts(token: String): Flow<AuthResult<List<Post>>>
    // 3. 게시글 상세 조회
    suspend fun getPostDetail(token: String, id: Int): Flow<AuthResult<Post>>
    // 4. 게시글 생성 (인증 필요)
    suspend fun createPost(token: String, post: Post, image: MultipartBody.Part?): Flow<AuthResult<Post>>
    // 5. 게시글 수정 (인증 필요)
    suspend fun updatePost(token: String, id: Int, post: Post, image: MultipartBody.Part?): Flow<AuthResult<Post>>
    // 6. 게시글 삭제 (인증 필요)
    suspend fun deletePost(token: String, id: Int): Flow<AuthResult<Unit>>
    // 7. 북마크 토글 (Use Case 요구사항에 따라 PostRepository에 포함)
    suspend fun toggleBookmark(token: String, id: Int): Flow<AuthResult<BookmarkResponse>>
    suspend fun toggleLike(token: String, id: Int): Flow<AuthResult<Like>>
//    suspend fun getMyPostsLikes(token: String): Flow<AuthResult<List<PostListItem>>>
//    suspend fun getMyPostsComments(token: String): Flow<AuthResult<List<PostListItem>>>
//    suspend fun getMyPostsBookmarks(token: String): Flow<AuthResult<List<PostListItem>>>
}
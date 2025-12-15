package com.sesac.data.repository

import android.util.Log
import com.sesac.data.mapper.toBookmarkResponse
import com.sesac.data.mapper.toDomain
import com.sesac.data.mapper.toPost
import com.sesac.data.source.api.PostApi
import com.sesac.domain.model.BookmarkResponse
import com.sesac.domain.model.Like
import com.sesac.domain.model.Post
import com.sesac.domain.repository.PostRepository
import com.sesac.domain.result.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {
    // ------------------------------
    // GET LIST
    // ------------------------------
    override suspend fun getPostList(query: String?): Flow<AuthResult<List<Post>>> = flow {
        emit(AuthResult.Loading)

        val posts = postApi.getPostsList(query).toDomain()
        emit(AuthResult.Success(posts))

    }.catch {
        Log.e("TAG-PostRepository", "Get Post List error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // GET DETAIL
    // ------------------------------
    override suspend fun getPostDetail(token: String, id: Int): Flow<AuthResult<Post>> = flow {
        emit(AuthResult.Loading)

        val post = postApi.getPostDetail("Bearer $token", id).toPost()
        emit(AuthResult.Success(post))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Get Post Detail error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // MY POSTS
    // ------------------------------
    override suspend fun getMyPosts(token: String): Flow<AuthResult<List<Post>>> = flow {
        emit(AuthResult.Loading)

        val posts = postApi.getMyPosts("Bearer $token").toDomain()
        emit(AuthResult.Success(posts))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Get My Posts error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // CREATE
    // ------------------------------
    override suspend fun createPost(
        token: String,
        post: Post,
        image: MultipartBody.Part?
    ): Flow<AuthResult<Post>> = flow {
        emit(AuthResult.Loading)

        val params = mutableMapOf<String, RequestBody>()
        params["title"] = post.title.toRequestBody("text/plain".toMediaTypeOrNull())
        params["content"] = post.content.toRequestBody("text/plain".toMediaTypeOrNull())
        params["post_type"] = post.postType.name.lowercase(Locale.ROOT).toRequestBody("text/plain".toMediaTypeOrNull())

        val created = postApi.createPost("Bearer $token", params, image)
        emit(AuthResult.Success(created.toPost()))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Create Post error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // UPDATE
    // ------------------------------
    override suspend fun updatePost(
        token: String,
        id: Int,
        post: Post,
        image: MultipartBody.Part?
    ): Flow<AuthResult<Post>> = flow {
        emit(AuthResult.Loading)

        val params = mutableMapOf<String, RequestBody>()
        params["title"] = post.title.toRequestBody("text/plain".toMediaTypeOrNull())
        params["content"] = post.content.toRequestBody("text/plain".toMediaTypeOrNull())
        params["post_type"] = post.postType.name.lowercase(Locale.ROOT).toRequestBody("text/plain".toMediaTypeOrNull())

        val updated = postApi.updatePost("Bearer $token", id, params, image)
        emit(AuthResult.Success(updated.toPost()))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Update Post error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // DELETE
    // ------------------------------
    override suspend fun deletePost(token: String, id: Int): Flow<AuthResult<Unit>> = flow {
        emit(AuthResult.Loading)

        postApi.deletePost("Bearer $token", id)
        emit(AuthResult.Success(Unit))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Delete Post error: $it")
        emit(AuthResult.NetworkError(it))
    }

    // ------------------------------
    // BOOKMARK
    // ------------------------------
    override suspend fun toggleBookmark(
        token: String,
        id: Int
    ): Flow<AuthResult<BookmarkResponse>> = flow {
        emit(AuthResult.Loading)

        val result = postApi.bookmarkToggle("Bearer $token", id).toBookmarkResponse()
        emit(AuthResult.Success(result))

    }.catch {
        Log.e("TAG-PostRepositoryImpl", "Toggle Bookmark error: $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun toggleLike(
        token: String,
        id: Int
    ): Flow<AuthResult<Like>> = flow {

        emit(AuthResult.Loading)

        try {
            val result = postApi.likeToggle("Bearer $token", id).toDomain()
            emit(AuthResult.Success(result))
        } catch (e: Exception) {
            emit(AuthResult.NetworkError(e))
        }
    }
}

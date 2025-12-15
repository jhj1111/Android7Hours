package com.sesac.data.source.api

import com.sesac.data.dto.BookmarkResponseDTO
import com.sesac.data.dto.CommentDTO
import com.sesac.data.dto.CommentRequestDTO
import com.sesac.data.dto.LikeResponseDTO
import com.sesac.data.dto.post.response.PostDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @GET("posts/")
    suspend fun getPostsList(
//        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): List<PostDTO>

    @GET("posts/mine/")
    suspend fun getMyPosts(
        @Header("Authorization") token: String
    ): List<PostDTO>

    @GET("posts/{id}/")
    suspend fun getPostDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): PostDTO

    @Multipart
    @POST("posts/")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): PostDTO

    @Multipart
    @PATCH("posts/{id}/")
    suspend fun updatePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): PostDTO

    @DELETE("posts/{id}/")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    @POST("posts/{id}/bookmark-toggle/")
    suspend fun bookmarkToggle(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): BookmarkResponseDTO

    @POST("posts/{id}/like-toggle/")
    suspend fun likeToggle(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): LikeResponseDTO


    @GET("posts/{postId}/comments/")
    suspend fun getComments(
        @Path("postId") postId: Int
    ): List<CommentDTO>

    @POST("posts/{postId}/comments/")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int,
        @Body request: CommentRequestDTO
    ): CommentDTO

    @DELETE("posts/{postId}/comments/{commentId}/")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Unit
}



//
//    @POST("posts/{post_id}/like_toggle/")
//    suspend fun toggleLike(
//        @Header("Authorization") token: String,
//        @Path("post_id") postId: Int
//    ): LikeResponseDTO
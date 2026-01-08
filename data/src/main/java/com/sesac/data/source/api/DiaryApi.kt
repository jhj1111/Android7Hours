package com.sesac.data.source.api

import com.sesac.data.dto.DiaryDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface DiaryApi {
//    @POST("generate-diary")
//    suspend fun generateDiary(
//        @Body data: DiaryRequestDTO
//    ): DiaryDTO

    @GET("paths/{pathId}/diary")
    suspend fun getDiary(
        @Path("pathId") pathId: Int
    ): DiaryDTO
}
package com.sesac.data.dto

// API 응답용 - 서버에서 받을 데이터
data class DiaryDTO(
    val diary: String
)

//// API 요청용 - 서버로 보낼 데이터
//data class DiaryRequestDTO(
//    val distance: Float,
//    val duration: Int,
//    val pathName: String
//)

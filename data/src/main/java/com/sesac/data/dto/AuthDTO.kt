package com.sesac.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 회원 가입에 필요한 유저 정보
@JsonClass(generateAdapter = true)
data class AuthDTO(
    val id: Int = -1,
    val username: String?,
    val email: String,
    @Json(name = "full_name")
    val fullName: String,
    val nickname: String?,
    val password: String? = null,
    @Json(name = "password_verification")
    val passwordVerification: String? = null,
    @Json(name = "profile_image")
    val profileImage: String? = null,
    @Json(name = "invitation_code") // NEW FIELD
    val invitationCode: String? = null,
    @Json(name = "is_pet")
    val isPet: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class TokenDTO(
    val refresh: String,
    val access: String,
)

@JsonClass(generateAdapter = true)
data class TokenRefreshRequestDTO(
    val refresh: String
)

@JsonClass(generateAdapter = true)
data class TokenRefreshResponseDTO(
    val access: String,
    val refresh: String?,
)

@JsonClass(generateAdapter = true)
data class LoginResponseDTO(
    val refresh: String,
    val access: String,
    val user: AuthDTO,
)

@JsonClass(generateAdapter = true)
data class LoginRequestDTO(
    val email: String,
    val password: String,
)

@JsonClass(generateAdapter = true)
data class KakaoLoginRequestDTO(
    @Json(name = "access_token")
    val accessToken: String,
    val email: String? = null,
    val nickname: String? = null
)

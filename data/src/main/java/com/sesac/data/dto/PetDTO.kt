package com.sesac.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetDTO(
    val id: Int = -1,
    val owner: String,
    val name: String,
    val gender: String? =null,
    val birthday: String?, // Changed to nullable
    val neutering: Boolean,
    val breed: String?, // Changed to nullable
    val image: String? = null,
    @Json(name = "linked_user")
    val linkedUser: String? = null, // New field
    @Json(name = "last_location")
    val lastLocation: PetLocationResponseDTO? = null // New field
)

@JsonClass(generateAdapter = true)
data class BreedDTO(
    val id: Int =-1,
    @Json(name = "breed_name")
    val breedName: String,
)
package com.sesac.domain.model

data class BannerData(
    val id: Int,
    val image: String,
    val title: String,
    val subtitle: String
){
    companion object{
        val EMPTY = BannerData(
            id = -1,
            image = "",
            title = "",
            subtitle = "",
        )
    }
}
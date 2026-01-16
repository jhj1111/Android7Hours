package com.sesac.data.mapper

import com.sesac.data.dto.DiaryDTO
import com.sesac.domain.model.Diary

fun DiaryDTO.toDiary() = Diary(
    diary = this.diary
)
package com.sesac.domain.usecase.diary

data class DiaryUseCase(
    val getDiaryUseCase: GetDiaryUseCase,
    val getDiaryFromLocalUseCase: GetDiaryFromLocalUseCase,
    val saveDiaryToLocalUseCase: SaveDiaryToLocalUseCase,
)
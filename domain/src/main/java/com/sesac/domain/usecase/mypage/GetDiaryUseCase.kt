package com.sesac.domain.usecase.mypage

import com.sesac.domain.repository.MypageRepository
import javax.inject.Inject

class GetDiaryUseCase @Inject constructor(
    private val repository: MypageRepository
) {
    suspend operator fun invoke(scheduleId: Long, pathId: Int): String? {
        return repository.getDiary(scheduleId, pathId)
    }
}
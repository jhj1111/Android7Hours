package com.sesac.domain.usecase.diary

import com.sesac.domain.repository.MypageRepository
import javax.inject.Inject

class GetDiaryFromLocalUseCase @Inject constructor (
    private val repository: MypageRepository
) {
    suspend operator fun invoke(scheduleId: Long): String? {
        return repository.getDiaryFromLocal(scheduleId)
    }
}
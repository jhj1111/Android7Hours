package com.sesac.domain.usecase.diary

import com.sesac.domain.repository.MypageRepository
import javax.inject.Inject

class SaveDiaryToLocalUseCase @Inject constructor(
    private val repository: MypageRepository
) {
    suspend operator fun invoke(scheduleId: Long, pathId: Int, diary: String) {
        repository.saveDiaryToLocal(scheduleId, pathId, diary)
    }
}
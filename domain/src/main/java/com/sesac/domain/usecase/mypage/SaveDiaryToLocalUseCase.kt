package com.sesac.domain.usecase.mypage

import com.sesac.domain.repository.MypageRepository
import javax.inject.Inject

class SaveDiaryToLocalUseCase @Inject constructor(
    private val repository: MypageRepository
) {
    suspend operator fun invoke(scheduleId: Long, pathId: Int, diary: String, isSynced: Boolean) {
        repository.saveDiaryToLocal(scheduleId, pathId, diary, isSynced)
    }
}
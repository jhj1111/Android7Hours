package com.sesac.domain.usecase.diary

import com.sesac.domain.model.Diary
import com.sesac.domain.repository.PathRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiaryUseCase @Inject constructor(
    private val repository: PathRepository
) {
    suspend operator fun invoke(pathId: Int) = repository.getDiary(pathId)
}

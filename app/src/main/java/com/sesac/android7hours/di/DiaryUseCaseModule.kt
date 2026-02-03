package com.sesac.android7hours.di

import com.sesac.domain.repository.MypageRepository
import com.sesac.domain.repository.PathRepository
import com.sesac.domain.usecase.diary.DiaryUseCase
import com.sesac.domain.usecase.diary.GetDiaryFromLocalUseCase
import com.sesac.domain.usecase.diary.GetDiaryUseCase
import com.sesac.domain.usecase.diary.SaveDiaryToLocalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object DiaryUseCaseModule {
    @Provides
    @ActivityRetainedScoped
    fun provideDiaryUseCase(
        pathRepository: PathRepository,
        mypageRepository: MypageRepository,
    ): DiaryUseCase {
        return DiaryUseCase(
            getDiaryUseCase = GetDiaryUseCase(pathRepository),
            getDiaryFromLocalUseCase = GetDiaryFromLocalUseCase(mypageRepository),
            saveDiaryToLocalUseCase = SaveDiaryToLocalUseCase(mypageRepository),
        )
    }
}
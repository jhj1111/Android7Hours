package com.sesac.android7hours.di

import com.sesac.domain.repository.MypageRepository
import com.sesac.domain.usecase.mypage.AddScheduleUseCase
import com.sesac.domain.usecase.mypage.DeleteFavoriteCommunityPostUseCase
import com.sesac.domain.usecase.mypage.DeleteFavoriteWalkPathsUseCase
import com.sesac.domain.usecase.mypage.DeleteScheduleUseCase
import com.sesac.domain.usecase.mypage.GetDiaryFromLocalUseCase
import com.sesac.domain.usecase.mypage.GetDiaryUseCase
import com.sesac.domain.usecase.mypage.GetFavoriteCommunityPostsUseCase
import com.sesac.domain.usecase.mypage.GetFavoriteWalkPathsUseCase
import com.sesac.domain.usecase.mypage.GetMypageStatsUseCase
import com.sesac.domain.usecase.mypage.GetSchedulesUseCase
import com.sesac.domain.usecase.mypage.MypageUseCase
import com.sesac.domain.usecase.mypage.SaveDiaryToLocalUseCase
import com.sesac.domain.usecase.mypage.UpdatePermissionStatusUseCase
import com.sesac.domain.usecase.mypage.UpdateScheduleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object MypageUseCaseModule {
    @Provides
    @ActivityRetainedScoped
    fun provideMypageUseCase(repository: MypageRepository): MypageUseCase {
        return MypageUseCase(
            addScheduleUseCase = AddScheduleUseCase(repository),
            deleteFavoriteCommunityPostUseCase = DeleteFavoriteCommunityPostUseCase(repository),
            deleteFavoriteWalkPathsUseCase = DeleteFavoriteWalkPathsUseCase(repository),
            deleteScheduleUseCase = DeleteScheduleUseCase(repository),
            getDiaryFromLocalUseCase = GetDiaryFromLocalUseCase(repository),
            getDiaryUseCase = GetDiaryUseCase(repository),
            getFavoriteCommunityPostsUseCase = GetFavoriteCommunityPostsUseCase(repository),
            getFavoriteWalkPathsUseCase = GetFavoriteWalkPathsUseCase(repository),
            getMypageStatsUseCase = GetMypageStatsUseCase(repository),
            getSchedulesUseCase = GetSchedulesUseCase(repository),
            saveDiaryToLocalUseCase = SaveDiaryToLocalUseCase(repository),
            updatePermissionStatusUseCase = UpdatePermissionStatusUseCase(repository),
            updateScheduleUseCase = UpdateScheduleUseCase(repository),
        )
    }
}
package com.sesac.domain.usecase.mypage

data class MypageUseCase(
    val addScheduleUseCase: AddScheduleUseCase,
    val deleteFavoriteCommunityPostUseCase: DeleteFavoriteCommunityPostUseCase,
    val deleteFavoriteWalkPathsUseCase: DeleteFavoriteWalkPathsUseCase,
    val deleteScheduleUseCase: DeleteScheduleUseCase,
    val getFavoriteCommunityPostsUseCase: GetFavoriteCommunityPostsUseCase,
    val getFavoriteWalkPathsUseCase: GetFavoriteWalkPathsUseCase,
    val getMypageStatsUseCase: GetMypageStatsUseCase,
    val getSchedulesUseCase: GetSchedulesUseCase,
    val updatePermissionStatusUseCase: UpdatePermissionStatusUseCase,
    val updateScheduleUseCase: UpdateScheduleUseCase,
)
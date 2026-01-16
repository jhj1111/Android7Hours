package com.sesac.domain.repository

import com.sesac.domain.model.Diary
import com.sesac.domain.model.FavoriteCommunityPost
import com.sesac.domain.model.FavoriteWalkPath
import com.sesac.domain.model.MypageSchedule
import com.sesac.domain.model.MypageStat
import com.sesac.domain.model.Path
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface MypageRepository {
    fun getMypageStats(): Flow<List<MypageStat>>
//    fun getMypageMenuItems(): Flow<List<MypageMenuItem>>
    fun getFavoriteWalkPaths(): Flow<List<FavoriteWalkPath>>
    fun deleteFavoriteWalkPaths(favoriteWalkPathId: Int): Flow<Boolean>
    fun getFavoriteCommunityPosts(): Flow<List<FavoriteCommunityPost>>
    fun deleteFavoriteCommunityPosts(favoriteCommunityPostId: Int): Flow<Boolean>
    fun getSchedules(date: LocalDate): Flow<List<MypageSchedule>>
    fun addSchedule(schedule: MypageSchedule): Flow<Boolean>
    fun deleteSchedule(scheduleId: Long): Flow<Boolean>
//    fun getMypagePermissions(): Flow<List<MypagePermission>>
    fun updatePermissionStatus(key: String, isEnabled: Boolean): Flow<Boolean>
    fun updateSchedule(schedule: MypageSchedule): Flow<Boolean>
    // ✅ Diary Room 관련 추가
    suspend fun saveDiaryToLocal(scheduleId: Long, pathId: Int, diary: String)
    suspend fun getDiaryFromLocal(scheduleId: Long): String?
    suspend fun syncDiaryToServer(scheduleId: Long): Boolean
}
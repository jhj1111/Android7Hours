package com.sesac.data.repository

import android.util.Log
import com.sesac.data.dao.DiaryDao
import com.sesac.data.entity.DiaryEntity
import com.sesac.data.source.local.datasource.MockMypage
import com.sesac.domain.model.FavoriteCommunityPost
import com.sesac.domain.model.FavoriteWalkPath
import com.sesac.domain.model.MypageSchedule
import com.sesac.domain.model.MypageStat
import com.sesac.domain.repository.MypageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MypageRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : MypageRepository {
    override fun getMypageStats(): Flow<List<MypageStat>> = flow {
        emit(MockMypage.stats)
    }

//    override fun getMypageMenuItems(): Flow<List<MypageMenuItem>> = flow {
//        emit(MockMypage.menuItems)
//    }

    override fun getFavoriteWalkPaths(): Flow<List<FavoriteWalkPath>> = flow {
        emit(MockMypage.favoriteWalkPaths)
    }

    override fun deleteFavoriteWalkPaths(favoriteWalkPathId: Int): Flow<Boolean> = flow {
        val removed = MockMypage.favoriteWalkPaths.removeAll { it.id == favoriteWalkPathId }
        emit(removed)
    }

    override fun getFavoriteCommunityPosts(): Flow<List<FavoriteCommunityPost>> = flow {
        emit(MockMypage.favoritePosts)
    }

    override fun deleteFavoriteCommunityPosts(favoriteCommunityPostId: Int): Flow<Boolean> = flow {
        val removed = MockMypage.favoritePosts.removeAll { it.id == favoriteCommunityPostId }
        emit(removed)
    }

    override fun getSchedules(date: LocalDate): Flow<List<MypageSchedule>> = flow {
        val filtered = MockMypage.schedules.filter { it.date == date }
        emit(filtered)
    }

    override fun addSchedule(schedule: MypageSchedule): Flow<Boolean> = flow {
        MockMypage.schedules.add(schedule)
        emit(true)
    }

    override fun deleteSchedule(scheduleId: Long): Flow<Boolean> = flow {
        val removed = MockMypage.schedules.removeAll { it.id == scheduleId }
        emit(removed)
    }

//    override fun getMypagePermissions(): Flow<List<MypagePermission>> = flow {
//        emit(MockMypage.permissions)
//    }

    override fun updatePermissionStatus(key: String, isEnabled: Boolean): Flow<Boolean> = flow {
        // This is a mock implementation, so we don't actually persist the state.
        // In a real app, you would update a database or shared preferences here.
        emit(true)
    }
    // ✅ 일정 업데이트 추가
    override fun updateSchedule(schedule: MypageSchedule): Flow<Boolean> = flow {
        val index = MockMypage.schedules.indexOfFirst { it.id == schedule.id }
        if (index != -1) {
            MockMypage.schedules[index] = schedule
            emit(true)
        } else {
            emit(false)
        }
    }

    // ✅ Room에 다이어리 저장
    override suspend fun saveDiaryToLocal(scheduleId: Long, pathId: Int, diary: String) {
        val entity = DiaryEntity(
            scheduleId = scheduleId,
            pathId = pathId,
            diary = diary,
            isSynced = false
        )
        diaryDao.insertDiary(entity)
    }

    // ✅ Room에서 다이어리 조회
    override suspend fun getDiaryFromLocal(scheduleId: Long): String? {
        return diaryDao.getDiaryByScheduleId(scheduleId)?.diary
    }

    // ✅ 서버 동기화 (서버 준비되면 사용)
    override suspend fun syncDiaryToServer(scheduleId: Long): Boolean {
        return try {
            val diary = diaryDao.getDiaryByScheduleId(scheduleId) ?: return false

            // TODO: 서버 API 호출
            // diaryServerApi.uploadDiary(diary)

            // 동기화 완료 후 Room에서 삭제 또는 마크
            diaryDao.markAsSynced(scheduleId)
            // 또는 diaryDao.deleteDiary(scheduleId)

            true
        } catch (e: Exception) {
            Log.e("MypageRepository", "다이어리 동기화 실패", e)
            false
        }
    }
}
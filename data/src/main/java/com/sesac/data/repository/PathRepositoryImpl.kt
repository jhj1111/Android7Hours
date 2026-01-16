package com.sesac.data.repository

import android.util.Log
import com.naver.maps.geometry.LatLng
import com.sesac.data.mapper.toBookmarkResponse
import com.sesac.data.dao.PathDao
import com.sesac.data.mapper.toDiary
import com.sesac.data.mapper.toDomain
import com.sesac.data.mapper.toPathCreateRequestDTO
import com.sesac.data.mapper.toPathEntity
import com.sesac.data.mapper.toPathUpdateRequestDTO
import com.sesac.data.mapper.toPath
import com.sesac.data.mapper.toPathList
import com.sesac.data.mapper.toUserPath
import com.sesac.data.source.api.PathApi
import com.sesac.data.util.PolylineEncoder
import com.sesac.domain.model.BookmarkResponse
import com.sesac.domain.model.Coord
import com.sesac.domain.model.Diary
import com.sesac.domain.model.Like
import com.sesac.domain.model.Path
import com.sesac.domain.repository.PathRepository
import com.sesac.domain.result.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PathRepositoryImpl @Inject constructor(
    private val pathApi: PathApi,
    private val pathDao: PathDao
): PathRepository {
    override suspend fun getAllRecommendedPaths(coord: Coord?, radius: Float?): Flow<AuthResult<List<Path>>> = flow {
        emit(AuthResult.Loading)
        val paths = pathApi.getPaths(
//            token = "Bearer $token",
            lat = coord?.latitude,
            lng = coord?.longitude,
            radius = radius
        ).map { it.toPath() }
        Log.d("TAG-PathRepository", "Recommend Path : ${paths.map { it.thumbnail }}")
        emit(AuthResult.Success(paths))
    }.catch {
        Log.d("TAG-PathRepository", "Recommend Path error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun getPathById(id: Int): Flow<AuthResult<Path>> = flow {
        emit(AuthResult.Loading)
        val result = pathApi.getPathById(id).toPath()
        Log.d("TAG-PathRepository", "GET Path : $result")
        emit(AuthResult.Success(result))
    }.catch {
        Log.d("TAG-PathRepository", "GET Path error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun getMyPaths(token: String): Flow<AuthResult<List<Path>>> = flow {
        emit(AuthResult.Loading)
        val result = pathApi.getMyPaths("Bearer $token").toPathList()
        emit(AuthResult.Success(result))
    }.catch {
        Log.d("TAG-PathRepository", "My Path error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun createPath(token: String, path: Path): Flow<AuthResult<Path>> = flow {
        emit(AuthResult.Loading)
        // 🔹 Polyline 인코딩 적용
        val encodedPolyline = PolylineEncoder.encode(
            path.coord?.map { LatLng(it.latitude, it.longitude) } ?: emptyList()
        )

        // 🔹 DTO 생성 시 polyline 필드에 적용
        val request = path.toPathCreateRequestDTO()
        Log.d("PathRepository", "  path create request: $request")

        val createdPath = pathApi.createPath(
            token = "Bearer $token",
            request = request
        )
        Log.d("PathRepository", "  path create response: $createdPath")
        emit(AuthResult.Success(createdPath.toPath()))
    }.catch { e ->
        Log.e("PathRepository", "❌ Error: ${e.javaClass.simpleName}")
        Log.e("PathRepository", "❌ Message: ${e.message}")
        e.printStackTrace()
        emit(AuthResult.NetworkError(e))
    }

    override suspend fun updatePath(token: String, id: Int, updatedPath: Path): Flow<AuthResult<Path>> = flow {
        emit(AuthResult.Loading)
        val result = pathApi.updatePath("Bearer $token", id, updatedPath.toPathUpdateRequestDTO()).toPath()
        emit(AuthResult.Success(result))
    }.catch {
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun deletePath(token: String, id: Int): Flow<AuthResult<Unit>> = flow {
        emit(AuthResult.Loading)
        pathApi.deletePath("Bearer $token", id)
        emit(AuthResult.Success(Unit))
    }.catch {
        Log.d("TAG-PathRepository", "Delete Path error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun getDiary(pathId: Int): Flow<AuthResult<Diary>> = flow {
        emit(AuthResult.Loading)
        val result = pathApi.getDiary(pathId).toDiary()
        emit(AuthResult.Success(result))
    }.catch {
        Log.d("TAG-PathRepository", "get Diary error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun toggleBookmark(
        token: String,
        id: Int
    ): Flow<AuthResult<BookmarkResponse>> = flow {
        emit(AuthResult.Loading)
        val result = pathApi.bookmarkToggle("Bearer $token", id).toBookmarkResponse()
        emit(AuthResult.Success(result))
    }.catch {
        Log.d("TAG-PathRepository", "Toggle Bookmark error : $it")
        emit(AuthResult.NetworkError(it))
    }

    override suspend fun toggleLike(token: String, id: Int): Flow<AuthResult<Like>>  = flow {
        emit(AuthResult.Loading)
        val result = pathApi.likeToggle("Bearer $token", id).toDomain()
        emit(AuthResult.Success(result))
    }.catch {
        Log.d("TAG-TrailRepository", "Toggle Like error : $it")
        emit(AuthResult.NetworkError(it))
    }

    // ⭐ Local(Room)
    override suspend fun saveDraft(draft: Path): Flow<Path> = flow {
        val entity = draft.toPathEntity()
        val newId = pathDao.insertDraft(entity)
        val savedPath = draft.copy(id = newId.toInt())
        emit(savedPath)
    }

    override suspend fun getAllDrafts(): Flow<List<Path>> = flow {
        val entities = pathDao.getDraftsByStatus()
        emit(entities.map { it.toUserPath() }) // Mapper 필요
    }

    override suspend fun deleteDraft(draft: Path): Flow<Boolean> = flow {
        val entity = draft.toPathEntity()
        pathDao.deleteDraft(entity)
        emit(true)
    }

    override suspend fun clearAllDrafts(): Flow<Boolean> = flow {
        pathDao.clearAllDrafts()
        emit(true)
    }

}
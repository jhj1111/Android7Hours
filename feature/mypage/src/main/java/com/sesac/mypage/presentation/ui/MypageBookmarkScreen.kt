package com.sesac.mypage.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.component.CommonFilterTabs
import com.sesac.common.component.CommonListContainer
import com.sesac.common.model.PathParceler
import com.sesac.common.model.toPathParceler
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.mypage.presentation.MypageViewModel

@Composable
fun MypageBookmarkScreen(
    uiStatus: AuthUiState,
    viewModel: MypageViewModel = hiltViewModel(),
    onNavigateToPost: (Int) -> Unit = {},
    onNavigateToPathDetail: (PathParceler) -> Unit = {},
) {
    val filterOptions = listOf("산책로", "커뮤니티")
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val bookmarkedPaths by viewModel.bookmarkedPaths.collectAsStateWithLifecycle()
    val bookmarkedPosts by viewModel.bookmarkedPosts.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLoadingPath = remember(selectedPath) { selectedPath is ResponseUiState.Loading }

    LaunchedEffect(uiStatus) {
        if (uiStatus.isLoggedIn) {
            viewModel.getMyBookmarks(uiStatus.token)
        }
    }

    LaunchedEffect(selectedPath) {
        when (val state = selectedPath) {
            is ResponseUiState.Success -> {
                val pathParceler = state.result.toPathParceler()
                onNavigateToPathDetail(pathParceler)
                viewModel.resetSelectedPathState()
            }
            is ResponseUiState.Error -> {
                Toast.makeText(context, "산책로 정보를 불러오는 데 실패했습니다: ${state.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetSelectedPathState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CommonFilterTabs(
            filterOptions = filterOptions,
            selectedFilter = activeFilter,
            onFilterSelected = viewModel::onFilterChange,
            fiterIcons = listOf(Icons.TwoTone.Bookmarks, Icons.AutoMirrored.Filled.Chat),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        )

        if (isLoadingPath) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            when (activeFilter) {
                filterOptions[0] -> {
                    when (val state = bookmarkedPaths) {
                        is ResponseUiState.Loading -> CircularProgressIndicator()
                        is ResponseUiState.Success -> {
                            CommonListContainer(
                                title = "즐겨찾는 산책로",
                                itemList = state.result,
                                emptyStateMessage = "즐겨찾는 산책로가 없습니다",
                                emptyStateSubMessage = "산책로 페이지에서 ⭐를 눌러 추가해보세요",
                                itemContent = { path ->
                                    BookmarkedPathCard(
                                        uiState = uiStatus,
                                        path = path,
                                        onPathClick = { viewModel.getPathInfo(path.id) },
                                        onRemoveClick = viewModel::toggleBookmark,
                                    )
                                }
                            )
                        }
                        is ResponseUiState.Error -> Text(text = state.message)
                        else -> {}
                    }
                }
                filterOptions[1] -> {
                    when (val state = bookmarkedPosts) {
                        is ResponseUiState.Loading -> CircularProgressIndicator()
                        is ResponseUiState.Success -> {
                            CommonListContainer(
                                title = "즐겨찾는 게시글",
                                itemList = state.result,
                                emptyStateMessage = "즐겨찾는 게시글이 없습니다",
                                emptyStateSubMessage = "커뮤니티에서 ♥를 눌러 추가해보세요",
                                itemContent = { bookmarkedPost ->
                                    BookmarkedPostCard(
                                        uiState = uiStatus,
                                        bookmarkedPost = bookmarkedPost,
                                        onPostClick = {},
                                        onRemoveClick = viewModel::toggleBookmark
                                    )
                                }
                            )
                        }
                        is ResponseUiState.Error -> Text(text = state.message)
                        else -> {}
                    }
                }
            }
        }
    }
}

// BookmarkedPost를 Post로 변환하는 임시 확장 함수
// 이상적으로는 data layer의 mapper에 위치해야 함
//private fun BookmarkedPost.toPost(): com.sesac.domain.model.Post {
//    return com.sesac.domain.model.Post(
//        id = this.id,
//        userId = this.userId,
//        authUserNickname = this.authUserNickname,
//        authUserProfileImageUrl = this.authUserProfileImageUrl,
//        postType = this.postType,
//        title = this.title,
//        image = this.image,
//        viewCount = this.viewCount,
//        commentCount = this.commentCount,
//        likeCount = this.likeCount,
//        bookmarkCount = this.bookmarkCount,
//        isLiked = this.isLiked,
//        isBookmarked = this.isBookmarked,
//        createdAt = java.util.Date(), // Mapper에서 실제 Date로 변환 필요
//        updatedAt = java.util.Date(), // Mapper에서 실제 Date로 변환 필요
//        content = "" // BookmarkedPost에는 content가 없으므로 빈 문자열로 처리
//    )
//}

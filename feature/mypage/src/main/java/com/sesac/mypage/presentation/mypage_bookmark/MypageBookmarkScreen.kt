package com.sesac.mypage.presentation.mypage_bookmark

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.R
import com.sesac.common.component.CommonFilterTabs
import com.sesac.common.component.CommonListContainer
import com.sesac.common.model.PathParceler
import com.sesac.common.model.toPathParceler
import com.sesac.common.ui.theme.paddingMedium
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
            viewModel.getMyBookmarks(uiStatus)
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
            horizontalArrangement = Arrangement.spacedBy(paddingMedium, Alignment.CenterHorizontally)
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
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                title = stringResource(R.string.mypage_favorite_path),
                                itemList = state.result,
                                emptyStateMessage = stringResource(R.string.mypage_favorite_path_empty),
                                emptyStateSubMessage = stringResource(R.string.mypage_favorite_path_empty_submessage),
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
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                title = stringResource(R.string.mypage_favorite_post),
                                itemList = state.result,
                                emptyStateMessage = stringResource(R.string.mypage_favorite_post_empty),
                                emptyStateSubMessage = stringResource(R.string.mypage_favorite_post_empty_submessage),
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
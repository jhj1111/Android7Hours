package com.sesac.trail.presentation.trail_main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sesac.common.ui.theme.SheetMinHeight
import com.sesac.domain.model.Path
import com.sesac.domain.model.User
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.trail.presentation.component.BottomSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    activeTab: WalkPathTab,
    recommendedPathsState: ResponseUiState<List<Path>>,
    myPathsState: ResponseUiState<List<Path>>,
    uiState: AuthUiState,
    currentUser: User?,
    onSheetOpenToggle: () -> Unit,
    onStartRecording: () -> Unit,
    onTabChange: (WalkPathTab) -> Unit,
    onPathClick: (Path) -> Unit,
    onFollowClick: (Path) -> Unit,
    onModifyClick: (Path) -> Unit,
    onDeleteClick: (Int) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = SheetMinHeight,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContent = {
            val activeState =
                if (activeTab == WalkPathTab.RECOMMENDED) {
                    recommendedPathsState
                } else {
                    myPathsState
                }

            when (activeState) {
                is ResponseUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ResponseUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = activeState.message)
                    }
                }

                else -> { // Success / Idle
                    BottomSheetContent(
                        uiState = uiState,
                        activeTab = activeTab,
                        recommendedPaths =
                            (recommendedPathsState as? ResponseUiState.Success)?.result
                                ?: emptyList(),
                        myPaths =
                            (myPathsState as? ResponseUiState.Success)?.result
                                ?: emptyList(),
                        currentUser = currentUser,
                        onSheetOpenToggle = onSheetOpenToggle,
                        onStartRecording = onStartRecording,
                        onTabChange = onTabChange,
                        onPathClick = onPathClick,
                        onFollowClick = onFollowClick,
                        onModifyClick = onModifyClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
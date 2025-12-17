package com.sesac.trail.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.PrimaryGreenDark
import com.sesac.common.ui.theme.PrimaryGreenLight
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.Path
import com.sesac.domain.model.User
import com.sesac.common.ui_state.AuthUiState
import com.sesac.trail.presentation.ui.WalkPathTab

@Composable
fun BottomSheetContent(
    uiState: AuthUiState,
    activeTab: WalkPathTab,
    recommendedPaths: List<Path>,
    myPaths: List<Path>,
    currentUser: User?,
    onSheetOpenToggle: () -> Unit,
    onStartRecording: () -> Unit,
    onTabChange: (WalkPathTab) -> Unit,
    onPathClick: (Path) -> Unit,
    onFollowClick: (Path) -> Unit,
    onModifyClick: (Path) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Surface(
//        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Handle
//            Box(
//                modifier = Modifier
//                    .padding(vertical = paddingSmall)
//                    .width(SheetHandleWidth)
//                    .height(SheetHandleHeight)
//                    .background(SheetHandle, CircleShape)
//                    .clickable { onSheetOpenToggle() }
//            )

            // Record Button
            Button(
                onClick = onStartRecording,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingLarge)
                    .padding(top = paddingMicro, bottom = paddingSmall)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = PrimaryGreenDark)
                Spacer(Modifier.width(paddingMicro))
                Text("산책로 기록", color = PrimaryGreenDark, fontWeight = FontWeight.Bold)
            }

            // Tabs
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = White,
                contentColor = Purple600,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                        color = Purple600
                    )
                },
                modifier = Modifier.padding(horizontal = paddingLarge)
            ) {
                Tab(
                    selected = activeTab == WalkPathTab.RECOMMENDED,
                    onClick = { onTabChange(WalkPathTab.RECOMMENDED) },
                    text = { Text("추천 산책로") },
                    selectedContentColor = Purple600,
                    unselectedContentColor = GrayTabText
                )
                Tab(
                    selected = activeTab == WalkPathTab.MY_RECORDS,
                    onClick = { onTabChange(WalkPathTab.MY_RECORDS) },
                    text = { Text("내 기록") },
                    selectedContentColor = Purple600,
                    unselectedContentColor = GrayTabText
                )
            }

            // Content - 이제 PathListContent 하나로 통합됩니다.
            Box(
                modifier = Modifier
                    .padding(horizontal = paddingLarge)
                    .padding(bottom = paddingLarge)
            ) {
                val pathsToShow = if (activeTab == WalkPathTab.RECOMMENDED) recommendedPaths else myPaths
                PathListContent(
                    paths = pathsToShow,
                    currentUser = currentUser,
                    onPathClick = onPathClick,
                    onFollowClick = onFollowClick,
                    onModifyClick = onModifyClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}
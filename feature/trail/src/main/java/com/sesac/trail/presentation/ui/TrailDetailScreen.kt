package com.sesac.trail.presentation.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sesac.common.component.CommonCommentSection
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.GrayTabText
import com.sesac.common.ui.theme.PaddingSection
import com.sesac.common.ui.theme.PrimaryPurpleLight
import com.sesac.common.ui.theme.Purple600
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.fixImageUrl
import com.sesac.common.utils.samplePathUrl
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.trail.nav_graph.TrailNavigationRoute
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.component.TagFlow


// --- Main Composable ---
@Composable
fun TrailDetailScreen(
    uiState: AuthUiState,
    viewModel: TrailViewModel = hiltViewModel<TrailViewModel>(),
    navController: NavController,
    selectedDetailPath: Path?,
    onStartFollowing: (Path) -> Unit,
    onEditClick: (Path) -> Unit,
    onDeleteClick: (Path) -> Unit,
) {
    val context = LocalContext.current
    val selectedDetailPathState by viewModel.selectedPath.collectAsStateWithLifecycle()
    val bookmarkedPathsState by viewModel.bookmarkedPaths.collectAsStateWithLifecycle()
    val commentsState by viewModel.commentsState.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    val isBookmarked by remember(bookmarkedPathsState, selectedDetailPathState) {
        derivedStateOf {
            val paths = (bookmarkedPathsState as? ResponseUiState.Success)?.result ?: emptyList()
            val currentPathId = selectedDetailPathState?.id
            if (currentPathId == null) false else paths.any { it.id == currentPathId }
        }
    }



    LaunchedEffect(selectedDetailPath) {
        selectedDetailPath?.let { selected ->
            viewModel.getCurrentUserInfo()
            viewModel.updateSelectedPath(selected)
            viewModel.getComments(selected.id)
            viewModel.getUserBookmarkedPaths(uiState.token)
        }
    }

    selectedDetailPathState?.let { selected ->
        val handleBookmark: () -> Unit = {
            viewModel.toggleBookmark(uiState.token, selected.id)
            val message = if (isBookmarked) "즐겨찾기에서 제거합니다." else "즐겨찾기에 추가합니다."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            PathImageHeader(
                pathName = selected.pathName,
                isBookmarked = isBookmarked,
                onBookmarkClick = handleBookmark,
                imageUrl = selected.thumbnail ?: samplePathUrl,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                verticalArrangement = Arrangement.spacedBy(PaddingSection)
            ) {
                // Title & Uploader
                Column {
                    Text(
                        text = selected.pathName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(paddingMicro))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Uploader",
                                modifier = Modifier.size(16.dp),
                                tint = GrayTabText
                            )
                            Spacer(Modifier.width(paddingMicro))
                            Text(
                                text = "@${selected.uploader}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayTabText
                            )
                        }
                        if (selected.uploader == uiState.user?.nickname) {
                            Row {
                                TextButton(
//                                    onClick = { onEditClick(selected) },
                                    onClick = { navController.navigate(TrailNavigationRoute.TrailCreateTab) },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("수정", color = GrayTabText, fontSize = 14.sp)
                                }
                                TextButton(
//                                    onClick = { onDeleteClick(selected) },
                                    onClick = {
                                        viewModel.deletePath(selected.id)
                                        navController.popBackStack()
                                    },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("삭제", color = GrayTabText, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                // Follow Button
                Button(
                    onClick = {
                        onStartFollowing(selected)
                        navController.navigate(TrailNavigationRoute.TrailMainTab)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = null)
                    Spacer(Modifier.width(paddingMicro))
                    Text("이 산책로 따라가기", fontWeight = FontWeight.Bold, color = White)
                }

                // Stats Grid
                Column(verticalArrangement = Arrangement.spacedBy(paddingSmall)) {
                    Row(horizontalArrangement = spacedBy(paddingSmall)) {
                        InfoCard(
                            icon = Icons.Filled.LocationOn,
                            label = "거리",
                            value = selected.distance.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Filled.Schedule,
                            label = "소요시간",
                            value = selected.duration.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = spacedBy(paddingSmall)) {
                        InfoCard(
                            icon = Icons.Filled.Favorite,
                            label = "좋아요",
                            value = "${selected.bookmarkCount}개",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            label = "내 위치에서",
                            value = selected.distanceFromMe.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                // Route Features
                PathSection(title = "코스 특징") {
                    if (selectedDetailPath!!.tags.isNotEmpty()) {
                        TagFlow(
                            selectedTags = selected.tags,
                            editable = false
                        )
                    } else {
                        Text(
                            text = "등록된 코스 특징이 없습니다.",
                            color = GrayTabText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Description
                PathSection(title = "산책로 소개") {
                    Text(
                        text = selected.pathComment ?: "소개글이 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayTabText,
                        lineHeight = 24.sp
                    )
                }


                // Reviews
                PathSection(title = "이용자 후기") {
                    CommonCommentSection(
                        commentsState = commentsState,
                        currentUserId = userInfo?.id ?: -1,
                        onPostComment = { content ->
                            uiState.token?.let { token ->
                                viewModel.createComment(token, selected.id, content)
                            } ?: Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        },
                        onUpdateComment = { commentId, content ->
                            uiState.token?.let { token ->
                                viewModel.updateComment(token, selected.id, commentId, content)
                            } ?: Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteComment = { commentId ->
                            uiState.token?.let { token ->
                                viewModel.deleteComment(token, selected.id, commentId)
                            } ?: Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}


// --- Image Header ---
@Composable
fun PathImageHeader(
    pathName: String,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
//    imageUrl: String = "http://192.168.0.216:9000/paths/media/path_thumbnails/2/ff1b9952783e4ba2b35581ab6403d951.png"
    imageUrl: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
    ) {
        Log.d("TAG-TrailDetailScreen", "imageUrl : ${fixImageUrl(imageUrl)}")
        AsyncImage(
            model = fixImageUrl(imageUrl),
            contentDescription = pathName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        FloatingActionButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(paddingLarge),
            containerColor = if (isBookmarked) Purple600 else MaterialTheme.colorScheme.surface,
            contentColor = if (isBookmarked) White else GrayTabText,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "좋아요"
            )
        }
    }
}

// --- InfoCard ---
@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurpleLight)
    ) {
        Column(modifier = Modifier.padding(paddingSmall)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = paddingMicro)
            ) {
                Icon(icon, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(paddingMicro))
                Text(label, style = MaterialTheme.typography.bodySmall, color = GrayTabText)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// --- Section ---
@Composable
fun PathSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(paddingMicro))
        content()
    }
}


//@Preview(showBackground = true)
//@Composable
//fun WalkPathDetailPagePreview() {
//    val dummyPosition = Coord(
//        latitude = 0.5,
//        longitude = 0.5,
//    )
//    val mockPath = UserPath(
//        id = 1,
//        name = "강남역 주변 산책로",
//        userId = -1,
//        distance = 1.5f,
//        time = 15,
//        likes = 45,
//        distanceFromMe = 0.3f,
//        coord = listOf(dummyPosition),
//        tags = listOf("🌳 자연 친화적", "🐕 반려견 동반 가능", "🌸 꽃길","👨‍👩‍👧‍👦 가족 동반")
//    )
//
//    val navController = rememberNavController()
//    Android7HoursTheme {
//        TrailDetailScreen(
////            path = mockPath,
//            navController = navController,
//            onStartFollowing = {}
//        )
//    }
//}

/*
@Composable
fun PathImageHeader(
    pathName: String,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    imageUrl
 */

@Preview
@Composable
fun PathImageHeaderPreview(){
    Android7HoursTheme {
        val url = samplePathUrl
        PathImageHeader(
            pathName = "강남역 주변 산책로",
            isBookmarked = true,
            onBookmarkClick = {},
//            imageUrl = "data:image/jpeg;base64,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",
            imageUrl = url
        )
    }
}

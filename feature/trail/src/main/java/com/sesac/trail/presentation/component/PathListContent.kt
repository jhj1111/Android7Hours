package com.sesac.trail.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.ColorBlue
import com.sesac.common.ui.theme.ColorGreen
import com.sesac.common.ui.theme.ColorOrange
import com.sesac.common.ui.theme.PrimaryGreenLight
import com.sesac.common.ui.theme.Red500
import com.sesac.common.ui.theme.cardImageHeight
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.iconSizeMedium
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.samplePathUrl
import com.sesac.domain.model.Path
import com.sesac.domain.model.User

/**
 * '추천 산책로'와 '내 기록' 탭의 리스트를 표시하는 통합 Composable
 *
 * @param paths 표시할 산책로 리스트
 * @param currentUser 현재 로그인한 사용자 정보
 * @param onPathClick 산책로 아이템 클릭 시 콜백
 * @param onFollowClick '따라가기' 버튼 클릭 시 콜백
 * @param onModifyClick '수정' 메뉴 클릭 시 콜백
 * @param onDeleteClick '삭제' 메뉴 클릭 시 콜백
 */
@Composable
fun PathListContent(
    paths: List<Path>,
    currentUser: User?,
    onPathClick: (Path) -> Unit,
    onFollowClick: (Path) -> Unit,
    onModifyClick: (Path) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = paddingLarge),
        verticalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        items(paths, key = { it.id }) { path ->
            PathItem(
                path = path,
                currentUser = currentUser,
                onPathClick = onPathClick,
                onFollowClick = onFollowClick,
                onModifyClick = onModifyClick,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

/**
 * 산책로 정보를 보여주는 카드 아이템 Composable.
 * 현재 사용자가 산책로 작성자인 경우 수정/삭제 메뉴를 포함합니다.
 */
@Composable
fun PathItem(
    path: Path,
    currentUser: User?,
    onPathClick: (Path) -> Unit,
    onFollowClick: (Path) -> Unit,
    onModifyClick: (Path) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    // 현재 사용자가 이 산책로의 작성자인지 확인
    val isMyPath = currentUser?.nickname != null && currentUser.nickname == path.uploader

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingLarge)
            .clickable { onPathClick(path) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 썸네일 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardImageHeight)
                    .background(PrimaryGreenLight),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(path.thumbnail ?: samplePathUrl)
                        .crossfade(true)
                        .scale(Scale.FILL)
                        .build(),
                    contentDescription = path.pathName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            // 2. 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                verticalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                // 헤더: 산책로 이름, 작성자, 그리고 수정/삭제 메뉴
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = path.pathName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(paddingMicro))
                        Text(
                        text = "@${path.uploader}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 내 경로인 경우에만 수정/삭제 메뉴 표시
                    if (isMyPath) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "옵션 더보기")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = {
                                        onModifyClick(path)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제") },
                                    onClick = {
                                        onDeleteClick(path.id)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 핵심 정보: 거리, 시간, 난이도
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    // m 단위를 km로 변환하여 표시
                    val distanceInKm = "%.1f".format(path.distance / 1000f)
                    InfoChip(icon = Icons.Outlined.Route, text = "${distanceInKm}km", iconTint = ColorGreen)
                    InfoChip(icon = Icons.Outlined.Timer, text = "${path.duration/60}분", iconTint = ColorBlue)
                    InfoChip(icon = Icons.Outlined.BarChart, text = "난이도 ${path.level}", iconTint = ColorOrange)
                }

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // 사용자 관련 정보 및 액션 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(paddingSmall)) {
                        path.distanceFromMe?.let {
                            val distanceFromMeInKm = "%.1f".format(it / 1000f)
                            InfoChip(
                                icon = Icons.Outlined.DirectionsWalk,
                                text = "나와 ${distanceFromMeInKm}km",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                        InfoChip(
                            icon = Icons.Outlined.FavoriteBorder,
                            // Django Serializer의 bookmark_count 필드와 매칭
                            text = "${path.bookmarkCount} 즐겨찾기",
                            style = MaterialTheme.typography.bodyMedium,
                            iconTint = Red500
                        )
                    }

                    Button(
                        onClick = { onFollowClick(path) },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Route, contentDescription = null)
                        Spacer(modifier = Modifier.width(paddingSmall))
                        Text(stringResource(id = R.string.trail_button_follow))
                    }
                }
            }
        }
    }
}

/**
 * 아이콘과 텍스트를 함께 보여주는 작은 재사용 가능 Composable
 */
@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(iconSizeMedium),
            tint = iconTint
        )
        Spacer(Modifier.width(paddingSmall))
        Text(
            text = text,
            style = style,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun PathListContentPreview() {
    Android7HoursTheme {
        PathListContent(
            paths = listOf(Path.EMPTY.copy(duration = 120)),
            currentUser = User(username = "tt", nickname = "nn", fullName = "name", email = ""),
            onPathClick = {},
            onDeleteClick = {},
            onFollowClick = {},
            onModifyClick = {},
        )
    }
}
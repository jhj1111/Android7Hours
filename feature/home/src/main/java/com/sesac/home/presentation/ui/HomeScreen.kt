package com.sesac.home.presentation.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sesac.common.component.CommonLazyRow
import com.sesac.common.model.PathParceler
import com.sesac.common.model.toPathParceler
import com.sesac.common.ui.theme.bannerHeight
import com.sesac.common.ui.theme.cardWidth
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import com.sesac.home.presentation.HomeViewModel
import kotlinx.coroutines.delay
import com.sesac.common.R as cR


// --- 4. HomePage ---
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    uiState: AuthUiState,
    onNavigateToPathDetail: (PathParceler?) -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
) {
    val context = LocalContext.current
    val banners by viewModel.bannerList.collectAsStateWithLifecycle()
    val pathList by viewModel.recommendPathList.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        viewModel.getRecommendedPaths()
    }

    LaunchedEffect(pagerState.pageCount) {
        if (pagerState.pageCount > 1) {
            while (true) {
                delay(5000)
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % pagerState.pageCount
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                BannerSectionView(
                    banners = banners,
                    modifier = Modifier.padding(top = paddingMedium),
                    pagerState = pagerState,
                )
            }

            item {
                CommonLazyRow(
                    title = "산책로 추천",
                    items = pathList,
                ) { path ->
                    ContentCardView(
                        data = path,
                        onClick = { onNavigateToPathDetail(path?.toPathParceler()) },
                        modifier = Modifier.width(cardWidth)
                    )
                }
            }

            item {
                CommunityCard(
                    image = cR.drawable.community_banner,
                    onClick = onNavigateToCommunity,
                    modifier = Modifier.padding(horizontal = paddingLarge, vertical = paddingMedium)
                )
            }
        }
    }
}

// CommunityCard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityCard(
    modifier: Modifier = Modifier,
    image: Any,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(cR.string.community),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = paddingMedium)
        )
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(bannerHeight),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(image)
                        .crossfade(true)
                        .scale(Scale.FILL)
                        .build(),
                    contentDescription = "Community",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                Text(
                    text = stringResource(cR.string.home_community_banner_text),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart).padding(paddingLarge)
                )
            }
        }
    }
}

// --- 6. Preview ---
//@Preview(showBackground = true)
//@Composable
//fun HomePagePreview() {
//    Android7HoursTheme {
//        HomeScreen(onNavigateToWalkPath = {}, onNavigateToCommunity = {})
//    }
//}

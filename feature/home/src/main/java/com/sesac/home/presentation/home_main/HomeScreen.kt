package com.sesac.home.presentation.home_main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sesac.common.component.CommonLazyRow
import com.sesac.common.model.PathParceler
import com.sesac.common.model.toPathParceler
import com.sesac.common.ui.theme.cardWidth
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.home.presentation.HomeViewModel
import kotlinx.coroutines.delay
import com.sesac.common.R


// --- 4. HomePage ---
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
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
                    title = stringResource(R.string.home_carousel_title_trail_recommendation),
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
                CommunityCardView(
                    image = R.drawable.community_banner,
                    onClick = onNavigateToCommunity,
                    modifier = Modifier.padding(horizontal = paddingLarge, vertical = paddingMedium)
                )
            }
        }
    }
}
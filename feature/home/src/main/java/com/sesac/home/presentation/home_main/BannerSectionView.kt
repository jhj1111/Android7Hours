package com.sesac.home.presentation.home_main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sesac.common.ui.theme.bannerHeight
import com.sesac.common.ui.theme.buttonRound
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.domain.model.BannerData

@Composable
fun BannerSectionView(
    banners: List<BannerData?>,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    ) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = paddingLarge),
            pageSpacing = paddingMedium
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier.fillMaxWidth().height(bannerHeight),
                shape = RoundedCornerShape(buttonRound)
            ) {
                BannerCardContentView(banner = banner)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingMedium),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                PageIndicatorView(isSelected = index == pagerState.currentPage)
            }
        }
    }
}



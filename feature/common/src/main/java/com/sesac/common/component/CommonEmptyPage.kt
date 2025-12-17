package com.sesac.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray400
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.cardImageSizeSmall
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun CommonEmptyPage(
    placeHolder: Painter? = null,
    title: String? = null,
    subTitle: String? = null,
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = paddingLarge),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = placeHolder ?: painterResource(id = R.drawable.placeholder),
            contentDescription = "Empty Pet",
            modifier = Modifier.Companion.size(cardImageSizeSmall)
        )
        Spacer(modifier = Modifier.height(paddingMedium))
        Text(
            text = title ?: stringResource(R.string.common_empty_page),
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Companion.Bold,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(paddingSmall))
        Text(
            text = subTitle ?: stringResource(R.string.common_empty_page_submessage),
            style = MaterialTheme.typography.bodyMedium.copy(color = Gray400),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun EmptyPetViewPreview(){
    Android7HoursTheme {
        CommonEmptyPage(
            placeHolder = painterResource(R.drawable.placeholder),
            title = stringResource(R.string.mypage_pet_empty_list),
            subTitle = stringResource(R.string.mypage_pet_empty_list_subtitle),
        )
    }
}
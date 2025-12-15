package com.sesac.auth.presentation.login_screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sesac.common.R
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.ui.theme.paddingExtraLarge
import com.sesac.common.ui.theme.paddingMedium

@Composable
fun AuthLoginHeaderView() {
    Icon(
        painterResource(R.drawable.image7hours),
        contentDescription = "App Logo",
        modifier = Modifier.size(iconSizeLarge),
        tint = Color.Unspecified,
    )
    Spacer(modifier = Modifier.height(paddingMedium))
    Text(
        text = stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(paddingExtraLarge))
}
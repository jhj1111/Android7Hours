package com.sesac.mypage.presentation.mypage_setting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.badgeEnabledBg
import com.sesac.common.ui.theme.badgeEnabledText
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.shapeIcon

@Composable
fun EnabledBadgeView(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = shapeIcon,
        color = badgeEnabledBg
    ) {
        Text(
            text = stringResource(id = R.string.mypage_setting_enabled),
            color = badgeEnabledText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = paddingSmall, vertical = paddingMicro)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnabledBadgeViewPreview() {
    Android7HoursTheme {
        EnabledBadgeView()
    }
}
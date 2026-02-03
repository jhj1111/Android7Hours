package com.sesac.mypage.presentation.mypage_setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.infoBoxBg
import com.sesac.common.ui.theme.infoBoxBorder
import com.sesac.common.ui.theme.infoBoxText
import com.sesac.common.ui.theme.infoBoxTitle
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.shapeCard

@Composable
fun InfoBoxView(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = shapeCard,
        color = infoBoxBg,
        border = BorderStroke(borderMicro, infoBoxBorder)
    ) {
        Column(modifier = Modifier.padding(paddingLarge)) {
            Text(
                text = stringResource(id = R.string.mypage_setting_permission_info_title),
                fontWeight = FontWeight.Bold,
                color = infoBoxTitle,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(paddingSmall))
            Column(verticalArrangement = Arrangement.spacedBy(paddingMicro)) {
                listOf(
                    R.string.mypage_setting_permission_info_camera,
                    R.string.mypage_setting_permission_info_gps,
                    R.string.mypage_setting_permission_info_notification
                ).forEach { id ->
                    Text(
                        text = "• ${stringResource(id = id)}",
                        color = infoBoxText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoBoxViewPreview() {
    Android7HoursTheme {
        InfoBoxView()
    }
}

package com.sesac.mypage.presentation.mypage_setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.NoteBox
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.shapeCard

@Composable
fun PrivacyNoteView(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = shapeCard,
        color = NoteBox
    ) {
        Text(
            text = stringResource(id = R.string.mypage_setting_privacy_note),
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyNoteViewPreview() {
    Android7HoursTheme {
        PrivacyNoteView()
    }
}

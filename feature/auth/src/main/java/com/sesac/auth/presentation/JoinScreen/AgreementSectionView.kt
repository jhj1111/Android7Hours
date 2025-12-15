package com.sesac.auth.presentation.JoinScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme

/**
 * 약관 동의 섹션
 */
@Composable
fun AgreementSectionView(
    agreeAll: Boolean, onAgreeAllChange: (Boolean) -> Unit,
    agreeAge: Boolean, onAgreeAgeChange: (Boolean) -> Unit,
    agreeTerms: Boolean, onAgreeTermsChange: (Boolean) -> Unit,
    agreePrivacy: Boolean, onAgreePrivacyChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeAll, onCheckedChange = onAgreeAllChange)
            Text(stringResource(id = R.string.auth_join_agreement_all), fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.LightGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeAge, onCheckedChange = onAgreeAgeChange)
            Text(stringResource(id = R.string.auth_join_agreement_age), color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeTerms, onCheckedChange = onAgreeTermsChange)
            Text(stringResource(id = R.string.auth_join_agreement_terms), color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreePrivacy, onCheckedChange = onAgreePrivacyChange)
            Text(stringResource(id = R.string.auth_join_agreement_privacy), color = Color.Gray)
        }
    }
}

@Preview
@Composable
fun AgreementSectionViewPreview(){
    Android7HoursTheme {
        AgreementSectionView(
            agreeAll = true,
            onAgreeAgeChange = { _ -> },
            agreeAge = true,
            onAgreeAllChange = { _ -> },
            agreeTerms = false,
            onAgreeTermsChange = { _ -> },
            agreePrivacy = false,
            onAgreePrivacyChange = { _ -> }
        )
    }
}
package com.sesac.auth.presentation.login_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun LoginFooterLinksView(
    onSignUpClick: () -> Unit,
    onFindAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = paddingSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onSignUpClick) {
            Text(stringResource(id = R.string.auth_signup_button))
        }
        Text("|", color = TextSecondary)
        TextButton(onClick = onFindAccountClick) {
            Text(stringResource(R.string.auth_find_id_and_password))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFooterLinksViewPreview() {
    Android7HoursTheme {
        LoginFooterLinksView(
            onSignUpClick = {},
            onFindAccountClick = {}
        )
    }
}
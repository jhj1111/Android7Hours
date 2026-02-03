package com.sesac.monitor.presentation.monitor_cam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.paddingLarge

@Composable
fun CallingContent(petName: String, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.Companion.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Text(
            "${petName}${stringResource(R.string.monitor_pet_connecting_now_message)}",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.Companion.height(paddingLarge))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.Companion.height(paddingLarge))
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.common_action_cancel))
        }
    }
}

@Preview
@Composable
fun CallingContentPreview(){
    Android7HoursTheme {
        CallingContent(
            petName = "뽀삐",
            onCancel = {},
        )
    }
}
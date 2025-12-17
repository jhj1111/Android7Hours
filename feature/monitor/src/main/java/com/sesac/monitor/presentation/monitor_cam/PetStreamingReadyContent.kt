package com.sesac.monitor.presentation.monitor_cam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium

@Composable
fun PetStreamingReadyContent(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.Companion.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Text(stringResource(R.string.monitor_ready), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.Companion.height(paddingMedium))
        Text(stringResource(R.string.monitor_pet_stream_message), textAlign = TextAlign.Companion.Center)
        Spacer(modifier = Modifier.Companion.height(paddingLarge))
        Button(onClick = onStartClick) {
            Text(stringResource(R.string.monitor_ready_set))
        }
    }
}

@Preview
@Composable
fun PetStreamingReadyContentPreview(){
    Android7HoursTheme {
        PetStreamingReadyContent(
            onStartClick = {},
        )
    }
}
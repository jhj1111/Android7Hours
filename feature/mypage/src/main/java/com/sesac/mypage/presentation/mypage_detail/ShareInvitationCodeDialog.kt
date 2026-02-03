package com.sesac.mypage.presentation.mypage_detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray300
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.InvitationCode

// NEW: ShareInvitationCodeDialog Composable
@Composable
fun ShareInvitationCodeDialog(
    invitationCodeState: ResponseUiState<InvitationCode>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
//    val clipboardManager = LocalClipboardManager.current
//    val clipboardManager = LocalClipboard.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var codeToShare by remember { mutableStateOf("") }
    val codeMessage = stringResource(R.string.mypage_create_pet_code)
    val petCodeInfoMessage = "${stringResource(R.string.mypage_pet_code)} : $codeToShare${stringResource(R.string.mypage_pet_code_info_text)}"
    val petCodePastedMessage = stringResource(R.string.mypage_pet_code_pasted)

    LaunchedEffect(invitationCodeState) {
        if (invitationCodeState is ResponseUiState.Success) {
            codeToShare = invitationCodeState.result.code
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.mypage_pet_code)) },
        text = {
            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                when (invitationCodeState) {
                    is ResponseUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.Companion.padding(paddingLarge))
                        Text(stringResource(R.string.mypage_pet_code_creating))
                    }

                    is ResponseUiState.Success -> {
                        Text(stringResource(R.string.mypage_pet_code_created))
                        Spacer(modifier = Modifier.Companion.height(paddingMedium))
                        Row(
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .border(borderMicro, Gray300, MaterialTheme.shapes.small)
                                .padding(paddingMedium)
                        ) {
                            Text(
                                codeToShare,
                                style = Typography.titleLarge,
                                fontWeight = FontWeight.Companion.Bold
                            )
                            Spacer(modifier = Modifier.Companion.width(paddingSmall))
                            IconButton(onClick = {
//                                clipboardManager.setText(AnnotatedString(codeToShare))
                                val clip = ClipData.newPlainText(codeMessage, codeToShare)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context, petCodePastedMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code")
                            }
                        }
                    }

                    is ResponseUiState.Error -> {
                        Text("${stringResource(R.string.mypage_pet_code_create_failed)}${invitationCodeState.message}")
                    }

                    else -> { /* Idle state, shouldn't happen when dialog is shown */
                    }
                }
            }
        },
        confirmButton = {
            if (invitationCodeState is ResponseUiState.Success) {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                petCodeInfoMessage
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Code")
                        Spacer(modifier = Modifier.Companion.width(paddingSmall))
                        Text(stringResource(R.string.common_share))
                    }
                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.common_close))
                    }
                }
            } else {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.common_close))
                }
            }
        }
    )
}

@Preview
@Composable
fun ShareInvitationCodeDialogPreview(){
    Android7HoursTheme {
        ShareInvitationCodeDialog(
            invitationCodeState = ResponseUiState.Success("", InvitationCode("abc".repeat(10))),
            onDismiss = {},
        )
    }
}
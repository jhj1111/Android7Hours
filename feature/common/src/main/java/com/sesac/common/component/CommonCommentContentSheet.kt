package com.sesac.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.paddingExtraLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.config.sampleIconImageUrl
import com.sesac.domain.model.Comment

@Composable
fun CommonCommentSheetContent(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
    newCommentContent: String,
    isLoggedIn: Boolean,
    onNewCommentChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onLoginRequest: () -> Unit
) {
    // 최신 정렬 (새 댓글이 위로)
    val sortedComments = comments.sortedByDescending { it.createdAt }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        // Header
        Text(
            text = "${stringResource(R.string.common_comment)} (${comments.size})",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(paddingLarge)
        )

        // 댓글 리스트 (최신순)
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false) // 남은 공간만 차지
                .fillMaxWidth()
                .padding(horizontal = paddingLarge),
            verticalArrangement = Arrangement.spacedBy(paddingMedium)
        ) {
            if (sortedComments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingExtraLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.common_empty_comment), color = Gray500)
                    }
                }
            } else {
                items(sortedComments, key = { it.id }) { comment ->
//                    CommentItemView(comment)
                    CommonCommentItem(comment)
                }
            }
        }

        // 입력창 - 항상 하단에 고정
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newCommentContent,
                onValueChange = onNewCommentChange,
                placeholder = {
                    Text(
                        if (isLoggedIn) stringResource(R.string.comment_placeholder_comment_write)
                        else stringResource(R.string.auth_login_require_message)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged {
                        if (it.isFocused && !isLoggedIn) {
                            onLoginRequest()
                            focusManager.clearFocus()
                        }
                    },
                readOnly = !isLoggedIn,
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(paddingSmall))

            IconButton(
                onClick = onAddComment,
                enabled = newCommentContent.isNotBlank() && isLoggedIn
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "댓글 작성",
                    tint = if (newCommentContent.isNotBlank() && isLoggedIn)
                        MaterialTheme.colorScheme.primary else Gray500
                )
            }
        }
    }
}

@Preview
@Composable
fun CommunityCommentSheetContentPreview() {
    Android7HoursTheme {
        CommonCommentSheetContent(
            comments = listOf(
                Comment.EMPTY.copy(
                    authorNickName = "홍동길",
                    authorImage = sampleIconImageUrl,
                    content = "댓글1111",
                    timeAgo = "10년 전",
                )
            ),
            newCommentContent = "댓글 입력 a a a a a",
            isLoggedIn = true,
            onNewCommentChange = {},
            onAddComment = {},
            onLoginRequest = {}
        )
    }
}

@Preview
@Composable
fun CommunityCommentSheetContentEmptyPreview() {
    Android7HoursTheme {
        CommonCommentSheetContent(
            comments = emptyList(),
            newCommentContent = "",
            isLoggedIn = true,
            onNewCommentChange = {},
            onAddComment = {},
            onLoginRequest = {}
        )
    }
}

@Preview
@Composable
fun CommunityCommentSheetContentLoggedOutPreview() {
    Android7HoursTheme {
        CommonCommentSheetContent(
            comments = emptyList(),
            newCommentContent = "",
            isLoggedIn = false,
            onNewCommentChange = {},
            onAddComment = {},
            onLoginRequest = {},
        )
    }
}

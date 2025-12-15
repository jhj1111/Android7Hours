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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.utils.samplePathUrl
import com.sesac.domain.model.Comment

@Composable
fun CommonCommentSheetContent(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
    newCommentContent: String,
    onNewCommentChange: (String) -> Unit,
    onAddComment: () -> Unit
) {
    // 최신순으로 정렬 (새 댓글이 위로)
    val sortedComments = comments.sortedByDescending { it.createdAt }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        // Header
        Text(
            text = "댓글 (${comments.size})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // 댓글 리스트 (최신순)
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false) // 남은 공간만 차지
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (sortedComments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("첫 댓글을 작성해보세요!", color = Color.Gray)
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newCommentContent,
                onValueChange = onNewCommentChange,
                placeholder = { Text("댓글 달기...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onAddComment,
                enabled = newCommentContent.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "댓글 작성",
                    tint = if (newCommentContent.isNotBlank())
                        MaterialTheme.colorScheme.primary else Color.Gray
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
            comments = listOf(Comment.EMPTY.copy(
                authorNickName = "홍동길",
                authorImage = samplePathUrl,
                content = "댓글1111",
                timeAgo = "10년 전",
            )),
            newCommentContent = "입력",
            onNewCommentChange = {},
            onAddComment = {},
        )
    }
}

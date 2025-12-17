package com.sesac.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray400
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.domain.model.Comment
import com.sesac.common.ui_state.ResponseUiState

@Composable
fun CommonCommentSection(
    commentsState: ResponseUiState<List<Comment>>,
    currentUserId: Int,
    onPostComment: (String) -> Unit,
    onUpdateComment: (Int, String) -> Unit,
    onDeleteComment: (Int) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(top = paddingLarge)) {
        // Comment Input
        CommentInput(
            value = commentText,
            onValueChange = { commentText = it },
            onPostClick = {
                onPostComment(commentText)
                commentText = ""
            }
        )

        Spacer(modifier = Modifier.height(paddingLarge))

        // Comments List
        when (commentsState) {
            is ResponseUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is ResponseUiState.Success -> {
                val comments = commentsState.result
                if (comments.isEmpty()) {
                    Text(
                        text = "아직 댓글이 없습니다. 첫 댓글을 남겨보세요!",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Gray500
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(paddingMedium),
                        modifier = Modifier.heightIn(max = 400.dp) // Prevent infinite height in scrollable parent
                    ) {
                        items(comments) { comment ->
                            CommentItem(
                                comment = comment,
                                isAuthor = comment.authorId == currentUserId,
                                onUpdate = { updatedContent -> onUpdateComment(comment.id, updatedContent) },
                                onDelete = { onDeleteComment(comment.id) }
                            )
                        }
                    }
                }
            }
            is ResponseUiState.Error -> {
                Text(
                    text = "댓글을 불러오는데 실패했습니다: ${commentsState.message}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.error
                )
            }
            is ResponseUiState.Idle -> {
                // Do nothing
            }
        }
    }
}

@Composable
fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onPostClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(id = R.string.comment_placeholder_comment_write)) },
            shape = MaterialTheme.shapes.large
        )
        Spacer(modifier = Modifier.width(paddingSmall))
        IconButton(onClick = onPostClick, enabled = value.isNotBlank()) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = stringResource(id = R.string.comment_action_post),
                tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else Gray400
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isAuthor: Boolean,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newContent by remember(comment.content) { mutableStateOf(comment.content) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.comment_delete_confirm_title)) },
            text = { Text(stringResource(id = R.string.comment_delete_confirm_text)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text(stringResource(id = R.string.common_yes)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.common_no))
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(paddingSmall)
        ) {
            AsyncImage(
                model = comment.authorImage,
                contentDescription = "Author Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(paddingSmall))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(comment.authorNickName, fontWeight = FontWeight.Bold)
                    Text(comment.timeAgo ?: "방금 전", style = MaterialTheme.typography.bodySmall, color = Gray500)
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isEditing) {
                    Column {
                        OutlinedTextField(
                            value = newContent,
                            onValueChange = { newContent = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(paddingSmall))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                onUpdate(newContent)
                                isEditing = false
                            }) {
                                Text(stringResource(id = R.string.comment_action_save))
                            }
                            Spacer(modifier = Modifier.width(paddingSmall))
                            OutlinedButton(onClick = { isEditing = false }) {
                                Text(stringResource(id = R.string.comment_action_cancel))
                            }
                        }
                    }
                } else {
                    Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (isAuthor && !isEditing) {
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Comment Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.comment_action_edit)) },
                            onClick = {
                                isEditing = true
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.comment_action_delete)) },
                            onClick = {
                                showDeleteDialog = true
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
/*
comment: Comment,
    isAuthor: Boolean,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit
 */

@Preview
@Composable
fun CommentItemPreivew() {
    Android7HoursTheme {
        CommentItem(
            comment = Comment.EMPTY,
            isAuthor = true,
            onUpdate = { String -> },
            onDelete = {},
        )
    }
}
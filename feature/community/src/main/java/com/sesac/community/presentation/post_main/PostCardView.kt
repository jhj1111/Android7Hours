package com.sesac.community.presentation.post_main

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.getTimeAgo
import com.sesac.common.utils.sampleBannerImageUrl
import com.sesac.common.utils.sampleIconUmageUrl
import com.sesac.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PostCardView(
    post: Post,
    isMyPost: Boolean,
    onLikeToggle: (postId: Int) -> Unit,
    onBookmarkToggle: (postId: Int) -> Unit,
    onEdit: (post: Post) -> Unit,
    onDelete: (postId: Int) -> Unit,
    onCommentClick: (postId: Int) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingSmall),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(vertical = paddingMedium)) {
            // --- 1. Header ---
            PostHeader(
                author = post.authUserNickname ?: "사용자",
                authorImage = post.authUserProfileImageUrl,
                timeAgo = post.createdAt.getTimeAgo(context),
                isMyPost = isMyPost,
                onEdit = { onEdit(post) },
                onDelete = { onDelete(post.id) }
            )
            Spacer(modifier = Modifier.height(paddingMedium))

            // --- 2. Title and Content ---
            Column(modifier = Modifier.padding(horizontal = paddingLarge)) {
                Text(
                    text = post.title,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(paddingSmall))
                CommonExpandableText(text = post.content)
            }
            Spacer(modifier = Modifier.height(paddingMedium))

            // --- 3. Image ---
            post.image?.let { imageUrl ->
                var showHeart by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!post.isLiked) onLikeToggle(post.id)
                                    showHeart = true
                                    coroutineScope.launch {
                                        delay(800)
                                        showHeart = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Post image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showHeart,
                        enter = scaleIn(initialScale = 0.5f),
                        exit = scaleOut()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like Heart",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(paddingMedium))

            // --- 4. Actions ---
            PostActions(
                isLiked = post.isLiked,
                isBookmarked = post.isBookmarked,
                onLikeToggle = { onLikeToggle(post.id) },
                onCommentClick = { onCommentClick(post.id) },
                onBookmarkToggle = { onBookmarkToggle(post.id) }
            )
            Spacer(modifier = Modifier.height(paddingSmall))

            // --- 5. Status (Likes, Comments, Views) ---
            PostStatus(
                likes = post.likeCount,
                comments = post.commentCount,
                views = post.viewCount
            )
        }
    }
}

@Composable
fun PostHeader(
    author: String,
    authorImage: String?,
    timeAgo: String,
    isMyPost: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = authorImage,
            contentDescription = "Author image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(paddingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = author, style = Typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = timeAgo, style = Typography.bodySmall, color = TextSecondary)
        }
        if (isMyPost) {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("수정") }, onClick = {
                        onEdit()
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("삭제") }, onClick = {
                        onDelete()
                        menuExpanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun CommonExpandableText(text: String, minimizedMaxLines: Int = 2) {
    var isExpanded by remember { mutableStateOf(false) }
    val canExpand = text.lines().size > minimizedMaxLines || text.length > 100

    Column(modifier = Modifier.clickable(enabled = canExpand) { isExpanded = !isExpanded }) {
        Text(
            text = text,
            style = Typography.bodyMedium,
            maxLines = if (isExpanded || !canExpand) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (canExpand && !isExpanded) {
            Text(
                text = "더보기",
                style = Typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PostActions(
    isLiked: Boolean,
    isBookmarked: Boolean,
    onLikeToggle: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like, Comment, Share (Left-aligned)
        IconButton(onClick = onLikeToggle) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onCommentClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Comment",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Spacer to push bookmark to the end
        Spacer(modifier = Modifier.weight(1f))

        // Bookmark (Right-aligned)
        IconButton(onClick = onBookmarkToggle) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PostStatus(likes: Int, comments: Int, views: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        Text(
            text = "좋아요 ${likes}개",
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "댓글 ${comments}개",
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "조회수 ${views}회",
            style = Typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Preview
@Composable
fun PostCardViewPreview() {
    Android7HoursTheme {
        PostCardView(
            Post.EMPTY.copy(
                title = "제목",
                authUserProfileImageUrl = sampleIconUmageUrl,
                image = sampleBannerImageUrl,
                authUserNickname = "작성자",
                content = "글",
            ),
            isMyPost = true,
            onEdit = {},
            onDelete = {},
            onLikeToggle = {},
            onCommentClick = {},
            onBookmarkToggle = {},
        )
    }
}

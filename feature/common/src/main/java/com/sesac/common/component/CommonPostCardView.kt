package com.sesac.common.component

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.avatarSize
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.postImageExtremeLarge
import com.sesac.common.utils.getTimeAgo
import com.sesac.common.config.sampleBannerImageUrl
import com.sesac.common.config.sampleIconImageUrl
import com.sesac.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommonPostCardView(
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
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = paddingSmall),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.Companion.padding(vertical = paddingMedium)) {
            // --- 1. Header ---
            PostHeader(
                author = post.authUserNickname ?: "author",
                authorImage = post.authUserProfileImageUrl,
                timeAgo = post.createdAt.getTimeAgo(context),
                isMyPost = isMyPost,
                onEdit = { onEdit(post) },
                onDelete = { onDelete(post.id) }
            )
            Spacer(modifier = Modifier.Companion.height(paddingMedium))

            // --- 2. Title and Content ---
            Column(modifier = Modifier.Companion.padding(horizontal = paddingLarge)) {
                Text(
                    text = post.title,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Companion.Bold
                )
                Spacer(modifier = Modifier.Companion.height(paddingSmall))
                CommonExpandableText(text = post.content)
            }
            Spacer(modifier = Modifier.Companion.height(paddingMedium))

            // --- 3. Image ---
            post.image?.let { imageUrl ->
                var showHeart by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()

                Box(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(postImageExtremeLarge)
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
                    contentAlignment = Alignment.Companion.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Post image",
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentScale = ContentScale.Companion.Crop
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showHeart,
                        enter = scaleIn(initialScale = 0.5f),
                        exit = scaleOut()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like Heart",
                            tint = White.copy(alpha = 0.8f),
//                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.Companion.height(paddingMedium))

            // --- 4. Actions ---
            PostActions(
                isLiked = post.isLiked,
                isBookmarked = post.isBookmarked,
                onLikeToggle = { onLikeToggle(post.id) },
                onCommentClick = { onCommentClick(post.id) },
                onBookmarkToggle = { onBookmarkToggle(post.id) }
            )
            Spacer(modifier = Modifier.Companion.height(paddingSmall))

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
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = paddingLarge),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        AsyncImage(
            model = authorImage,
            contentDescription = "Author image",
            modifier = Modifier.Companion
                .size(avatarSize)
                .clip(CircleShape),
            contentScale = ContentScale.Companion.Crop
        )
        Spacer(modifier = Modifier.Companion.width(paddingMedium))
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = author,
                style = Typography.titleSmall,
                fontWeight = FontWeight.Companion.Bold
            )
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
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.common_action_edit)) },
                        onClick = {
                            onEdit()
                            menuExpanded = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.common_action_delete)) },
                        onClick = {
                            onDelete()
                            menuExpanded = false
                        })
                }
            }
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
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = paddingSmall),
        verticalAlignment = Alignment.Companion.CenterVertically
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
        Spacer(modifier = Modifier.Companion.weight(1f))

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
    val countsText = stringResource(R.string.common_counts)

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = paddingLarge),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        Text(
            text = "${stringResource(R.string.common_like)} ${likes}$countsText",
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Companion.Bold
        )
        Text(
            text = "${stringResource(R.string.common_comment)} ${comments}$countsText",
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Companion.Normal
        )
        Text(
            text = "${stringResource(R.string.common_view_counts)} ${views}${stringResource(R.string.common_counts_views)}",
            style = Typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Preview
@Composable
fun PostCardViewPreview() {
    Android7HoursTheme {
        CommonPostCardView(
            Post.Companion.EMPTY.copy(
                title = "제목",
                authUserProfileImageUrl = sampleIconImageUrl,
                image = sampleBannerImageUrl,
                authUserNickname = "작성자",
                content = "글".repeat(200),
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
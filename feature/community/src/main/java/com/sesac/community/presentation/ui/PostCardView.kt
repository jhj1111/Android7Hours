package com.sesac.community.presentation.ui

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
                authUserProfileImageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMSEhUTExIVFRUXGBUVGBcVFxgXFxcYFRUXFxUXFRUYHSggGBolGxUVITEhJSkrLi4uFx8zODMtNygtLisBCgoKDg0OFxAQFysdFR0rKystLSsrLS0tLSsrLS0tLSsrLS0tKy0rLTctLS0tKystLTcrKysrKysrKysrKysrK//AABEIAOAA4QMBIgACEQEDEQH/xAAcAAACAgMBAQAAAAAAAAAAAAADBAIFAQYHAAj/xAA+EAABAwIDBQUFBwMEAgMAAAABAAIRAyEEEjEFQVFhcQaBkaGxEyLB0fAHFDJCUuHxFWKSI3KCojPCFhdz/8QAGQEBAQEBAQEAAAAAAAAAAAAAAAECAwQF/8QAHhEBAQEBAAMAAwEAAAAAAAAAAAERAgMSITFBURP/2gAMAwEAAhEDEQA/AOeFSYRySRrqbXSssjShgrDRIKwCgO1vJZFBKPrFDfinTqinfu29Fp0RoSqs4h3FZZXPFXCrtmECy7BhVmHxB0lNOrmNUwCOEAKJ92bCQr4pLjFniVUXLcO1S9gyNfNU/tTxXjUMapVXFOmyDol3inxCq6TzBugVHlRF2x7BvCyarJlUGYqUqtRsp7QuaIbxVfidsVHauKrGobjdNMOnEcSp0XNJvySCk0pqOnbHrt9mICsW1gte2B/42q1aVUWBeqLtXh5paq1pOSPar/woNIw1Jo1TFcsLYCq6jzKM0rDSfs15GyryMgupBZpmELMVKm9VcEzrAKjmQnOQSqhLPBlGzLDkACsgwsuCGSqGG1FI4lKZlEuQSrOQ5Rn0wWyEBDBaL9xR9yTCcGndP14KUDonVAcbpijTMExayYOyqhaH5TEA+JI+CCvWYVn/AESp7I1SIABI5x+KOkIrdjVMpGU5gWgjm9sgddPFNFWxRc1bieyrw5wgw3M6Y1N4A8AqzEdnarSPdMW5avDR3mdEtVQlq80XV87s5VkgNuJLp/Lob9zgnqPY6vUNNrGE5swmLe7En/sFDFj2dP8ApCFbN1A36LYOyv2a1GMBq1MvuaD9RMnygLbMN2ZwdEhzvec28k2tKas51z/D4R7oytJm1rom1+z+IrsDG0nk74Gn1ddH/qTGACmxrQNBYLw24/gD00U/0jc8NcWb9le0CZFKJiznARNzPT61R3/ZfjxH+mDxOaAB01K7O3tBxb9eKNS29TP4rKe8/peLHGf/AK4xfD1+S8u2/wBUpfq9F5a9oz618r4gJdrrqWKxI4qvqVuCnjlxKsJQ3FKMqkpmk0ncT3a9FtIyslMjZ1UiRTeRxAJ9EcbExB0ov/xKyqreENwV1/Q6/uzReA4xJaYaZgTyVlS7DYtzh/pGJAJ4SLqjUSFAUiYjfpz5LpDPssxF5N5OWOG6V7CfZ3VDXAtIdAqMnQPY64B4O+Supjn+ABNosd6nQwJc8t5Ejuv8CunDsNlqyGHIcro4EuJeOkQnNjdi8lVhdGaZPDQ6LOrjnNDs5Ue33WkkGox3JzS0tvzErZsH2Ge8N192x7yD/wCx8F03Z2yKdKmIF4vN7/G1ldUcIALCLDyACVcc7p9iKVNrWmDckjiS0N9JWwbM7NU3A0yAW+535Vd4rCNzX1Fx1Ij4FN7OhoHUeQU1cJ1+xdFwYA0ZWNLQ3d7xLifElPYbsnRaSctyZPONJVyMUBvRc+irKtGxKU/hWP8A4/RkOyCRp3AgW7z4q0lSBQUNPstRAcCPxzm7z+w8E62jTogZWgQmcXiA0arV9q7Svlbr6LPXWOnj4vVN43axJgHzCRqAn82vegYVrW/pLuJ/aJTLSx3AHdlNuhG4rluvTk5/BTI0AmCeh84WSQLiDp4HiNQpV5FxB1IIseYPHwVbUqE3Fj9aws2LDtSoIkfXcq/EVkCpUI6pevVlc616j/fBzXkisp9Mji9bAPGqWbTJMb1ful5uVvf2e9gG13itWBbSbu0zeBXuj50U3YH7PK2LcHublpby74Rr1ldo2R2JwWGFqbSd+a/lojvxzKTRTotDWiwACrKuNJu4+aze3Tnx62RooM0awdAFk4ulwHgFrPtt58FNrydGa9ST3LPu36RsmemdGg9wRaRYPyjwWtisW2n9lZYDEZtSCet+8KzrU65W5pg7khi6Y08/gjmoQEpWqTqqwr6tQDM3eAUrgTJzHWEPEAmrPL0TmGw1gI3eigzTp5nRuFvL+FZu91vco4WhHes410NPRUUe1cUM7INiD8x6HxWG4vTx7wqrH0/eCJUdI6LP7VZU8Y4kXNi0+s/BbNRrS0dy03Zr5cfFbLQqWV0sWXtlA4kDkq+pXSlbE2k8f4sqkg2NxJcYaFru1QWkAa7zEq7oOv8AXDQJDGtDzBH79FmzXbx3FBTqDUv6zp0unfYlwBY5rjpzHI8Vl5ptJBLgAJhrXE8SS4C45Japj8MD7rmtdF3Fj5PfET3rGSOu+xqriXtaHO7yZsd4PEcCgurhx/S7du8eKRxO2AN4c0i9rEf7bx5JOrimzlEwQC06xPArFrU5p3GP14pAVZUPvGYwfHmpUGGZXOtiwvI8LCHxyCocu9X3ZTt1Vwhyl7nUiCMpMgHiFrWIaTYINOhcL34+bHc8Pt8PpB8AOcJAnQa3UMFjC90kzpYfHhK5XhNrOptLdZ47ua2rYG2clFz3fu4/XoudjpOq6HSxTRc3Wa2PJkC0rTtjY11eo97py07N4TGvVXntDHMrFjpz9Wjakb5VtsrMNwieKqdkUCddPFbLhqAaLfFa5idUd7kvV5IriogLblSlPCS6d2ism0lGk1HAUHiFX7WZmYYsVaQk67dQg1d9AkeHkg12ENNuPgthOFF0rUw26FMNVOy8MW3k3v4yYV+x0DuSLmhpJnkiPrSNdUUWrVuBxS2Iqb+Fx1NkE1JjiD81N0E34g35TCGsVquQXN+HOOXRKV6jWNzOsSItqS46ADnvU6wL6h3AD1N/+qUqNzFzt0QBuAbx779yNxq23KZkuzkzJgPJMGwkCyQwRuG8s3nb0Wx43IPdguc67iLCBFh3T3wgvwYEvMAuywABaRb3hrZc+o78d5FFtP3Ty0+feh0K0jorPbGGDWFziI3A67/3VRg/f0EN3HiuVjvz1sP0HhpkzuVzg6oeJiB04KqoYeSeUAc9JV5hcIYA8lMY7o+anw9fmsov3dvALyOXs4A6qeKwKx4pp+F4oZw4Xt14wnVSn8JjTly7kmaIRWiEHSNk12ik1gcGgyTBuTvtvW07Oe0M91p6uM98LjFPGOlv9un8Lp3YvHmtSOeDltfXyhYsdOb9btg9oge5q7pryA0VmzGSLiFRYamxwENHifQqzw1SbEEEcb+aRafYjNQ6LEeFphKmEWmo4du5TqCCFKibkF7URpsov4qAZCTrNurCoEpXbZVVFjXa9Uq2pu7viE9jaMmByPmh/cyb8Y8rIsAoXJPTz/jzRqYumKGDIAteRZF9h9RZMCJbrzSjGDQiwMdTqVa+xse6EAUgJ74+ay1KoqmFMtMACSTG++k703SwBe6SMoBmXcTv52TrqYMSLBTpMcTJtwHxPBRqVQdq8IwNhrXOOjQGzpqTwC1TCmXFp1EaFdVfs9rmEO3i+9aq/s2G1ZYTGv4Y+JWeo68d/MA2fhN5HRWtJoR6eDIECymKQXPGeqh3LyJkXkY18/1HKBRmslE9mIXqecmolFqBBlFSYF0H7O8W8Es9wN4knNPhC581dH+zPY+Z+dzpH9paekw71CLzXS8EwmJg84PwCtadKUzhcE0CzR4QmqWHASRrS7KMKQpJlxQXuVRKiyFKoFim6yhWes0EaEJ2qyKtkvUqb1nUGqFJ4l1io1cYOKWdigd6rUeZRDrndCcNPRI0a0ST19T8ESjic1+nmtKdAUXU54obq9vT90tWx+UXgm+8D1TUNvogiN+5V9WiCQ1t+J4nkg09rCoY4p/AAbsu9T4rDcDAi3Pf/CkMNHBOEDjPchOvvUTQ2hSgIJfCE6tuWFiGIZvCQeE896VqALNi6DlXlnvXlnF1wGloiApdr4WDieC9Lg9WGqVC9VqkqIKKe2fTzOC7p9nuzYaCQ0W/SAuHbLrBj2kyOYN/Ueq+gOw2OD6Yh+aw1N+8X9VSN2Y2BCFXq5Vk1wlsS6UEa1TiQlamMA1K1XtX2kbhmkucbbmtzO+S5vW+073jFN5E7yAVLXWc/HZ6+040MhTw+0cw5rnnYvtOzHOc0S1zQDld+YcR4Ld9n4e/1wWaliyZipSO0MbA1U6lIzAVLtrgspFfjtpGZCTO1HrP3XMq3aw9nTJm6Y1h3+vEWJQXdraYMGq0Hm4Bc9x2ycbiCXNBIJ/C06dUCl2FxzzeiQOJj5rUK6e/tCInNLT+h2b01TmGqvrFpDgG8CNeMzcFahsHsA+j7z3GeAdA8itzwVLINCe6fVSi1p4dsaDwhMUqmVIHG846G/ehDGR+YrOovPv5A3lCdjSd6qXYqd5KE5yl6Fw7FjRQ9qdyq21SmqNXmpqnM54qFRyi5070NwsYU0BzFeSWR/6x/isqjiFUJcpiulXFd3J5EYEJhRWFAakLi4HXTvXW/s82vRZla6uwvNoDXDuGZcglbB2TxOWq2JmRAZTa57uWd9mhCPompWUzWtzVVhquZjTcWGpBvzhNtqWiJRVPtjZdOtZ7dfRVFP7N8A73ntc7fcwFulLCk8O9ZxGyS4figcAmNezTW9nMPRqtGFohhEyRw6rcdktDhZump5lZ2ZsjISTv+CssPQDQQ3SSUxb1parQHDcq3F7Oa65CvKzg0KsxWKbv0SkaxjMAQTl8Ctc2ps59SReN/wBeK3erXY424IjcG0sBHlw+aYutX2bQNNuUCR4FWlBr3/lI5q5w+BaDJb9dFanENY33QFGda4MNlub83T8AlMXWaBAifEeYVxj9oTuAPP5KhxFbN+Yd+byss0hCrB19EIOHBZrE70By5tGg9SlJNcZTTCsgjU1RCWYzmmaaBmVlzTCiCiSgV9i/9S8msyyro+d65slZVg5komGwMlehxV9OmVLKrnEYMAaqvrgAooIanNnYv2TswAJ3Tp3pNzkXDMk+d7COJPBB2HsZt41GBr3TziL8BqSFurcREaeC4n2Zx7Q9rQ4mLZswa3oLE9+q65h8QDTBmTbQQB0lFbLhqoaAfgj/AHvQALVGbYIs6wFk7s3G+0qAA+aGNpZolaVSCVNla8LWtp7bFI5XAtJJMkeautc87cWWJxOYlvr3qo2pUgEkzy5fVlU1+0LRJLhMDT0C17H9oy7dbeSeuqxeo7zxVf4M5j+KBv8AkrzBe7aZGsLnOzMY975YIbOp39Ft+HxTmxaT3JK59xsz6o3fXVJYzFR+Yd3zSdHGkktLZOvOEricKagc5sQOOitrCOKx7TYAm9yq6pi+neZ/hQdhSD7z2jv/AGQa2HnR9M95+S51UcRXH8JZlTnZSdhzOrP8m+kqPsSDu/yb81lTNNyPTckGG6YplZDrCUywpSk5MtKloZaUdqVYjsKgKvLy8mjgLE3Rsk2G6ZYYXqrjRMS6yqq4uncRWtASccbqkQpTOsDenaeOrRFN9Uj+0u84QaVQjQNH/EHzdJTDagd+Jzx1JcPBFOYXG4hhl1ZwiCQapJ6ZWknyXRezm2g9ozVXOOkC279Wp8lzAUWyAKjeVnye4NK2LY1MUWlzq2QTBge+Z3ATI7wOaLHQq+II3nxd81fdlqzQC57nZp0J0HILScJ2ipQG0m30l13eA0VzsfabhUm7gbENHDoFGtbs6u0WGaVTdp8KK9Iyffbdp7rg8kSptgMiKdz0+CqsdtMkSBBOs7vJW1Zc+ufY2rcjTlwVYKBq1b/hAFt3grrtHSDnFwsTrwVZs2pA4u0XGx6b5NjYMCAwWaE8MXUn3frwVJSxDpiJ6GyO2u+LWVcLWzYba1Vsl8zybJ8ZCl/V68S1pj9RCoKGIrEWqC/5THjfVGBqtklw/wBzbCfn1V1lZV3uqAklsndIB81WVqJbqCpuxU/j97mLH5eIUHve2XNcXM38uTmnRYUt7V24nuKyKnEDwjzEFEbVa+MzYPEaeG7xWTQjQ9x+vhHNEZYW7pHn+/qjtvpf64JccwOunpZEaBzHn8llTbHpmm5I0+oPj8k3THNZpIbaUxTKWYEdjlFFzLyxmWEVwpCc5GKrqz17HnNOeIQs0pVrkakijgJinTHM8h8SgNTVOyiaPTaRbT+1lp/3O1jlKZw2CNQgXJ0DRoAL2G4C90LB0y6YgNH4nH8I7+O+PTVWI2jTY05fw6EyM1UjXX8o8BzOjNTVnhsHRojM995/L+Ym8ZuHJuusgJyjtp77fgpj8rbdAY1PpC0+ttQ1HC0nQXs0b/iSdTEqVDaeeo1jXZabZcT+kADO93cD5BX0pK6XhtoPLGl1psNxshV8S4iJj5LVcBtw1XT+FujBqQ1u8nuN+JKvKOMa7f37v3UsdJVTj6uYwFVsoubpK2x2EBS9TChYsdJ0p6eJIcZbHRNsxANwdUw/DDdwhDGHG8KalojYKfw9eObdIOnMJOk0CCE1Sico33b1GnxHepoliKYBzN0J37v7XclEW95luI16g8QoCrv3GxHwUX+6dZB94HfyPwKIKcrr2HHl+3PmsCqW28JgjqNyVde7TfeOPRZY48JHAnTooGW17/X8FM03A7vD5FKU6XC/r3o7WrKnmsR2NS9CU01wUqjMUwhAogUomvKMryg4vCA/Dgm6YUSV6nnCGx8wkFA+5uamw88SsGsrpodOid6bbAiRbcABLuk6C2qWBUwQTeY3x6ckn34D18SS0kn3dABYOi8NG5s6nfrwipxVYkyf2A3ADgm8TUJPTcNw4BI1HrvJkRkPhjnHUnINLCJcfAgf8imhFOgAfxVYc87xSbdrP+Tsru7klW0cxpNNxBcf9slzv+o9FnbFRzqhb+k5bWkz70csxdA5pR6jjnxJMAwxrRoGiC6BpuAnmU/s/bTgRmMWkkmTqdPkqSsYIA3CB8T4kqLHb+CzY1HV9j44PaL31M3PerJxC59sDH02kS9zjaBcgbgAP2W+UKktE6wuPUdJdYNMblAtR8qG9qxil3rOewO8GPiPipPZZRa33T1afIrI9Uu5w0DoI5ZhmHrCE10ti9nER1HzafFHc2zTy9CR8AgVLOn9V+8a+vmggabt1kWi12hseHHmERt90Ivs3WhAWgw705THHx+aTo1T+YJhlbkVlTYCm1BpvlHaiiNU5QpUwpiJ5l5QlZUxXFalbghCoSUHOotqc16XmNhriptoOStPEEJ1tUlBH2bpXqrxcDdHefkmHHKIm59EiWzPBduOf2BPMzck/XxUC1TcyJHTz/lYzD6En63roGcE3354MZ5lgMdxjvVaDckm9/E71YUKhh3/AObj/jPy8lWOqEqCBYhlSc9DJWaqx2RmDpDgzm75aldJ2e6wJ4bjK5ds+Mwv3DU9JsuhbN0aGiLCRvHU8Vz6jXLZWCVh7VjCaJhzVybJ1GrHs7JipTUSxZsUHLYDqPj8UGpRkR3plrV6m2D1UCWDqkWdeLKxpwgU6fv8j8E2ynCKkxqI1vJRadyK1ZEmtRAhZlnMUBQpNMIMlTMoCZgvKC8g/9k=",
                image = "data:image/jpeg;base64,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",
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

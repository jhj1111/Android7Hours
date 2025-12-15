package com.sesac.community.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.White
import com.sesac.domain.model.Post
import com.sesac.domain.type.PostType
import com.sesac.domain.type.toKoreanString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEditorDialogView(
    categories: List<PostType>,
    initialPost: Post? = null, // null이면 새 글, 아니면 수정
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, postType: PostType, imageUri: Uri?) -> Unit,
) {
    val isEditMode = initialPost != null
    val dialogTitle = if (isEditMode) "게시글 수정" else "게시글 작성"
    val buttonText = if (isEditMode) "수정" else "작성"

    var title by remember { mutableStateOf(initialPost?.title ?: "") }
    var content by remember { mutableStateOf(initialPost?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(initialPost?.postType ?: PostType.REVIEW) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf(initialPost?.image) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            existingImageUrl = null // 새 이미지를 선택하면 기존 이미지는 무시
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // 1. 카테고리 선택
                Text("카테고리", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.toKoreanString()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 2. 제목 입력
                Text("제목", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("제목을 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 3. 내용 입력
                Text("내용", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("무슨 생각을 하고 계신가요?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 10
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 4. 이미지 선택
                Text("이미지", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val currentImage = imageUri ?: existingImageUrl
                    if (currentImage == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("사진 추가", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        AsyncImage(
                            model = currentImage,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                imageUri = null
                                existingImageUrl = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Image", tint = White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, content, selectedCategory, imageUri)
                },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text(buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Preview
@Composable
fun PostEditorDialogViewPreview() {
    Android7HoursTheme {
        PostEditorDialogView(
            categories = listOf(PostType.REVIEW, PostType.INFO),
            initialPost = Post.EMPTY.copy(
                image = "data:image/jpeg;base64,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"
            ),
            onDismiss = {},
            onSave = { _, _, _, _ -> },
        )
    }
}
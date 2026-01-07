package com.sesac.community.presentation.post_main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.cardHeight
import com.sesac.common.ui.theme.cardHeightSmall
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.sampleBannerImageUrl
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
    val dialogTitle = if (isEditMode) stringResource(R.string.community_post_edit) else stringResource(R.string.community_post_create)
    val buttonText = if (isEditMode) stringResource(R.string.common_action_edit) else stringResource(R.string.common_create)

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
                Text(stringResource(R.string.common_categroy), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(paddingSmall))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(paddingSmall)) {
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
                Spacer(modifier = Modifier.height(paddingLarge))

                // 2. 제목 입력
                Text(stringResource(R.string.common_title), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(paddingSmall))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text(stringResource(R.string.community_placeholder_input_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(paddingLarge))

                // 3. 내용 입력
                Text(stringResource(R.string.common_content), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(paddingSmall))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text(stringResource(R.string.community_placeholder_post_content_empty)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = cardHeightSmall),
                    maxLines = 10
                )
                Spacer(modifier = Modifier.height(paddingLarge))

                // 4. 이미지 선택
                Text(stringResource(R.string.common_image), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(paddingSmall))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                        .border(
                            width = borderMicro,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.small
                        )
                        .clip(MaterialTheme.shapes.small)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val currentImage = imageUri ?: existingImageUrl
                    if (currentImage == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(stringResource(R.string.common_add_image), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
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
                                .padding(paddingMicro)
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
                Text(stringResource(R.string.common_action_cancel))
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
                content = "dddd",
                image = sampleBannerImageUrl
            ),
            onDismiss = {},
            onSave = { _, _, _, _ -> },
        )
    }
}
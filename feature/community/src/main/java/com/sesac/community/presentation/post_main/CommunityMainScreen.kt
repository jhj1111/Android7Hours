package com.sesac.community.presentation.post_main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.sesac.common.component.CommonFilterTabs
import com.sesac.common.ui.theme.Gray400
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.White
import com.sesac.common.component.CommonCommentSheetContent
import com.sesac.community.presentation.CommunityViewModel
import com.sesac.domain.model.Post
import com.sesac.domain.type.PostType
import com.sesac.domain.result.AuthUiState
import com.sesac.domain.result.ResponseUiState
import java.util.Date
import com.sesac.common.R as cR

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityMainScreen(
    nav2LoginScreen: () -> Unit,
    uiState: AuthUiState,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val token = uiState.token
    val lifecycleOwner = LocalLifecycleOwner.current

    // StateFlows
    val isCommentsOpen by viewModel.isCommentsOpen.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val newCommentContent by viewModel.newCommentContent.collectAsStateWithLifecycle()
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedPost by viewModel.selectedPostForComments.collectAsStateWithLifecycle()
    val filteredPosts by viewModel.filteredPosts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()

    val isCreateDialogOpen by viewModel.isCreateDialogOpen.collectAsStateWithLifecycle()
    val editingPost by viewModel.editingPost.collectAsStateWithLifecycle()
    val isEditDialogOpen by viewModel.isEditDialogOpen.collectAsStateWithLifecycle()

    val postDetail by viewModel.post.collectAsStateWithLifecycle()
    val createPostState by viewModel.createPostState.collectAsStateWithLifecycle()
    val updatePostState by viewModel.updatePostState.collectAsStateWithLifecycle()
    val deletePostState by viewModel.deletePostState.collectAsStateWithLifecycle()

    val postEditorCategories = listOf(PostType.REVIEW, PostType.INFO)

    // 검색 상태
    var isSearchOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.isCreateDialogOpen.value = true },
                containerColor = Primary,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "새 게시글 작성")
            }
        },
        topBar = {
            Column {
                AnimatedContent(
                    targetState = isSearchOpen,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "TopBar Animation"
                ) { searchActive ->
                    if (searchActive) {
                        TopAppBar(
                            title = {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChange(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    placeholder = { Text("검색...") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = {
                                        focusManager.clearFocus()
                                    }),
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                Icon(Icons.Default.Close, contentDescription = "검색어 지우기")
                                            }
                                        }
                                    }
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    isSearchOpen = false
                                    viewModel.onSearchQueryChange("")
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "검색 닫기")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//                                navigationIconContentColor = Color.Unspecified,
//                                titleContentColor = Color.Unspecified,
//                                actionIconContentColor = Color.Unspecified
                            )
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        CenterAlignedTopAppBar(
                            title = { Text("커뮤니티", fontWeight = FontWeight.Bold) },
                            actions = {
                                IconButton(onClick = { isSearchOpen = true }) {
                                    Icon(Icons.Default.Search, contentDescription = "검색 열기")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//                                navigationIconContentColor = Color.Unspecified,
//                                titleContentColor = Color.Unspecified,
//                                actionIconContentColor = Color.Unspecified
                            )
                        )
                    }
                }
                CommonFilterTabs(
                    filterOptions = listOf("전체", "산책후기", "정보공유"),
                    selectedFilter = activeFilter,
                    onFilterSelected = { viewModel.onFilterChange(it) }
                )
            }
        }
    ) { paddingValues ->

        LaunchedEffect(lifecycleOwner) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.closeComments()
            }
        }

        LaunchedEffect(uiState, Unit) {
            viewModel.getPostList(uiState.token)
        }

        // region LaunchedEffect for CRUD
        LaunchedEffect(createPostState) {
            when (val result = createPostState) {
                is ResponseUiState.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.getPostList(token)
                }
                is ResponseUiState.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        // 게시글 수정 결과 처리
        LaunchedEffect(updatePostState) {
            when (val result = updatePostState) {
                is ResponseUiState.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.getPostList(token)
                    viewModel.editingPost.value = null
                }
                is ResponseUiState.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        // 게시글 삭제 결과 처리
        LaunchedEffect(deletePostState) {
            when (val result = deletePostState) {
                is ResponseUiState.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.getPostList(token)
                }
                is ResponseUiState.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        // 게시글 상세 로드 후 처리 (수정용)
        LaunchedEffect(postDetail) {
            if (postDetail is ResponseUiState.Success && editingPost != null) {
                viewModel.editingPost.value = (postDetail as ResponseUiState.Success<Post>).result
            }
        }
        // endregion

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (filteredPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(cR.string.community_placeholder_post_empty), color = Gray400)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredPosts, key = { it.id }) { post ->
                        PostCardView(
                            post = post,
                            isMyPost = post.userId == uiState.user?.id,
                            onLikeToggle = { viewModel.toggleLike(token, post.id) },
                            onBookmarkToggle = { viewModel.toggleBookmark(token, post.id) },
                            onEdit = { viewModel.onStartEditing(post) },
                            onDelete = { token?.let { viewModel.deletePost(it, post.id) } },
                            onCommentClick = {
                                val targetPost = filteredPosts.find { it.id == post.id }
                                targetPost?.let { viewModel.openComments(it) }
                            }
                        )
                    }
                }
            }
        }

        if (isCreateDialogOpen) {
            PostEditorDialogView(
                categories = postEditorCategories,
                onDismiss = { viewModel.isCreateDialogOpen.value = false },
                onSave = { title, content, postType, imageUri ->
                    val newPost = Post(
                        id = -1, title = title, content = content, image = null,
                        postType = postType, userId = uiState.user?.id ?: -1,
                        authUserNickname = uiState.user?.nickname ?: "", authUserProfileImageUrl = null,
                        likeCount = 0, commentCount = 0, bookmarkCount = 0, viewCount = 0,
                        isLiked = false, isBookmarked = false, comments = null,
                        createdAt = Date(), updatedAt = Date()
                    )
                    viewModel.createPost(context, token, newPost, imageUri)
                    viewModel.isCreateDialogOpen.value = false
                }
            )
        }

        if (isEditDialogOpen && editingPost != null) {
            PostEditorDialogView(
                categories = postEditorCategories,
                initialPost = editingPost,
                onDismiss = { viewModel.editingPost.value = null },
                onSave = { title, content, postType, imageUri ->
                    editingPost?.let { post ->
                        val updatedPost = post.copy(
                            title = title, content = content, postType = postType, updatedAt = Date(),
                            image = if (imageUri == null) post.image else null
                        )
                        token?.let { viewModel.updatePost(context, it, post.id, updatedPost, imageUri) }
                    }
                }
            )
        }
    }

    if (selectedPost != null && isCommentsOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeComments() },
            sheetState = modalSheetState
        ) {
            selectedPost?.let { post ->
                CommonCommentSheetContent(
                    comments = comments,
                    newCommentContent = newCommentContent,
                    onNewCommentChange = { viewModel.onNewCommentChange(it) },
                    onAddComment = { viewModel.addComment(token, post.id) }
                )
            }
        }
    }
}
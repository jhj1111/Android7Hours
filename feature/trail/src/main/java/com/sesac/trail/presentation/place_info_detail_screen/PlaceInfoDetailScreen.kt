package com.sesac.trail.presentation.place_info_detail_screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sesac.common.component.CommonCommentSection
import com.sesac.common.ui.theme.PaddingSection
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.config.sampleLocationImageUrl
import com.sesac.domain.model.Place
import com.sesac.domain.type.CommentType
import com.sesac.trail.presentation.PlaceViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceInfoDetailScreen(
    uiState: AuthUiState,
    place: Place,
    onBackClick: () -> Unit = {},
    placeViewModel: PlaceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isFavorite by remember(place.isBookmarked) { mutableStateOf(place.isBookmarked) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // ViewModel에서 댓글 상태 가져오기
    val commentsState by placeViewModel.commentsState.collectAsStateWithLifecycle()

    // 화면 진입 시 댓글 로드
    LaunchedEffect(place.id) {
        placeViewModel.loadPlaceComments(place.id)
    }

    // 즐겨찾기 핸들러
    val handleFavorite: () -> Unit = {
        isFavorite = !isFavorite
        scope.launch {
            val message = if (isFavorite) "즐겨찾기에 추가되었습니다" else "즐겨찾기에서 제거되었습니다"
            snackbarHostState.showSnackbar(message)
        }
    }

    // 전화걸기 핸들러
    val handleCall = {
        val phoneNumber = "02-123-4567" // 실제 데이터 연결 필요
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
        context.startActivity(intent)
    }

    // 주소 복사 핸들러
    val handleCopyAddress: () -> Unit = {
        place.address?.let { address ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("address", address)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
        Unit
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // 1. 상단 이미지 헤더
            item {
                PlaceImageHeader(
                    placeName = place.title,
                    isFavorite = isFavorite,
                    onFavoriteClick = handleFavorite,
                    imageUrl = place.imageUrl
                        ?: sampleLocationImageUrl
                )
            }

            // 2. 병원 기본 정보 & 상세 정보
            item {
                PlaceInfoSection(
                    place = place,
                    onCall = handleCall,
                    onCopyAddress = handleCopyAddress
                )
            }

            // 3. 댓글 로직 시작 (ViewModel 연결)
            item {
                Column(modifier = Modifier.padding(horizontal = paddingLarge)) {
                    Text(
                        text = "방문자 리뷰",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = paddingSmall)
                    )

                    CommonCommentSection(
                        commentsState = commentsState,
                        currentUserId = uiState.user?.id ?: -1,
                        onPostComment = { content ->
                            placeViewModel.postPlaceComment(uiState, place.id, content, CommentType.PATH)
                        },
                        onUpdateComment = { commentId, content ->
                            placeViewModel.updatePlaceComment(uiState, place.id, commentId, content, CommentType.PATH)
                        },
                        onDeleteComment = { commentId ->
                            placeViewModel.deletePlaceComment(uiState, place.id, commentId, CommentType.PATH)
                        }
                    )

                    Spacer(modifier = Modifier.height(PaddingSection))
                }
            }
        }
    }
}


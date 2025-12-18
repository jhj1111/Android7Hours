package com.sesac.trail.presentation.trail_main_screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import android.content.Context
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun MemoDialog(
    show: Boolean,
    memoText: String,
    onTextChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("메모 추가") },
        text = {
            TextField(
                value = memoText,
                onValueChange = onTextChange,
                placeholder = { Text("메모를 입력하세요") }
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("저장")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("취소")
            }
        }
    )
}

fun addMemoMarker(
    context: Context,
    naverMap: NaverMap,
    coord: LatLng,
    memo: String,
    markers: MutableList<Marker>,
    infoWindowStates: MutableMap<Marker, Boolean>
) {
    val marker = Marker().apply {
        position = coord
        map = naverMap
        iconTintColor = 0xFFFF0000.toInt()
    }

    val infoWindow = InfoWindow().apply {
        adapter = object : InfoWindow.DefaultTextAdapter(context) {
            override fun getText(infoWindow: InfoWindow): CharSequence = memo
        }
    }

    marker.setOnClickListener {
        val isOpen = infoWindowStates[marker] ?: false
        if (isOpen) {
            infoWindow.close()
            infoWindowStates[marker] = false
        } else {
            infoWindow.open(marker)
            infoWindowStates[marker] = true
        }
        true
    }

    markers.add(marker)
}

@Preview(showBackground = true)
@Composable
fun MemoDialogPreview() {
    MemoDialog(
        show = true,
        memoText = "프리뷰 메모입니다",
        onTextChange = {},
        onCancel = {},
        onConfirm = {}
    )
}
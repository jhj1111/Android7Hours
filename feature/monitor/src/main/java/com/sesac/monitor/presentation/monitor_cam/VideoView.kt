package com.sesac.monitor.presentation.monitor_cam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun VideoView(
    videoTrack: VideoTrack?,
    eglBase: EglBase?,
    modifier: Modifier = Modifier.Companion,
    isMirror: Boolean = false // 좌우 반전 여부 파라미터 추가
) {
    val eglContext = eglBase?.eglBaseContext
    val context = LocalContext.current
    // remember를 사용하여 SurfaceViewRenderer 인스턴스를 리컴포지션 간에도 유지
    val surfaceViewRenderer = remember { SurfaceViewRenderer(context) }

    // videoTrack sink의 라이프사이클을 DisposableEffect로 관리
    DisposableEffect(surfaceViewRenderer, videoTrack, eglContext, isMirror) {
        surfaceViewRenderer.init(eglContext, null)
        surfaceViewRenderer.setEnableHardwareScaler(true)
        surfaceViewRenderer.setMirror(isMirror)
        // 영상의 종횡비를 유지하면서 화면에 맞도록 스케일링 설정
        surfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        // 뷰를 다른 UI 요소 위에 올리기 위해 Z-order 설정
        surfaceViewRenderer.setZOrderMediaOverlay(true)


        videoTrack?.addSink(surfaceViewRenderer)

        onDispose {
            videoTrack?.removeSink(surfaceViewRenderer)
            surfaceViewRenderer.release() // Renderer 리소스 해제
        }
    }

    AndroidView(
        factory = {
            // factory는 기억된 인스턴스를 반환하기만 하면 됩니다.
            surfaceViewRenderer
        },
        // update 람다는 DisposableEffect가 sink 관리를 처리하므로 필요 없음
        modifier = modifier
    )
}
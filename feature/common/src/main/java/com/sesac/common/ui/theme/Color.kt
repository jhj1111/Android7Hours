package com.sesac.common.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =================================================================
// 1. 기본 색상 팔레트 (Base Palette)
// =================================================================

// White & Black
val White = Color.White
val Black = Color.Black

// Gray Scale
val Gray50 = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D5DB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray700 = Color(0xFF374151)
val Gray900 = Color(0xFF1F2937)

// Purple Scale
val Purple40 = Color(0xFF6650a4)
val Purple80 = Color(0xFFD0BCFF)
val Purple100 = Color(0xFFEDE9FE)
val Purple500 = Color(0xFF6200EE) // NOTE: 8B5CF6(PurpleMain)와 중복 용도, 통합 검토 필요
val Purple600 = Color(0xFF5D3E8C)
val Purple700 = Color(0xFF6B21A8)
val PurpleMain = Color(0xFF8B5CF6)
val PurpleDark = Color(0xFF7C3AED)

// Purple Grey Scale
val PurpleGrey40 = Color(0xFF625b71)
val PurpleGrey80 = Color(0xFFCCC2DC)

// Pink Scale
val Pink40 = Color(0xFF7D5260)
val Pink80 = Color(0xFFEFB8C8)
val ColorPink = Color(0xFFEC4899)

// Red Scale
val RedMain = Color(0xFFEF4444)
val Red500 = RedMain // NOTE: RedMain과 동일, 통합 검토 필요

// Orange Scale
val ColorOrange = Color(0xFFF97316)

// Yellow Scale
val Yellow100 = Color(0xFFFEE500)
val Yellow200 = Color(0xFFFDE047) // 추가: 밝은 노란색
val Yellow400 = Color(0xFFFACC15)
val Yellow500 = Color(0xFFEAB308) // 추가: 표준 노란색
val Yellow600 = Color(0xFFCA8A04) // 추가: 진한 노란색

// Green Scale
val GreenMain = Color(0xFF22C55E)
val Green100 = Color(0xFFDCFCE7) // 추가: 매우 밝은 녹색
val Green200 = Color(0xFFBBF7D0) // 추가: 밝은 녹색
val Green500 = Color(0xFF10B981) // 추가: 표준 녹색
val Green600 = Color(0xFF059669) // 추가: 진한 녹색

// Blue Scale
val BlueMain = Color(0xFF3B82F6)
val Blue100 = Color(0xFFDBEAFE) // 추가: 매우 밝은 파란색
val Blue200 = Color(0xFFBFDBFE)
val Blue500 = Color(0xFF3B82F6) // 추가: 표준 파란색 (BlueMain과 동일)
val Blue600 = Color(0xFF2563EB) // 추가: 진한 파란색
val Blue700 = Color(0xFF1D4ED8)
val Blue800 = Color(0xFF1E3A8A)

// =================================================================
// 2. Material 3 호환 색상 (Material 3 Colors)
// =================================================================

// NOTE: Material 3 테마 템플릿 호환을 위한 색상
// 현재 사용하지 않는다면 제거 검토 필요
val Purple80Material = Purple80
val PurpleGrey80Material = PurpleGrey80
val Pink80Material = Pink80

val Purple40Material = Purple40
val PurpleGrey40Material = PurpleGrey40
val Pink40Material = Pink40

// =================================================================
// 3. 브랜드 색상 (Brand Colors)
// =================================================================

// Primary Colors
val Primary = PurpleMain
val PrimaryPurple = PurpleDark
val PrimaryPurpleLight = Color(0xFFF5F3FF)

// Primary Green Variations
val PrimaryGreenLight = Color(0xFFDBE8CC)
val PrimaryGreenMedium = Color(0xFFB8D4A8)
val PrimaryGreenDark = Color(0xFF2C4A6E)

// Accent Colors
val AccentGreen = GreenMain // NOTE: GreenMain과 동일, 통합 검토 필요
val AccentBlue = BlueMain // 추가: 파란색 액센트
val AccentOrange = ColorOrange // 추가: 오렌지 액센트

// Legacy Color Group (하위 호환)
// NOTE: 메인 색상과 중복, 점진적 제거 검토 필요
val ColorGreen = GreenMain
val ColorBlue = BlueMain
val ColorPurple = PurpleMain

// =================================================================
// 4. 배경 및 표면 색상 (Background & Surface)
// =================================================================

val Background = Gray50
val Surface = White
val Header = PrimaryGreenLight // NOTE: PrimaryGreenLight와 동일, 통합 검토 필요

// =================================================================
// 5. 텍스트 색상 (Text Colors)
// =================================================================

val TextPrimary = Gray900
val TextSecondary = Gray500
val TextDisabled = Gray400
val TextOnPrimary = White
val GrayTabText = Color(0xFF4B5563)

// =================================================================
// 6. UI 요소 색상 (UI Element Colors)
// =================================================================

// Icons & Borders
val Icon = Color(0xFF4B5563)
val Border = Gray200
val Indicator = Gray300

// Buttons
val ButtonSecondary = Gray100
val OnButtonSecondary = Gray700

// Other UI Elements
val SheetHandle = Gray300
val NoteBox = Gray100

// =================================================================
// 7. 상태 색상 (Status Colors)
// =================================================================

val Error = RedMain
val OnError = Color(0xFFFEE2E2)
val Success = GreenMain // 추가: 성공 상태
val Warning = Yellow500 // 추가: 경고 상태
val Info = BlueMain // 추가: 정보 상태

// Point Colors
val star = Color(0xFFF59E0B)

// =================================================================
// 8. 기능별 색상 (Feature Specific Colors)
// =================================================================

// Permission (권한)
val permEnabledBorder = Color(0xFFD8B4FE)
val permEnabledBg = Color(0xFFF5F3FF)
val badgeEnabledBg = Color(0xFFDCFCE7)
val badgeEnabledText = Color(0xFF166534)

// Info Box (안내 박스)
val infoBoxBg = Color(0xFFEFF6FF)
val infoBoxBorder = Color(0xFFBFDBFE)
val infoBoxTitle = Color(0xFF1E3A8A)
val infoBoxText = Color(0xFF1D4ED8)

// Container (컨테이너)
val primaryContainer = Color(0xFFEDE9FE)
val OnPrimaryContainer = Color(0xFF6D28D9)

// =================================================================
// 9. Light 변형 색상 (Light Variations)
// =================================================================

val LightPurple = Color(0xFFEBE5FF)
val LightBlue = Color(0xFFE0F3FF)
val LightBlue2 = Color(0xFFEFF5FF)
val LightGreen = Color(0xFFE0FFE8)
val LightGray = Color(0xFFEEF0F2)
val LightPink = Color(0xFFFDF0F3)
val LightYellow = Color(0xFFFEF9C3) // 추가: 밝은 노란색 배경

// =================================================================
// 10. 그라데이션 (Gradients)
// =================================================================

// Brush - Color Gradients
val brushPurple = Brush.verticalGradient(listOf(Color(0xFFA855F7), PurpleMain))
val brushBlue = Brush.verticalGradient(listOf(Color(0xFF60A5FA), BlueMain))
val brushGreen = Brush.verticalGradient(listOf(Color(0xFF4ADE80), GreenMain))
val brushYellow = Brush.verticalGradient(listOf(Color(0xFFFCD34D), Yellow400)) // 추가: 노란색 그라데이션

// Stat Graph Brushes
val StatPurple = brushPurple
val StatBlue = brushBlue
val StatGreen = brushGreen
val StatYellow = brushYellow // 추가: 노란색 통계 그래프
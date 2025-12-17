package com.sesac.common.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// =================================================================
// 1. Spacing & Padding (여백)
// =================================================================
val paddingNone = 0.dp
val paddingMicro = 4.dp
val paddingSmall = 8.dp
val paddingMedium = 12.dp
val paddingLarge = 16.dp
val paddingExtraLarge = 48.dp

// Section Padding (Naming Exception 유지)
val PaddingSection = 24.dp


// =================================================================
// 2. Icon Sizes (아이콘 크기)
// =================================================================
val iconSizeSmall = 16.dp
val iconSizeMedium = 20.dp
val iconSize = 28.dp       // Standard (w-7 h-7)
val iconSizeLarge = 48.dp
val iconSizeExtremeLarge = 120.dp
val iconPhotoSize = 36.dp
val iconPhotoInnerSize = 20.dp

// Specific Icons
val cardIconSize = 40.dp
val iconBoxSize = 56.dp    // Container size (w-14 h-14)


// =================================================================
// 3. Avatar & Profile (프로필 이미지)
// =================================================================
val avatarSizeMedium = 40.dp
val avatarSizeLarge = 80.dp

// Alias (기존 코드 호환용)
val avatarSize = avatarSizeMedium


// =================================================================
// 4. Component Sizes (컴포넌트 크기)
// =================================================================
// Header & Banner
val headerHeight = 64.dp
val bannerHeight = 192.dp

// Cards
val cardWidth = 256.dp
val cardHeightSmall = 125.dp
val cardHeight = 160.dp
val cardImageHeight = 256.dp
val cardImageSizeMicro = 64.dp
val cardImageSizeSmall = 80.dp
val cardImageSize = 96.dp
val postImageHeight = 192.dp

// Buttons & Handle
val buttonHeightMedium = 56.dp
val circularButtonSize = 80.dp
val SheetMinHeight = 240.dp
val SheetHandleWidth = 40.dp
val SheetHandleHeight = 4.dp
val bottomSheetMaxHeight = 240.dp


// =================================================================
// 5. Corner Radius (둥근 모서리 값)
// =================================================================
val buttonRound = 8.dp
val cardRound = 12.dp


// =================================================================
// 6. Shapes (실제 적용되는 모양 객체)
// =================================================================
// Tip: 위에서 정의한 Radius 변수를 재사용하여 일관성 유지
val buttonShape = RoundedCornerShape(buttonRound) // 8.dp
val cardShape = RoundedCornerShape(cardRound)     // 12.dp


// =================================================================
// 7. Elevation (그림자 높이)
// =================================================================
val elevationSmall = 2.dp
val elevationMedium = 4.dp
val elevationLarge = 8.dp

// =================================================================
// 8. Border (테두리 패딩)
// =================================================================

val borderNone = 0.dp
val borderMicro = 1.dp
val borderSmall = 4.dp
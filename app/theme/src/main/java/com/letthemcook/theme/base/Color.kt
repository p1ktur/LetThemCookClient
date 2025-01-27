package com.letthemcook.theme.base

import androidx.compose.ui.graphics.Color

// Own Theme 0 1 2 3 4 5 6 7 8 9 A B C D E F

val screenZeroLight = Color(0xFFDFFFEF) // 0xFFD8A8B8
val screenOneLight = Color(0xFFDFFFEB) // 0xFFE0B0C0
val screenOneDimmedLight = screenOneLight.run { copy(1f, red * 0.55f, green * 0.55f, blue * 0.55f) }
val screenTwoLight = Color(0xFFDBF8E7) // 0xFFE8B8C8
val screenThreeLight = Color(0xFFD7EFE3) // 0xFFF0C0D0
val backgroundLight = Color(0xFFF8FFF8)
val dividerLight = Color(0xFF264D33)
val textLight = Color(0xFF264D33)
val textInverseLight = Color(0xFFD7EFE3)
val textDimmedLight = Color(0xFF264D33)
val textDimmedInverseLight = Color(0xFF517F5F)
val containerLight = Color(0xFFEAFDED) // 0xFFFBDCE3
val highlightColorLight = Color(0xFF7788FF)
val badHighlightColorLight = Color(0xFFFF8877)
val warningHighlightColorLight = Color(0xFFDDFF88)
val goodHighlightColorLight = Color(0xFF88FF77)
val canvasBackgroundLight = Color(0xFFF0F8F0)
val canvasGridLight = Color(0xFF779988)
val errorTextLight = Color(0xFFAD2623)

val screenZeroDark = Color(0xFF4F6F5F)
val screenOneDark = Color(0xFF486858)
val screenOneDimmedDark = screenOneDark.run { copy(1f, red * 0.55f, green * 0.55f, blue * 0.55f) }
val screenTwoDark = Color(0xFF3F5F4F)
val screenThreeDark = Color(0xFF385848)
val backgroundDark = Color(0xFF000800)
val dividerDark = Color(0xFF517F5F)
val textDark = Color(0xFFD7EFE3)
val textInverseDark = Color(0xFF264D33)
val textDimmedDark = Color(0xFF517F5F)
val textDimmedInverseDark = Color(0xFF264D33)
val containerDark = Color(0xFF628776)
val highlightColorDark = Color(0xFF7788DD)
val badHighlightColorDark = Color(0xFFDD8877)
val warningHighlightColorDark = Color(0xFFDDDD77)
val goodHighlightColorDark = Color(0xFF88FF77)
val canvasBackgroundDark = Color(0xFF779988)
val canvasGridDark = Color(0xFF264D33)
val errorTextDark = Color(0xFF7F1713)
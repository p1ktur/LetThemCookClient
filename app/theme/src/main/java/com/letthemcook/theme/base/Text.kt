package com.letthemcook.theme.base

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getTypography(textColor: Color): Typography {
    val defaultTextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp,
        color = textColor
    )

    return Typography(
        labelSmall = defaultTextStyle.copy(
            fontSize = 8.sp,
            lineHeight = 12.sp
        ),
        labelMedium = defaultTextStyle.copy(
            fontSize = 10.sp,
            lineHeight = 14.sp
        ),
        labelLarge = defaultTextStyle.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        bodySmall = defaultTextStyle.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        bodyMedium = defaultTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = defaultTextStyle.copy(
            fontSize = 20.sp,
            lineHeight = 24.sp
        ),
        titleSmall = defaultTextStyle.copy(
            fontSize = 24.sp,
            lineHeight = 28.sp
        ),
        titleMedium = defaultTextStyle.copy(
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleLarge = defaultTextStyle.copy(
            fontSize = 40.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold
        )
    )
}